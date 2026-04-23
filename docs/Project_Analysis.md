# 프로젝트 분석 보고서

## 1. 프로젝트 설명
본 프로젝트는 프로그램(서비스/대관) 예약 및 상품 판매, 커뮤니티(게시판), 결제 시스템 등을 통합 제공하는 **통합 웹 CMS (Content Management System) 및 커머스 플랫폼**입니다.
메인 서비스로는 관리자 페이지를 통한 사이트 환경(정보, 위치 등) 설정, 사용자 권한 관리, 게시판 관리, 상품 등록 및 관리를 지원하며, 사용자 페이지에서는 프로그램 예약, 상품 장바구니/구매 기능, 오프라인 프로그램 검색, 게시판 조회 및 작성 기능 등을 제공합니다. `docs/legacy/Project_Detail.md`의 명세에 따라, 인원 및 시간 단위의 예약 제한 기능, 다양한 결제 시스템(NHN KCP, 카카오페이, 토스페이 등) 연결도 기획 및 구현되어 있습니다.

## 2. 사용기술

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 4.0.1
- **Database**: MariaDB (운영), H2 Database (개발/테스트용)
- **Data Access**: Spring Data JPA, Spring JDBC
- **Security**: Spring Security, Spring OAuth2 Client, JJWT (JSON Web Token 0.11.5)
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Build Tool**: Gradle

### Frontend
- **Framework/Library**: React 19.2.0, Vite 7.3.1
- **Language**: TypeScript 5.9.3
- **Routing**: React Router DOM 7.13.0
- **Editor/UI Components**: 
  - Quill, React-Quill-New (리치 텍스트 에디터 기능)
  - Monaco Editor (코드/텍스트 뷰어 컴포넌트)
  - Lucide React (모던 아이콘 제공)
- **Style**: Standard CSS (각 컴포넌트/페이지 단위 `.css` 매핑)

## 3. 프로젝트 폴더 구조
프로젝트는 크게 백엔드 API 서버를 담당하는 메인 디렉토리(Java/Spring)와 프론트엔드 뷰를 담당하는 `frontend` 디렉토리로 구성된 풀스택 모노레포 형태의 구조를 가지고 있습니다.

```text
cmsproject/
├── build.gradle            # 백엔드(Spring Boot) 빌드 및 의존성 설정 파일
├── docs/                   # 프로젝트 관련 문서 디렉토리
│   └── legacy/             # (신규 생성) 기존 요구사항 및 명세 파일 보관소
├── frontend/               # 프론트엔드 (React) 루트 디렉토리
│   ├── package.json        # 프론트엔드 패키지 정보 및 실행 스크립트
│   └── src/                # 프론트엔드 소스 코드
│       ├── assets/         # 정적 리소스 (이미지, 폰트 등)
│       ├── components/     # 재사용 가능한 공통 UI 컴포넌트
│       ├── contexts/       # React Context API (전역 상태, 테마 등)
│       ├── layouts/        # 웹 페이지의 기본 뼈대(Header, Footer, Admin, 등)
│       └── pages/          # 라우팅 되는 개별 페이지 컴포넌트들
└── src/                    # 백엔드 (Spring Boot) 루트 디렉토리
    └── main/
        ├── java/com/lhsdev/cmsproject/
        │   ├── api/          # 외부 API 연동 관련 로직 (결제 모듈 등)
        │   ├── config/       # Spring Security, Swagger, Cors 등의 설정
        │   ├── controller/   # 클라이언트의 REST API 요청을 처리하는 계층
        │   ├── domain/       # 도메인 모델 (핵심 비즈니스 흐름 담당)
        │   ├── dto/          # 데이터 전송 객체 (Request / Response)
        │   ├── entity/       # DB 테이블 매핑용 JPA Entity 클래스 모음
        │   ├── interceptor/  # HTTP 요청/응답 시 처리될 공통 로직 (인증, 로깅)
        │   ├── repository/   # DB 쿼리 및 접근을 수행하는 계층
        │   └── service/      # 실제 서버의 비즈니스 로직 수행 계층
        └── resources/        # DB 접속 정보, 보안 키 등 (application.yml)
```

## 4. 폴더 내부 파일 설명

### Backend (`src/main/java/com/lhsdev/cmsproject/`)
- **`controller/`**: 브라우저나 프론트엔드 단의 HTTP(GET, POST 등) 요청을 가장 먼저 받아 필요한 서비스 레이어로 연결해 주는 엔드포인트 파일들이 존재합니다.
- **`service/`**: 컨트롤러에서 받은 데이터와 요청을 바탕으로 핵심 비즈니스 로직(데이터 가공, 가격 계산, 예외 체크 등) 처리를 담당하는 클래스들이 위치합니다.
- **`repository/`**: DB의 각 엔티티에 접근하여 데이터를 저장, 조회, 수정, 삭제(CRUD)하기 위한 인터페이스 파일들입니다. Data JPA를 상속받습니다.
- **`entity/`**: User, Board, Product, SiteSetting 등 DB의 실제 테이블 구조와 직접 1:1로 매핑되는 클래스들이 위치합니다.
- **`dto/`**: 외부와 통신할 때 Entity 자체를 노출시키지 않고 필요한 데이터만 담아 넘겨주거나 받기 위한 캡슐화 포장 파일(record나 클래스 형태)들이 정의됩니다.
- **`config/`**: 데이터베이스 접속 정보 외에, Spring Security를 통한 권한(Admin/Member 등) 분리, OAuth2 설정, JWT 발급 세팅, Swagger 연동 등 전체적인 시스템 환경의 설정을 담당하는 파일들입니다.

### Frontend (`frontend/src/`)
- **`pages/`**: 사용자가 직접 보게 되는 주요 웹 페이지(화면)들의 모음입니다. 화면에 대응되며 각 파일들은 컴포넌트/레이아웃을 조합하여 로직을 구성합니다.
  - `admin/`: 관리자 대시보드 및 각 메뉴(제품, 사용자, 로케이션 설정 등)의 설정을 지원하는 관리자 특화 페이지 폴더
  - `Home.tsx` / `About.tsx` / `Location.tsx`: 서비스 소개 및 랜딩, 오시는 길 안내 페이지
  - `ProductList.tsx` / `ProductDetail.tsx`: 상품 리스트 및 단일 상품에 대한 세부 내용을 보여주는 페이지
  - `Board*.tsx`: 자유게시판/공지사항 등의 목록(List), 상세(View), 작성(Write) 파일.
  - `Login.tsx` / `Cart.tsx` / `MyPage.tsx`: 소셜/일반 로그인 기능 구현과 사용자 장바구니, 예약 정보 확인용 마이페이지
- **`components/`**: 모달(Modal), 버튼, 입력 폼 등 다양한 화면에서 공통으로 사용될 UI 요소들이 분리된 단독 파일들입니다.
- **`layouts/`**: 웹사이트에서 변하지 않는 상단 네비게이션, 하단 푸터, 혹 은 관리자 페이지의 사이드바처럼 공통적으로 입혀야 할 뼈대 템플릿 파일이 정의되어 있습니다.
