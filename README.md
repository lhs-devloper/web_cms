# CMS Project

통합 웹 CMS 및 커머스 플랫폼입니다.  
프로그램 예약, 상품 판매, 게시판, 결제 시스템을 통합 제공하며, **홈페이지 찍어내기식 배포**를 목표로 합니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Java 17, Spring Boot 4.0.1, Gradle |
| **Frontend** | React 19, Vite 7, TypeScript 5.9 |
| **Database** | MariaDB (운영) / H2 (개발) |
| **ORM** | Spring Data JPA |
| **인증** | Spring Security, OAuth2 (Google, Naver, Kakao, Github), JWT |
| **결제** | TossPay, KakaoPay, KCP |
| **API 문서** | Springdoc OpenAPI (Swagger UI) |

---

## 프로젝트 구조

```
cmsproject/
├── src/                          # Spring Boot 백엔드
│   └── main/
│       ├── java/com/lhsdev/cmsproject/
│       │   ├── api/              # 외부 API 연동
│       │   ├── config/           # Security, OAuth2, JWT, Swagger, CORS
│       │   ├── controller/       # REST API 엔드포인트
│       │   │   ├── admin/        # 관리자 전용 API
│       │   │   └── global/       # 공개 API
│       │   ├── domain/           # 핵심 도메인 모델
│       │   ├── dto/              # 데이터 전송 객체
│       │   ├── entity/           # JPA 엔티티
│       │   ├── repository/       # JPA 리포지토리
│       │   └── service/          # 비즈니스 로직
│       └── resources/
│           └── application.yaml  # 서버 설정
├── frontend/                     # React 프론트엔드
│   ├── src/
│   │   ├── components/           # 재사용 컴포넌트
│   │   ├── layouts/              # 레이아웃
│   │   └── pages/                # 페이지 컴포넌트
│   │       └── admin/            # 관리자 페이지
│   └── vite.config.ts
├── uploads/                      # 업로드 파일 저장소
├── build.gradle
└── settings.gradle
```

---

## 사전 요구사항

| 항목 | 버전 |
|------|------|
| Java | 17 이상 |
| Node.js | 18 이상 |
| MariaDB | 10.6 이상 |
| Nginx | 최신 안정 버전 |

---

## 배포 가이드 (한 세트 배포)

새로운 홈페이지를 한 세트로 배포하는 전체 과정입니다.

### 자동 배포 (스크립트 사용 - 권장)

`scripts/` 폴더에 자동화 스크립트가 준비되어 있습니다.

```bash
# 1. 서버에 프로젝트 클론
git clone <repository-url> /opt/cmsproject
cd /opt/cmsproject/scripts

# 2. 서버 환경 설치 (Java, Node, MariaDB, Nginx)
sudo bash 1_install.sh

# 3. 사이트 배포 (DB생성 → 빌드 → Nginx → 서비스등록 자동 처리)
sudo bash 2_deploy.sh

# 4. 운영 관리 (재배포, 재빌드, 로그 확인 등)
sudo bash 3_manage.sh
```

| 스크립트 | 용도 |
|----------|------|
| `1_install.sh` | 서버 환경 설치 (최초 1회) |
| `2_deploy.sh` | 새 사이트 배포 (사이트마다 실행) |
| `3_manage.sh` | 운영 관리 (재배포, 로그, 서비스 제어) |

---

### 수동 배포 가이드

아래는 스크립트 없이 수동으로 배포하는 경우의 절차입니다.

### 1단계: 서버 환경 준비

```bash
# Java 17 설치
sudo apt update
sudo apt install openjdk-17-jdk -y

# Node.js 설치 (nvm 권장)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18

# MariaDB 설치
sudo apt install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl enable mariadb

# Nginx 설치
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 2단계: 데이터베이스 설정

```bash
# MariaDB 접속
sudo mysql -u root

# 데이터베이스 및 사용자 생성
CREATE DATABASE cmsproject CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'cmsuser'@'localhost' IDENTIFIED BY '변경할비밀번호';
GRANT ALL PRIVILEGES ON cmsproject.* TO 'cmsuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3단계: 프로젝트 클론 및 설정

```bash
# 프로젝트 클론
git clone <repository-url> /opt/cmsproject
cd /opt/cmsproject
```

### 4단계: Backend 설정

`src/main/resources/application.yaml` 파일을 운영 환경에 맞게 수정합니다.

```yaml
server:
  port: 8080
  servlet:
    session:
      timeout: 1h

app:
  upload:
    dir: /opt/cmsproject/uploads/

spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/cmsproject
    username: cmsuser
    password: 변경할비밀번호
    driver-class-name: org.mariadb.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
            client-secret: YOUR_KAKAO_CLIENT_SECRET
          github:
            client-id: YOUR_GITHUB_CLIENT_ID
            client-secret: YOUR_GITHUB_CLIENT_SECRET
```

> **주의**: OAuth2 소셜 로그인이 필요 없는 경우, 해당 registration 항목을 제거하거나 dummy 값을 유지하면 됩니다.

### 5단계: Backend 빌드 및 실행

```bash
cd /opt/cmsproject

# Gradle 빌드
chmod +x gradlew
./gradlew clean build -x test

# JAR 파일 실행 (백그라운드)
nohup java -jar build/libs/cmsproject-0.0.1-SNAPSHOT.jar \
  --server.port=8080 \
  > /var/log/cmsproject/app.log 2>&1 &

# 로그 확인
tail -f /var/log/cmsproject/app.log
```

### 6단계: Frontend 빌드

```bash
cd /opt/cmsproject/frontend

# 의존성 설치
npm install

# 프로덕션 빌드
npm run build
# 빌드 결과물: frontend/dist/
```

### 7단계: Nginx 설정

`/etc/nginx/sites-available/cmsproject` 파일을 생성합니다.

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 업로드 파일 크기 제한
    client_max_body_size 100M;

    # Frontend (React 정적 파일)
    root /opt/cmsproject/frontend/dist;
    index index.html;

    # React SPA 라우팅 지원
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Backend API 프록시
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Swagger UI (필요 시)
    location /swagger-ui/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }

    location /v3/api-docs {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }

    # 업로드 파일 서빙
    location /uploads/ {
        alias /opt/cmsproject/uploads/;
    }
}
```

```bash
# 설정 활성화
sudo ln -s /etc/nginx/sites-available/cmsproject /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# 설정 검증 및 재시작
sudo nginx -t
sudo systemctl reload nginx
```

### 8단계: HTTPS 설정 (선택, 권장)

```bash
# Certbot 설치
sudo apt install certbot python3-certbot-nginx -y

# SSL 인증서 발급
sudo certbot --nginx -d your-domain.com

# 자동 갱신 확인
sudo certbot renew --dry-run
```

---

## systemd 서비스 등록 (권장)

백엔드를 시스템 서비스로 등록하면 서버 재부팅 시 자동 시작됩니다.

`/etc/systemd/system/cmsproject.service` 파일을 생성합니다.

```ini
[Unit]
Description=CMS Project Spring Boot Application
After=network.target mariadb.service

[Service]
Type=simple
User=www-data
Group=www-data
WorkingDirectory=/opt/cmsproject
ExecStart=/usr/bin/java -jar /opt/cmsproject/build/libs/cmsproject-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 등록 및 시작
sudo systemctl daemon-reload
sudo systemctl enable cmsproject
sudo systemctl start cmsproject

# 상태 확인
sudo systemctl status cmsproject

# 로그 확인
sudo journalctl -u cmsproject -f
```

---

## 배포 체크리스트

새 사이트를 찍어낼 때마다 아래 항목을 확인하세요.

- [ ] MariaDB 데이터베이스 생성 완료
- [ ] `application.yaml` DB 접속 정보 수정
- [ ] `application.yaml` OAuth2 클라이언트 정보 수정 (필요 시)
- [ ] 업로드 디렉토리 생성 및 권한 설정 (`uploads/`)
- [ ] Backend JAR 빌드 완료
- [ ] Frontend `npm run build` 완료
- [ ] Nginx 설정 파일 생성 및 도메인 설정
- [ ] HTTPS 인증서 발급 (운영 환경)
- [ ] systemd 서비스 등록 (선택)
- [ ] Swagger UI 접속 확인: `http://your-domain.com/swagger-ui.html`
- [ ] 관리자 로그인 확인

---

## 개발 환경 실행

```bash
# Backend 실행 (H2 인메모리 DB 사용 가능)
./gradlew bootRun

# Frontend 개발 서버 (별도 터미널)
cd frontend
npm install
npm run dev
# http://localhost:5173 에서 접속
# API 요청은 vite proxy로 localhost:8080에 자동 전달
```

---

## API 문서

전체 REST API 명세는 **[API_REFERENCE.md](./docs/API_REFERENCE.md)** 를 참조하세요.

운영 중인 서버에서는 Swagger UI로도 확인할 수 있습니다:
- Swagger UI: `http://your-domain.com/swagger-ui.html`
- OpenAPI JSON: `http://your-domain.com/v3/api-docs`
