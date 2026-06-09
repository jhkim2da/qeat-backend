# Qeat

세종대학교 축제 부스 운영을 위한 QR 주문 관리 서비스입니다. 부스 운영자는 부스, 메뉴, 테이블, 주문, 매출을 관리할 수 있고, 손님은 테이블 QR을 통해 로그인 없이 주문할 수 있습니다.

## 주요 기능

- 세종대학교 로그인 연동
- JSESSIONID 기반 세션 인증
- 부스 신청, 승인, 거절, 중지, 삭제
- 부스 영업 상태 및 영업 시간 관리
- 메뉴 등록, 조회, 수정, 삭제, 품절 처리
- 테이블 생성, bulk 생성, 활성화, 비활성화
- QR 기반 비로그인 주문 생성
- 운영자 주문 조회 및 주문 상태 변경
- 기간별 매출 조회
- 관리자용 부스 운영자 조회

## 기술 스택

- Backend: Java 21, Spring Boot 4.0.1, Spring Security, Spring Data JPA
- Database: MySQL 8.4, Docker Compose
- Auth: 세종대학교 로그인 연동, JSESSIONID 세션 쿠키
- QR/Image: ZXing, Multipart file upload
- Frontend 연동: React, `credentials: "include"` 기반 세션 쿠키 요청

## 실행 방법

### 1. 환경 변수 설정

`.env.example`을 참고해서 프로젝트 루트에 `.env` 파일을 생성합니다.

```properties
MYSQL_DATABASE=qeat
MYSQL_USER=qeat_user
MYSQL_PASSWORD=change-me
MYSQL_ROOT_PASSWORD=change-me
TZ=Asia/Seoul

DB_URL=jdbc:mysql://localhost:1000/qeat?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_unicode_ci
DB_USERNAME=qeat_user
DB_PASSWORD=change-me

FILE_DIR=/Users/kimjunghyun/Qeat/uploads
FILE_URL_PREFIX=/uploads
QR_BASE_URL=http://프론트_IP:5173
```

`QR_BASE_URL`은 QR 코드에 들어갈 프론트 주소입니다. 같은 와이파이에서 프론트와 연동할 때는 프론트 팀원의 현재 IP로 맞춰야 합니다.

### 2. MySQL 실행

```bash
docker compose up -d
```

MySQL CLI 접속 시 한글이 깨져 보이면 아래처럼 접속합니다.

```bash
docker exec -it qeat-mysql mysql --default-character-set=utf8mb4 -u root -p qeat
```

비밀번호는 `.env`의 `MYSQL_ROOT_PASSWORD` 값입니다.

### 3. 백엔드 실행

```bash
./gradlew bootRun
```

기본 서버 주소는 다음과 같습니다.

```text
http://localhost:9090
```

## 프론트 연동 주의사항

인증은 JWT가 아니라 JSESSIONID 세션 쿠키 방식입니다. 프론트에서 로그인 이후 인증이 필요한 API를 호출할 때는 반드시 쿠키를 포함해야 합니다.

```js
fetch("http://백엔드_IP:9090/api/auth/me", {
  credentials: "include"
});
```

Axios를 사용하는 경우:

```js
axios.get("http://백엔드_IP:9090/api/auth/me", {
  withCredentials: true
});
```

## 공통 응답

예외 응답은 다음 형식으로 통일되어 있습니다.

```json
{
  "message": "에러 메시지"
}
```

## 인증 API

### 로그인

```http
POST /api/auth/login
```

Request:

```json
{
  "studentNumber": "24102357",
  "password": "세종대학교_비밀번호"
}
```

Response:

```json
{
  "id": 1,
  "studentNumber": "24102357",
  "name": "김정현",
  "role": "OPERATOR"
}
```

### 내 정보 조회

```http
GET /api/auth/me
```

Response:

```json
{
  "id": 1,
  "studentNumber": "24102357",
  "name": "김정현",
  "role": "OPERATOR"
}
```

### 로그아웃

```http
POST /api/auth/logout
```

### 개발용 관리자 승격

```http
POST /api/auth/bootstrap-admin
```

로그인한 사용자를 개발용으로 관리자 권한으로 승격합니다. 제출 또는 운영 전에는 제거하거나 dev profile로 제한하는 것이 좋습니다.

## 부스 API

### 부스 생성

```http
POST /api/booths
```

Request:

```json
{
  "name": "Qeat 부스",
  "bank": "KAKAO",
  "accountNumber": "3333-00-0000000",
  "description": "축제 음식 판매 부스"
}
```

Response:

```json
1
```

사용 가능한 은행 enum:

```text
KB, SHINHAN, WOORI, HANA, NH, IBK, KAKAO, TOSS
```

### 내 부스 목록 조회

```http
GET /api/booths/my
```

Response:

```json
[
  {
    "boothId": 1,
    "name": "Qeat 부스",
    "description": "축제 음식 판매 부스",
    "bank": "KAKAO",
    "accountNumber": "3333-00-0000000",
    "boothStatus": "APPROVED",
    "open": true,
    "openTime": "10:00:00",
    "closeTime": "22:00:00",
    "canOrder": true,
    "createdAt": "2026-05-13"
  }
]
```

### 내 승인 부스 목록 조회

```http
GET /api/booths/my/approved
```

### 내 부스 단건 조회

```http
GET /api/booths/my/{boothId}
```

### 부스 수정

```http
PUT /api/booths/{boothId}
```

Request는 부스 생성과 동일합니다.

### 부스 삭제

```http
DELETE /api/booths/{boothId}
```

권한: 부스 주인 또는 관리자  
동작: 실제 DB row 삭제가 아니라 `boothStatus`를 `DELETED`로 변경하고 영업 상태를 닫습니다.

### 부스 영업 상태 변경

```http
PATCH /api/booths/{boothId}/open-status
```

Request:

```json
{
  "open": true
}
```

Response:

```json
true
```

### 부스 영업 시간 설정

```http
PATCH /api/booths/{boothId}/operating-time
```

Request:

```json
{
  "openTime": "10:00:00",
  "closeTime": "22:00:00"
}
```

### 부스 영업 시간 해제

```http
DELETE /api/booths/{boothId}/operating-time
```

### 승인 대기 부스 조회

```http
GET /api/booths/pending
```

권한: 관리자

### 부스 승인

```http
PATCH /api/booths/{boothId}/approve
```

권한: 관리자

### 부스 거절

```http
PATCH /api/booths/{boothId}/reject
```

권한: 관리자

### 부스 중지

```http
PATCH /api/booths/{boothId}/suspend
```

권한: 관리자  
동작: `boothStatus`를 `SUSPENDED`로 변경하고 영업 상태를 닫습니다.

부스 상태 enum:

```text
PENDING, REJECTED, APPROVED, SUSPENDED, DELETED
```

## 관리자 운영자 API

### 부스 운영자 목록 조회

```http
GET /api/booths/operators
```

권한: 관리자

Response:

```json
[
  {
    "userId": 1,
    "name": "김정현",
    "studentNumber": "24102357",
    "major": "컴퓨터공학과",
    "boothCount": 2
  }
]
```

### 부스 운영자 상세 조회

```http
GET /api/booths/operators/{operatorId}
```

권한: 관리자  
응답에는 해당 사용자의 기본 정보와 생성한 부스 목록이 포함됩니다. 부스 목록에는 승인, 대기, 거절, 중지, 삭제 상태가 모두 포함됩니다.

## 메뉴 API

### 부스 메뉴 목록 조회

```http
GET /api/booths/{boothId}/menus
```

Response:

```json
[
  {
    "id": 1,
    "name": "김치볶음밥",
    "description": "대표 메뉴",
    "price": 7000,
    "category": "MAIN_FOOD",
    "imageUrl": "/uploads/menu.png",
    "soldOut": false
  }
]
```

### 메뉴 단건 조회

```http
GET /api/menus/{menuId}
```

### 메뉴 생성

```http
POST /api/booths/{boothId}/menus
Content-Type: multipart/form-data
```

Form data:

```text
name=김치볶음밥
description=대표 메뉴
price=7000
category=MAIN_FOOD
image=파일
imageUrl=https://example.com/menu.png
```

`image` 파일과 `imageUrl`이 둘 다 있으면 파일 업로드가 우선됩니다.

카테고리 enum:

```text
MAIN_FOOD, SIDE_FOOD, DRINK, REQUEST
```

### 메뉴 수정

```http
PATCH /api/booths/{boothId}/menus/{menuId}
Content-Type: multipart/form-data
```

Form data는 메뉴 생성과 동일합니다.

### 메뉴 삭제

```http
DELETE /api/booths/{boothId}/menus/{menuId}
```

### 메뉴 품절 토글

```http
PATCH /api/booths/{boothId}/menus/{menuId}/sold-out
```

## 테이블 및 QR API

### 활성 테이블 조회

```http
GET /api/booths/{boothId}/tables
```

### 전체 테이블 조회

```http
GET /api/booths/{boothId}/tables/all
```

비활성 테이블까지 포함합니다.

Response:

```json
[
  {
    "id": 1,
    "boothId": 1,
    "tableNumber": 1,
    "tableToken": "abc123",
    "qrImageUrl": "/uploads/qr/abc123.png",
    "active": true
  }
]
```

### 단일 테이블 추가

```http
POST /api/booths/{boothId}/tables
```

Request:

```json
{
  "tableNumber": 1
}
```

### 테이블 bulk 추가

```http
POST /api/booths/{boothId}/tables/bulk
```

Request:

```json
{
  "count": 10
}
```

정책:

- 비활성 테이블을 먼저 재활성화합니다.
- 부족하면 존재하지 않는 가장 작은 `tableNumber`부터 새로 생성합니다.
- 조회 결과는 `tableNumber` 오름차순입니다.

### 테이블 비활성화

```http
PATCH /api/booths/{boothId}/tables/{tableId}/deactivate
```

### 테이블 활성화

```http
PATCH /api/booths/{boothId}/tables/{tableId}/activate
```

## 공개 QR API

### QR 테이블 정보 조회

```http
GET /api/public/tables/{tableToken}
```

로그인 없이 접근 가능합니다.

Response:

```json
{
  "boothId": 1,
  "boothName": "Qeat 부스",
  "open": true,
  "openTime": "10:00:00",
  "closeTime": "22:00:00",
  "canOrder": true,
  "tableId": 1,
  "tableNumber": 1,
  "tableToken": "abc123",
  "menus": [
    {
      "id": 1,
      "name": "김치볶음밥",
      "description": "대표 메뉴",
      "price": 7000,
      "category": "MAIN_FOOD",
      "imageUrl": "/uploads/menu.png",
      "soldOut": false
    }
  ]
}
```

프론트는 `canOrder`가 `false`일 때 손님 주문 UI를 막아야 합니다.

### QR 주문 생성

```http
POST /api/public/tables/{tableToken}/orders
```

로그인 없이 접근 가능합니다.

Request:

```json
{
  "items": [
    {
      "menuId": 1,
      "quantity": 2
    }
  ]
}
```

Response:

```json
1
```

## 주문 관리 API

### 부스 주문 조회

```http
GET /api/booths/{boothId}/orders
```

권한: 부스 운영자 또는 관리자

Response:

```json
[
  {
    "orderId": 1,
    "tableId": 1,
    "tableNumber": 1,
    "status": "CHECK",
    "totalPrice": 14000,
    "createdAt": "2026-05-13T13:25:10",
    "completedAt": null,
    "items": [
      {
        "menuName": "김치볶음밥",
        "quantity": 2,
        "price": 7000
      }
    ]
  }
]
```

주문 상태 enum:

```text
CHECK, COOKING, DONE, CANCELED
```

### 주문 접수

```http
PATCH /api/orders/{orderId}/booths/{boothId}/confirm
```

상태 변경: `CHECK` -> `COOKING`

### 주문 완료

```http
PATCH /api/orders/{orderId}/booths/{boothId}/complete
```

상태 변경: `COOKING` -> `DONE`

### 주문 취소

```http
PATCH /api/orders/{orderId}/booths/{boothId}/cancel
```

상태 변경: `CANCELED`

## 매출 API

### 기간별 매출 조회

```http
GET /api/booths/{boothId}/sales-summary?startDate=2026-05-13&endDate=2026-05-13
```

Response:

```json
{
  "totalSales": 42000,
  "totalOrderCount": 3,
  "dailySales": [
    {
      "date": "2026-05-13",
      "totalOrderCount": 3,
      "orders": [
        {
          "orderTime": "13:25:10",
          "amount": 12000
        },
        {
          "orderTime": "13:40:02",
          "amount": 18000
        },
        {
          "orderTime": "14:05:33",
          "amount": 12000
        }
      ]
    }
  ]
}
```

매출은 완료된 주문인 `DONE` 상태 기준으로 집계됩니다.

## 개발용 Thymeleaf QR 화면

기존 백엔드 화면도 남아 있습니다.

```http
GET /qr/{tableToken}
```

프론트 연동에서는 이 화면보다 `GET /api/public/tables/{tableToken}` API를 사용하는 흐름을 권장합니다.

## DB 참고

테이블과 주문 데이터를 초기화해야 할 때:

```sql
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE tables;
SET FOREIGN_KEY_CHECKS = 1;
```

`booth_status` enum을 수동 반영해야 할 경우:

```sql
ALTER TABLE booths
MODIFY booth_status ENUM('PENDING', 'REJECTED', 'APPROVED', 'SUSPENDED', 'DELETED') NOT NULL;
```

## 제출 전 확인 사항

- `/api/auth/bootstrap-admin`은 개발용 API이므로 운영 전 제거하거나 dev profile로 제한해야 합니다.
- CORS 허용 주소는 운영 환경과 개발 환경을 분리하는 것이 좋습니다.
- `.env` 파일은 Git에 올리지 않고 `.env.example`만 공유합니다.
- QR 코드 생성 시점의 `QR_BASE_URL`이 QR 이미지에 들어가므로, 프론트 IP가 바뀌면 서버 재시작 후 새 테이블을 생성해야 합니다.
