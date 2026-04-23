#!/bin/bash
# ============================================================
#  CMS Project - 프로젝트 배포 스크립트
#  대상: Ubuntu (Linux) - 1_install.sh 실행 후 사용
#  실행: sudo bash 2_deploy.sh
# ============================================================

set -e

# ── 색상 ──
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_step()  { echo -e "\n${BLUE}[STEP]${NC} $1"; }
print_ok()    { echo -e "${GREEN}[OK]${NC} $1"; }
print_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }
print_input() { echo -e "${CYAN}[INPUT]${NC} $1"; }

# ── root 권한 확인 ──
if [ "$EUID" -ne 0 ]; then
    print_error "root 권한으로 실행해주세요: sudo bash $0"
    exit 1
fi

echo "============================================================"
echo "  CMS Project - 신규 사이트 배포"
echo "============================================================"

# ============================================================
#  사용자 입력 수집
# ============================================================
echo ""
print_input "사이트 정보를 입력해주세요."
echo ""

# 프로젝트명
read -p "  프로젝트명 (예: mysite): " PROJECT_NAME
if [ -z "$PROJECT_NAME" ]; then
    print_error "프로젝트명을 입력해주세요."
    exit 1
fi

# 도메인
read -p "  도메인 (예: example.com): " DOMAIN
if [ -z "$DOMAIN" ]; then
    print_error "도메인을 입력해주세요."
    exit 1
fi

# DB 비밀번호
read -sp "  DB 비밀번호: " DB_PASSWORD
echo ""
if [ -z "$DB_PASSWORD" ]; then
    print_error "DB 비밀번호를 입력해주세요."
    exit 1
fi

# 백엔드 포트
read -p "  백엔드 포트 (기본 8080): " BACKEND_PORT
BACKEND_PORT=${BACKEND_PORT:-8080}

# Git 리포지토리
read -p "  Git 리포지토리 URL: " GIT_REPO
if [ -z "$GIT_REPO" ]; then
    print_error "Git 리포지토리 URL을 입력해주세요."
    exit 1
fi

# HTTPS 설정 여부
read -p "  HTTPS 설정? (y/N): " SETUP_HTTPS
SETUP_HTTPS=${SETUP_HTTPS:-N}

# ── 경로 설정 ──
INSTALL_DIR="/opt/${PROJECT_NAME}"
DB_NAME="${PROJECT_NAME}"
DB_USER="${PROJECT_NAME}"
SERVICE_NAME="${PROJECT_NAME}"
LOG_DIR="/var/log/${PROJECT_NAME}"

echo ""
echo "------------------------------------------------------------"
echo "  배포 설정 확인"
echo "------------------------------------------------------------"
echo "  프로젝트명  : ${PROJECT_NAME}"
echo "  도메인      : ${DOMAIN}"
echo "  설치 경로   : ${INSTALL_DIR}"
echo "  DB 이름     : ${DB_NAME}"
echo "  DB 사용자   : ${DB_USER}"
echo "  백엔드 포트 : ${BACKEND_PORT}"
echo "  서비스명    : ${SERVICE_NAME}"
echo "  HTTPS       : ${SETUP_HTTPS}"
echo "------------------------------------------------------------"
read -p "  진행하시겠습니까? (Y/n): " CONFIRM
CONFIRM=${CONFIRM:-Y}
if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
    echo "배포를 취소합니다."
    exit 0
fi

# ============================================================
#  1. 데이터베이스 생성
# ============================================================
print_step "MariaDB 데이터베이스 생성 (${DB_NAME})"

mariadb -u root <<EOF
CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF

print_ok "데이터베이스 '${DB_NAME}' / 사용자 '${DB_USER}' 생성 완료"

# ============================================================
#  2. 프로젝트 클론
# ============================================================
print_step "프로젝트 클론 → ${INSTALL_DIR}"

if [ -d "$INSTALL_DIR" ]; then
    print_warn "디렉토리가 이미 존재합니다: ${INSTALL_DIR}"
    read -p "  기존 디렉토리를 삭제하고 다시 클론? (y/N): " RECLONE
    if [[ "$RECLONE" =~ ^[Yy]$ ]]; then
        rm -rf "$INSTALL_DIR"
        git clone "$GIT_REPO" "$INSTALL_DIR"
    else
        cd "$INSTALL_DIR"
        git pull
    fi
else
    git clone "$GIT_REPO" "$INSTALL_DIR"
fi

print_ok "프로젝트 클론 완료"

# ============================================================
#  3. application.yaml 설정
# ============================================================
print_step "application.yaml DB 설정 변경"

APP_YAML="${INSTALL_DIR}/src/main/resources/application.yaml"

# DB 접속 정보 변경
sed -i "s|url: jdbc:mariadb://localhost:3306/.*|url: jdbc:mariadb://localhost:3306/${DB_NAME}|" "$APP_YAML"
sed -i "s|username: .*|username: ${DB_USER}|" "$APP_YAML"
sed -i "s|password: .*|password: ${DB_PASSWORD}|" "$APP_YAML"

# 업로드 경로 변경
sed -i "s|dir: uploads/|dir: ${INSTALL_DIR}/uploads/|" "$APP_YAML"

print_ok "application.yaml 수정 완료"

# ── 업로드 디렉토리 생성 ──
mkdir -p "${INSTALL_DIR}/uploads"
chown -R www-data:www-data "${INSTALL_DIR}/uploads"
chmod 755 "${INSTALL_DIR}/uploads"

# ── 로그 디렉토리 생성 ──
mkdir -p "$LOG_DIR"
chown -R www-data:www-data "$LOG_DIR"

# ============================================================
#  4. Backend 빌드
# ============================================================
print_step "Backend 빌드 (Gradle)"

cd "$INSTALL_DIR"
chmod +x gradlew
./gradlew clean build -x test --no-daemon

JAR_FILE=$(ls build/libs/*.jar | grep -v plain | head -1)
if [ -z "$JAR_FILE" ]; then
    print_error "JAR 파일을 찾을 수 없습니다."
    exit 1
fi
print_ok "Backend 빌드 완료: ${JAR_FILE}"

# ============================================================
#  5. Frontend 빌드
# ============================================================
print_step "Frontend 빌드 (Vite)"

cd "${INSTALL_DIR}/frontend"
npm install
npm run build

if [ ! -d "dist" ]; then
    print_error "Frontend 빌드 실패: dist 폴더 없음"
    exit 1
fi
print_ok "Frontend 빌드 완료"

# ============================================================
#  6. systemd 서비스 등록
# ============================================================
print_step "systemd 서비스 등록 (${SERVICE_NAME})"

cat > /etc/systemd/system/${SERVICE_NAME}.service <<EOF
[Unit]
Description=CMS Project - ${PROJECT_NAME}
After=network.target mariadb.service

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=${INSTALL_DIR}
ExecStart=/usr/bin/java -jar ${INSTALL_DIR}/${JAR_FILE} --server.port=${BACKEND_PORT}
Restart=on-failure
RestartSec=10
StandardOutput=append:${LOG_DIR}/app.log
StandardError=append:${LOG_DIR}/error.log

[Install]
WantedBy=multi-user.target
EOF

# 프로젝트 디렉토리 소유권 설정
chown -R www-data:www-data "$INSTALL_DIR"

systemctl daemon-reload
systemctl enable "$SERVICE_NAME"
systemctl start "$SERVICE_NAME"

print_ok "서비스 '${SERVICE_NAME}' 등록 및 시작 완료"

# ============================================================
#  7. Nginx 설정
# ============================================================
print_step "Nginx 설정 (${DOMAIN})"

cat > /etc/nginx/sites-available/${PROJECT_NAME} <<EOF
server {
    listen 80;
    server_name ${DOMAIN};

    client_max_body_size 100M;

    # Frontend
    root ${INSTALL_DIR}/frontend/dist;
    index index.html;

    # React SPA
    location / {
        try_files \$uri \$uri/ /index.html;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:${BACKEND_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_read_timeout 300s;
    }

    # OAuth2
    location /oauth2/ {
        proxy_pass http://localhost:${BACKEND_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location /login/oauth2/ {
        proxy_pass http://localhost:${BACKEND_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Swagger UI
    location /swagger-ui/ {
        proxy_pass http://localhost:${BACKEND_PORT};
        proxy_set_header Host \$host;
    }

    location /v3/api-docs {
        proxy_pass http://localhost:${BACKEND_PORT};
        proxy_set_header Host \$host;
    }

    # 업로드 파일
    location /uploads/ {
        alias ${INSTALL_DIR}/uploads/;
    }
}
EOF

# 심볼릭 링크 생성
ln -sf /etc/nginx/sites-available/${PROJECT_NAME} /etc/nginx/sites-enabled/${PROJECT_NAME}

# Nginx 설정 검증 및 재시작
nginx -t
systemctl reload nginx

print_ok "Nginx 설정 완료 (${DOMAIN})"

# ============================================================
#  8. HTTPS (선택)
# ============================================================
if [[ "$SETUP_HTTPS" =~ ^[Yy]$ ]]; then
    print_step "HTTPS 인증서 발급 (Let's Encrypt)"

    read -p "  이메일 주소 (인증서 알림용): " CERT_EMAIL
    if [ -z "$CERT_EMAIL" ]; then
        print_warn "이메일 미입력 - HTTPS 설정 건너뜀"
    else
        certbot --nginx -d "$DOMAIN" --non-interactive --agree-tos -m "$CERT_EMAIL"
        print_ok "HTTPS 인증서 발급 완료"
    fi
fi

# ============================================================
#  배포 완료
# ============================================================
echo ""
echo "============================================================"
echo -e "  ${GREEN}배포 완료!${NC}"
echo "============================================================"
echo ""
echo "  사이트       : http://${DOMAIN}"
echo "  Swagger UI   : http://${DOMAIN}/swagger-ui.html"
echo "  설치 경로    : ${INSTALL_DIR}"
echo "  로그         : ${LOG_DIR}/app.log"
echo ""
echo "  서비스 관리:"
echo "    sudo systemctl status  ${SERVICE_NAME}"
echo "    sudo systemctl restart ${SERVICE_NAME}"
echo "    sudo systemctl stop    ${SERVICE_NAME}"
echo "    sudo journalctl -u ${SERVICE_NAME} -f"
echo ""
echo "============================================================"
