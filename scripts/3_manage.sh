#!/bin/bash
# ============================================================
#  CMS Project - 운영 관리 스크립트
#  실행: sudo bash 3_manage.sh
# ============================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# ── root 권한 확인 ──
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}[ERROR]${NC} root 권한으로 실행해주세요: sudo bash $0"
    exit 1
fi

# ── 프로젝트 선택 ──
echo "============================================================"
echo "  CMS Project - 운영 관리"
echo "============================================================"
echo ""

# /opt 아래 배포된 프로젝트 목록
PROJECTS=()
for svc in /etc/systemd/system/*.service; do
    svc_name=$(basename "$svc" .service)
    if [ -d "/opt/${svc_name}" ] && grep -q "CMS Project" "$svc" 2>/dev/null; then
        PROJECTS+=("$svc_name")
    fi
done

if [ ${#PROJECTS[@]} -eq 0 ]; then
    echo -e "${YELLOW}배포된 CMS 프로젝트가 없습니다.${NC}"
    echo "  먼저 2_deploy.sh 를 실행해주세요."
    exit 1
fi

echo "  배포된 프로젝트:"
for i in "${!PROJECTS[@]}"; do
    STATUS=$(systemctl is-active "${PROJECTS[$i]}" 2>/dev/null || echo "unknown")
    if [ "$STATUS" = "active" ]; then
        echo -e "    $((i+1)). ${PROJECTS[$i]} ${GREEN}[running]${NC}"
    else
        echo -e "    $((i+1)). ${PROJECTS[$i]} ${RED}[${STATUS}]${NC}"
    fi
done
echo ""

read -p "  프로젝트 번호 선택: " SEL
SEL=$((SEL-1))

if [ -z "${PROJECTS[$SEL]}" ]; then
    echo -e "${RED}잘못된 선택입니다.${NC}"
    exit 1
fi

PROJECT="${PROJECTS[$SEL]}"
INSTALL_DIR="/opt/${PROJECT}"
LOG_DIR="/var/log/${PROJECT}"

echo ""
echo -e "  선택된 프로젝트: ${CYAN}${PROJECT}${NC}"
echo ""

# ── 메뉴 ──
show_menu() {
    echo "------------------------------------------------------------"
    echo "  관리 메뉴"
    echo "------------------------------------------------------------"
    echo "  1) 서비스 상태 확인"
    echo "  2) 서비스 재시작"
    echo "  3) 서비스 중지"
    echo "  4) 서비스 시작"
    echo "  5) 로그 보기 (실시간)"
    echo "  6) 코드 업데이트 & 재배포"
    echo "  7) Frontend만 재빌드"
    echo "  8) Backend만 재빌드"
    echo "  9) DB 비밀번호 변경"
    echo "  0) 종료"
    echo "------------------------------------------------------------"
}

# ── 코드 업데이트 & 재배포 ──
redeploy() {
    echo -e "${BLUE}[STEP]${NC} 코드 업데이트 시작"

    cd "$INSTALL_DIR"
    git pull

    echo -e "${BLUE}[STEP]${NC} Backend 빌드"
    ./gradlew clean build -x test --no-daemon

    echo -e "${BLUE}[STEP]${NC} Frontend 빌드"
    cd "${INSTALL_DIR}/frontend"
    npm install
    npm run build

    echo -e "${BLUE}[STEP]${NC} 서비스 재시작"
    chown -R www-data:www-data "$INSTALL_DIR"
    systemctl restart "$PROJECT"
    systemctl reload nginx

    echo -e "${GREEN}[OK]${NC} 재배포 완료"
}

# ── Frontend만 재빌드 ──
rebuild_frontend() {
    echo -e "${BLUE}[STEP]${NC} Frontend 재빌드"

    cd "${INSTALL_DIR}/frontend"
    npm install
    npm run build
    chown -R www-data:www-data "${INSTALL_DIR}/frontend/dist"

    echo -e "${GREEN}[OK]${NC} Frontend 빌드 완료 (Nginx 자동 반영)"
}

# ── Backend만 재빌드 ──
rebuild_backend() {
    echo -e "${BLUE}[STEP]${NC} Backend 재빌드"

    cd "$INSTALL_DIR"
    ./gradlew clean build -x test --no-daemon
    chown -R www-data:www-data "$INSTALL_DIR"

    echo -e "${BLUE}[STEP]${NC} 서비스 재시작"
    systemctl restart "$PROJECT"

    echo -e "${GREEN}[OK]${NC} Backend 재빌드 및 재시작 완료"
}

# ── DB 비밀번호 변경 ──
change_db_password() {
    read -sp "  새 DB 비밀번호: " NEW_PW
    echo ""
    read -sp "  비밀번호 확인: " NEW_PW2
    echo ""

    if [ "$NEW_PW" != "$NEW_PW2" ]; then
        echo -e "${RED}비밀번호가 일치하지 않습니다.${NC}"
        return
    fi

    # MariaDB 비밀번호 변경
    mariadb -u root -e "ALTER USER '${PROJECT}'@'localhost' IDENTIFIED BY '${NEW_PW}'; FLUSH PRIVILEGES;"

    # application.yaml 업데이트
    APP_YAML="${INSTALL_DIR}/src/main/resources/application.yaml"
    sed -i "s|password: .*|password: ${NEW_PW}|" "$APP_YAML"

    # 서비스 재시작
    systemctl restart "$PROJECT"

    echo -e "${GREEN}[OK]${NC} DB 비밀번호 변경 및 서비스 재시작 완료"
}

# ── 메인 루프 ──
while true; do
    show_menu
    read -p "  선택: " CHOICE
    echo ""

    case $CHOICE in
        1)
            systemctl status "$PROJECT" --no-pager
            ;;
        2)
            systemctl restart "$PROJECT"
            echo -e "${GREEN}[OK]${NC} 서비스 재시작 완료"
            ;;
        3)
            systemctl stop "$PROJECT"
            echo -e "${YELLOW}[OK]${NC} 서비스 중지"
            ;;
        4)
            systemctl start "$PROJECT"
            echo -e "${GREEN}[OK]${NC} 서비스 시작"
            ;;
        5)
            echo "  Ctrl+C 로 로그 보기 종료"
            journalctl -u "$PROJECT" -f
            ;;
        6)
            redeploy
            ;;
        7)
            rebuild_frontend
            ;;
        8)
            rebuild_backend
            ;;
        9)
            change_db_password
            ;;
        0)
            echo "종료합니다."
            exit 0
            ;;
        *)
            echo -e "${RED}잘못된 선택입니다.${NC}"
            ;;
    esac
    echo ""
done
