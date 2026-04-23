#!/bin/bash
# ============================================================
#  CMS Project - 서버 환경 설치 스크립트
#  대상: Ubuntu 20.04 / 22.04 / 24.04 (Linux)
#  실행: sudo bash 1_install.sh
# ============================================================

set -e

# ── 색상 ──
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_step()  { echo -e "\n${BLUE}[STEP]${NC} $1"; }
print_ok()    { echo -e "${GREEN}[OK]${NC} $1"; }
print_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# ── root 권한 확인 ──
if [ "$EUID" -ne 0 ]; then
    print_error "root 권한으로 실행해주세요: sudo bash $0"
    exit 1
fi

echo "============================================================"
echo "  CMS Project - 서버 환경 설치"
echo "  Java 17 / Node.js 18 / MariaDB / Nginx"
echo "============================================================"

# ── 패키지 업데이트 ──
print_step "시스템 패키지 업데이트"
apt update -y && apt upgrade -y
print_ok "패키지 업데이트 완료"

# ============================================================
#  1. Java 17
# ============================================================
print_step "Java 17 설치"
if java -version 2>&1 | grep -q "17"; then
    print_warn "Java 17 이미 설치됨 - 건너뜀"
else
    apt install -y openjdk-17-jdk
    print_ok "Java 17 설치 완료"
fi
java -version 2>&1 | head -1

# ============================================================
#  2. Node.js 18 (NodeSource)
# ============================================================
print_step "Node.js 18 설치"
if command -v node &> /dev/null && node -v | grep -q "v18"; then
    print_warn "Node.js 18 이미 설치됨 - 건너뜀"
else
    # NodeSource 저장소 추가
    apt install -y ca-certificates curl gnupg
    mkdir -p /etc/apt/keyrings
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_18.x nodistro main" > /etc/apt/sources.list.d/nodesource.list
    apt update -y
    apt install -y nodejs
    print_ok "Node.js 설치 완료"
fi
echo "Node: $(node -v) / npm: $(npm -v)"

# ============================================================
#  3. MariaDB
# ============================================================
print_step "MariaDB 설치"
if command -v mariadb &> /dev/null; then
    print_warn "MariaDB 이미 설치됨 - 건너뜀"
else
    apt install -y mariadb-server mariadb-client
    systemctl start mariadb
    systemctl enable mariadb
    print_ok "MariaDB 설치 완료"
fi

# ============================================================
#  4. Nginx
# ============================================================
print_step "Nginx 설치"
if command -v nginx &> /dev/null; then
    print_warn "Nginx 이미 설치됨 - 건너뜀"
else
    apt install -y nginx
    systemctl start nginx
    systemctl enable nginx
    print_ok "Nginx 설치 완료"
fi

# ============================================================
#  5. Certbot (HTTPS용)
# ============================================================
print_step "Certbot 설치 (SSL 인증서용)"
if command -v certbot &> /dev/null; then
    print_warn "Certbot 이미 설치됨 - 건너뜀"
else
    apt install -y certbot python3-certbot-nginx
    print_ok "Certbot 설치 완료"
fi

# ============================================================
#  6. 기타 유틸리티
# ============================================================
print_step "기타 유틸리티 설치 (git, unzip)"
apt install -y git unzip
print_ok "유틸리티 설치 완료"

# ============================================================
#  설치 결과 요약
# ============================================================
echo ""
echo "============================================================"
echo -e "  ${GREEN}서버 환경 설치 완료${NC}"
echo "============================================================"
echo ""
echo "  설치된 항목:"
echo "  - Java    : $(java -version 2>&1 | head -1)"
echo "  - Node.js : $(node -v)"
echo "  - npm     : $(npm -v)"
echo "  - MariaDB : $(mariadb --version 2>/dev/null || echo 'installed')"
echo "  - Nginx   : $(nginx -v 2>&1)"
echo "  - Certbot : $(certbot --version 2>&1)"
echo ""
echo "  다음 단계: bash 2_deploy.sh 실행"
echo "============================================================"
