# API Reference

CMS Project REST API 전체 명세서입니다.

- Base URL: `http://your-domain.com`
- 인증: JWT Bearer Token (Authorization 헤더)
- Swagger UI: `/swagger-ui.html`

---

## 목차

1. [인증 (Auth)](#인증-auth)
2. [메인 / 글로벌 설정](#메인--글로벌-설정)
3. [사용자 프로필](#사용자-프로필)
4. [상품](#상품)
5. [장바구니](#장바구니)
6. [주문](#주문)
7. [결제](#결제)
8. [게시판](#게시판)
9. [스토리](#스토리)
10. [파일 업로드](#파일-업로드)
11. [관리자 - 대시보드](#관리자---대시보드)
12. [관리자 - 회원 관리](#관리자---회원-관리)
13. [관리자 - 상품 관리](#관리자---상품-관리)
14. [관리자 - 주문 관리](#관리자---주문-관리)
15. [관리자 - 결제 설정](#관리자---결제-설정)
16. [관리자 - 배너 관리](#관리자---배너-관리)
17. [관리자 - 메뉴 관리](#관리자---메뉴-관리)
18. [관리자 - 게시판 관리](#관리자---게시판-관리)
19. [관리자 - 스토리 관리](#관리자---스토리-관리)
20. [관리자 - 소셜 로그인 설정](#관리자---소셜-로그인-설정)
21. [관리자 - 사이트 설정](#관리자---사이트-설정)
22. [관리자 - 통계](#관리자---통계)

---

## 인증 (Auth)

**Base Path:** `/api/auth`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/auth/login` | 이메일/비밀번호 로그인, JWT 토큰 반환 | X |
| POST | `/api/auth/signup` | 회원가입 | X |

**소셜 로그인 (OAuth2)**

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/oauth2/authorization/google` | Google 로그인 시작 |
| GET | `/oauth2/authorization/naver` | Naver 로그인 시작 |
| GET | `/oauth2/authorization/kakao` | Kakao 로그인 시작 |
| GET | `/oauth2/authorization/github` | Github 로그인 시작 |

---

## 메인 / 글로벌 설정

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/` | 메인 페이지 데이터 (배너, 유저 정보) | X |
| GET | `/api/login-config` | 활성화된 소셜 로그인 목록 | X |
| GET | `/api/global/config` | 전역 설정 (메뉴, 사이트 설정, 유저 정보) | X |
| GET | `/api/global/menus` | 활성 메인 메뉴 목록 | X |
| GET | `/api/global/setting` | 공개 사이트 설정 | X |
| GET | `/api/global/social/active` | 활성 소셜 로그인 프로바이더 목록 | X |
| GET | `/api/global/banners` | 활성 배너 목록 | X |

---

## 사용자 프로필

**Base Path:** `/api/profile`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/profile` | 내 프로필 조회 | O |
| POST | `/api/profile/update` | 프로필 수정 (이름, 프로필 사진) | O |
| POST | `/api/profile/password` | 비밀번호 변경 | O |

---

## 상품

**Base Path:** `/api/products`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/products` | 전체 상품 목록 조회 | X |
| GET | `/api/products/{id}` | 상품 상세 조회 | X |

---

## 장바구니

**Base Path:** `/api/cart`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/cart` | 장바구니 목록 조회 | O |
| POST | `/api/cart/add` | 장바구니에 상품 추가 | O |
| PUT | `/api/cart/{cartItemId}` | 장바구니 수량 변경 | O |
| DELETE | `/api/cart/{cartItemId}` | 장바구니 항목 삭제 | O |
| DELETE | `/api/cart/clear` | 장바구니 비우기 | O |

---

## 주문

**Base Path:** `/api/orders`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/orders` | 주문 생성 (장바구니 기반) | O |
| GET | `/api/orders` | 내 주문 목록 조회 (페이지네이션) | O |
| GET | `/api/orders/{id}` | 주문 상세 조회 | O |
| POST | `/api/orders/{id}/cancel` | 주문 취소 | O |

---

## 결제

**Base Path:** `/api/payments`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/payments/configs` | 활성 결제 수단 및 클라이언트 키 조회 | O |
| GET | `/api/payments/last-method` | 마지막 사용 결제 수단 조회 | O |
| POST | `/api/payments/{orderId}/ready` | 결제 준비 (결제 요청) | O |
| POST | `/api/payments/{orderId}/approve` | 결제 승인 | O |
| GET | `/api/payments/kakao/success` | KakaoPay 결제 완료 콜백 | - |
| GET | `/api/payments/toss/success` | TossPay 결제 완료 콜백 | - |
| GET | `/api/payments/kcp/return` | KCP 결제 완료 콜백 | - |

---

## 게시판

**Base Path:** `/api/board`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/board` | 전체 게시판 목록 | X |
| GET | `/api/board/{boardId}` | 게시판 글 목록 | X |
| GET | `/api/board/{boardId}/view/{id}` | 게시글 상세 (댓글 포함) | X |
| POST | `/api/board/{boardId}/save` | 게시글 작성 | O |
| POST | `/api/board/{boardId}/update` | 게시글 수정 | O |
| POST | `/api/board/{boardId}/delete` | 게시글 삭제 | O |
| POST | `/api/board/{boardId}/comment/save` | 댓글 작성 | O |
| POST | `/api/board/{boardId}/comment/delete` | 댓글 삭제 | O |

---

## 스토리

**Base Path:** `/api/stories`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/stories` | 공개 스토리 목록 | X |
| GET | `/api/stories/{id}` | 스토리 상세 | X |

---

## 파일 업로드

**Base Path:** `/api/files`

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/files/upload` | 파일 업로드 (multipart/form-data) | O |

---

## 관리자 - 대시보드

**Base Path:** `/api/admin`

> 관리자(ADMIN) 권한 필요

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin` | 대시보드 (세션, 방문자, 최신 게시글) |

---

## 관리자 - 회원 관리

**Base Path:** `/api/admin/user`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/user` | 회원 목록 (키워드/역할 검색) |
| GET | `/api/admin/user/search` | 회원 검색 |
| POST | `/api/admin/user/update-role` | 회원 역할 변경 |
| POST | `/api/admin/user/delete` | 회원 삭제 |

---

## 관리자 - 상품 관리

**Base Path:** `/api/admin/products`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/products` | 전체 상품 목록 |
| GET | `/api/admin/products/{id}` | 상품 상세 |
| POST | `/api/admin/products` | 상품 등록 |
| PUT | `/api/admin/products/{id}` | 상품 수정 |
| DELETE | `/api/admin/products/{id}` | 상품 삭제 |

---

## 관리자 - 주문 관리

**Base Path:** `/api/admin/orders`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/orders` | 전체 주문 목록 |
| GET | `/api/admin/orders/{id}` | 주문 상세 |
| PATCH | `/api/admin/orders/{id}/status` | 주문 상태 변경 |

---

## 관리자 - 결제 설정

**Base Path:** `/api/admin/payment`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/payment` | 결제 게이트웨이 설정 목록 |
| GET | `/api/admin/payment/{pg}` | 특정 PG 설정 조회 |
| POST | `/api/admin/payment/{pg}/save` | PG 설정 저장/수정 |

---

## 관리자 - 배너 관리

**Base Path:** `/api/admin/banners`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/banners` | 배너 목록 |
| GET | `/api/admin/banners/form` | 배너 폼 데이터 |
| POST | `/api/admin/banners/save` | 배너 저장 (이미지 업로드) |
| POST | `/api/admin/banners/delete` | 배너 삭제 |

---

## 관리자 - 메뉴 관리

**Base Path:** `/api/admin/menus`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/menus` | 메뉴 목록 |
| POST | `/api/admin/menus/save` | 메뉴 저장/수정 |
| POST | `/api/admin/menus/delete` | 메뉴 삭제 |
| POST | `/api/admin/menus/reorder` | 메뉴 순서 변경 |

**AdminController 내 메뉴 관리** (`/api/admin`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/menu` | 메뉴 관리 데이터 |
| POST | `/api/admin/menu/save` | 메뉴 저장 |
| POST | `/api/admin/menu/delete` | 메뉴 삭제 |
| POST | `/api/admin/menu/reorder` | 메뉴 순서 변경 |

---

## 관리자 - 게시판 관리

**Base Path:** `/api/admin`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/board` | 게시판 관리 목록 |
| POST | `/api/admin/board/create` | 게시판 생성 |
| GET | `/api/admin/board/edit/{boardId}` | 게시판 편집 데이터 |
| POST | `/api/admin/board/update` | 게시판 수정 |
| POST | `/api/admin/board/delete` | 게시판 삭제 |

---

## 관리자 - 스토리 관리

**Base Path:** `/api/admin/stories`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/stories` | 전체 스토리 목록 |
| POST | `/api/admin/stories` | 스토리 생성 |
| PUT | `/api/admin/stories/{id}` | 스토리 수정 |
| DELETE | `/api/admin/stories/{id}` | 스토리 삭제 |

---

## 관리자 - 소셜 로그인 설정

**Base Path:** `/api/admin/social`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/social` | 소셜 로그인 설정 목록 |
| POST | `/api/admin/social/save` | 소셜 로그인 설정 저장 |
| POST | `/api/admin/social/delete` | 소셜 로그인 설정 삭제 |

---

## 관리자 - 사이트 설정

**Base Path:** `/api/admin/setting`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/setting` | 사이트 설정 조회 |
| POST | `/api/admin/setting/save` | 사이트 설정 저장 |

---

## 관리자 - 통계

**Base Path:** `/api/admin/stats`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/admin/stats` | 방문자 통계 (일별 방문자, 합계) |

---

## 공통 응답 형식

### 성공

```json
{
  "success": true,
  "data": { ... }
}
```

### 에러

```json
{
  "success": false,
  "message": "에러 메시지"
}
```

---

## 인증 방식

### JWT Token

로그인 성공 시 JWT 토큰이 반환됩니다. 이후 인증이 필요한 API 요청 시 헤더에 포함합니다.

```
Authorization: Bearer <jwt-token>
```

### OAuth2 소셜 로그인

소셜 로그인은 `/oauth2/authorization/{provider}` 로 리다이렉트하여 진행됩니다.
지원 프로바이더: Google, Naver, Kakao, Github
