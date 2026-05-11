# Task Manager (Spring Boot)

JWT(Access Token)로 인증하고, 로그인한 사용자 기준으로 Todo를 CRUD하는 연습/포트폴리오용 API입니다.

## 설계 메모 (면접·문서용)

1차로는 **Stateless JWT 인증 흐름**을 끝까지 검증하는 데 집중했습니다. **Refresh Token**은 저장소(또는 블랙리스트)·회전(rotation)·로그아웃 처리까지 범위가 커져서, 이 프로젝트에서는 **다음 단계**로 남겨 두었습니다.

Todo는 **로그인한 사용자**와 연결되어 있고, 조회·수정·삭제 시 **그 사용자의 데이터인지** 검사합니다. 다른 사람의 일정에는 접근하지 못하게 했습니다.

**401**은 인증 실패(예: 로그인 자격 증명이 맞지 않음), **403**은 인증은 됐지만 해당 리소스에 대한 권한이 없을 때(예: 다른 사용자의 Todo)로 나누었습니다.

## 백엔드 구현 노트

### 최근 반영한 작업 요약
- **페이징 기본값**: `GET /api/todos`에 `@PageableDefault`(기본 크기 20, `createdDate` 내림차순) 적용.
- **입력 검증**: 회원가입·로그인·Todo 요청 DTO에 Bean Validation(`@NotBlank`, `@Email`, `@Size`, `@NotNull` 등) 적용. 실패 시 **400** + `"validation_failed"`(전역 예외 처리).
- **JWT**: 만료·변조 등으로 파싱에 실패한 Bearer 토큰으로 보호 API를 호출하면 **401** + `{"error":"unauthorized"}` (`JwtAuthenticationFilter`). 통합 테스트로 만료 토큰·로그인 흐름 등을 검증함.

### JPA Auditing
- 애플리케이션에 **`@EnableJpaAuditing`**을 켜 두었고, 엔티티 공통 상위 클래스(`BaseTimeEntity`)에 **`@CreatedDate` / `@LastModifiedDate`**를 두어 **생성·수정 시각이 기본으로 채워지도록** 했습니다.

### Soft Delete를 쓰지 않은 이유
- Todo는 **사용자 민감 정보(개인 식별·결제 등)** 성격이 아니라, 복구·감사 요구가 이 프로젝트 범위에서는 크지 않다고 보았습니다.
- **삭제 시 물리 삭제**로 두어 조회·인덱스·스토리지 측면에서 **단순하고 부담이 적다**고 판단했습니다. (필요해지면 `deleted` 플래그 + 조회 조건 보강 같은 방식으로 확장 가능.)

### 목록 응답: Spring Data `Page`
- Todo 목록 API는 **`Page<TodoResponseDTO>`** 형태로 반환합니다. (`Pageable` + `Page` — 콘텐츠, 총 건수, 페이지 번호·크기 등 메타데이터를 함께 내려 프론트에서 페이징 UI에 활용하기 쉽게 했습니다.)
- 클라이언트는 표준 Spring Data 쿼리 파라미터로 조정할 수 있습니다. 예: `GET /api/todos?page=0&size=10&sort=title,asc` (미지정 시 컨트롤러의 `@PageableDefault`가 적용됩니다.)

## 필요한 것

- **JDK 17** (Gradle toolchain 기준)
- (선택) **Postman** 또는 HTTP 클라이언트

## 실행

프로젝트 루트에서:

```bash
./gradlew bootRun
```

Windows:

```bat
gradlew.bat bootRun
```

기본 주소는 **http://localhost:8080** 입니다. (별도 `server.port` 설정이 없으면 8080)

### 프론트엔드 (React + Vite)

`frontend/` 디렉터리에서:

```bash
cd frontend
npm install
npm run dev
```

개발 서버는 보통 **http://localhost:5173** 입니다. API는 `vite.config.js`의 **프록시**로 `/api` → `http://localhost:8080` 에 전달됩니다.

Windows에서 Vite 8 계열이 `Cannot find native binding` / `@rolldown/binding-win32-x64-msvc` 오류를 낼 수 있어, 이 프로젝트는 **Vite 6 + 표준 `@vitejs/plugin-react`**만 사용합니다. (여전히 오류면 `frontend`에서 `node_modules`와 `package-lock.json` 삭제 후 `npm install` 재실행.)

### 개발용 샘플 데이터 (선택)

`CommandLineRunner`로 테스트 유저/샘플 Todo를 넣습니다. **`dev` 프로필**일 때만 동작합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

- 유저: `testUser` / 비밀번호: `12345`
- 이미 있으면 유저는 재사용하고, Todo는 DB가 비어 있을 때만 추가합니다.

샘플 없이 쓰려면 프로필 없이 실행한 뒤 아래 **회원가입**부터 하면 됩니다.

## API 문서 (Swagger)

서버 실행 후 브라우저에서:

- http://localhost:8080/swagger-ui/index.html

## Postman으로 빠르게 확인 (JWT)

**공통:** 보호된 API에는 헤더를 넣습니다.

- `Authorization: Bearer <accessToken>`

Postman에서는 **Auth 타입: Bearer Token**에 `accessToken` 값만 붙여 넣거나, **Headers**에 위 한 줄을 직접 넣어도 됩니다.

### 1) 회원가입

`POST` http://localhost:8080/api/auth/signup  

Body (JSON):

```json
{
  "username": "u1",
  "email": "u1@example.com",
  "password": "pass1234"
}
```

### 2) 로그인 (Access Token 발급)

`POST` http://localhost:8080/api/auth/login  

Body (JSON):

```json
{
  "usernameOrEmail": "u1",
  "password": "pass1234"
}
```

응답 예:

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJ..."
}
```

### 3) Todo 목록

`GET` http://localhost:8080/api/todos  

Headers: `Authorization: Bearer eyJ...`

응답은 **Spring Data `Page` JSON** 형태(콘텐츠 + `totalElements`, `number`, `size` 등)입니다. 필요 시 `?page=0&size=10&sort=createdDate,desc` 로 조정할 수 있습니다.

### 4) Todo 생성

`POST` http://localhost:8080/api/todos  

Headers: `Authorization: Bearer eyJ...`  

Body (JSON):

```json
{
  "title": "할 일",
  "dueDate": "2026-05-10T18:00:00"
}
```

### 5) Todo 단건 조회

`GET` http://localhost:8080/api/todos/{id}

### 6) 완료 여부 변경

`PATCH` http://localhost:8080/api/todos/{id}/status  

Body (JSON):

```json
{ "completed": true }
```

### 7) 제목/마감 수정

`PATCH` http://localhost:8080/api/todos/{id}/update  

Body는 생성과 동일 (`title`, `dueDate`).

### 8) 삭제

`DELETE` http://localhost:8080/api/todos/{id}

## 빌드 / 테스트

```bash
./gradlew test
```

## 설정 메모

- H2 인메모리 DB 사용 (`application.properties` 참고).
- JWT 설정 키: `app.jwt.*`  
  로컬 데모용 값이므로, 실제 배포 시에는 **환경 변수 등으로 secret을 분리**하는 것을 권장합니다.
