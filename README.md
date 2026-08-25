# Minilog GraphQL

Spring Boot와 JPA를 활용하여 구현한 마이크로블로그 API 프로젝트입니다.

기존 Minilog JPA with Auth 프로젝트의 사용자, 게시글, 팔로우, 피드, JWT 인증 기능을 유지하면서  
Spring for GraphQL을 적용하여 GraphQL Query와 Mutation 기반 API를 추가했습니다.

## 개발 환경

- Java 21
- Spring Boot 4.1.0
- Gradle
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring for GraphQL
- GraphQL Java Extended Scalars
- JWT
- MySQL 8
- Lombok
- Springdoc OpenAPI (Swagger)
- GraphiQL
- JUnit 5
- Mockito
- AssertJ
- Testcontainers

## 프로젝트 구조

```text
com.asdf.minilog
├── config
│   ├── ApiDocumentationConfig
│   ├── GraphQLConfig
│   └── SecurityConfig
├── controller
│   ├── AuthenticationController
│   ├── UserController
│   ├── ArticleController
│   ├── FollowController
│   └── FeedController
├── dto
│   ├── AuthenticationRequestDto
│   ├── AuthenticationResponseDto
│   ├── UserRequestDto
│   ├── UserResponseDto
│   ├── ArticleRequestDto
│   ├── ArticleResponseDto
│   ├── FollowRequestDto
│   └── FollowResponseDto
├── entity
│   ├── User
│   ├── Article
│   ├── Follow
│   └── Role
├── exception
│   ├── UserNotFoundException
│   ├── ArticleNotFoundException
│   ├── NotAuthorizedException
│   ├── GlobalExceptionHandler
│   └── MinilogGraphQLExceptionResolver
├── graphql
│   ├── GraphQLQueryController
│   ├── GraphQLMutationController
│   ├── input
│   │   ├── CreateArticleInput
│   │   ├── UpdateArticleInput
│   │   ├── CreateUserInput
│   │   └── UpdateUserInput
│   └── response
│       ├── ArticleResponse
│       ├── FollowResponse
│       └── UserResponse
├── repository
│   ├── UserRepository
│   ├── ArticleRepository
│   └── FollowRepository
├── security
│   ├── JwtAuthenticationEntryPoint
│   ├── JwtRequestFilter
│   ├── JwtUtil
│   ├── MinilogGrantedAuthority
│   ├── MinilogUserDetails
│   └── MinilogUserDetailsService
├── service
│   ├── UserService
│   ├── ArticleService
│   └── FollowService
├── util
│   ├── EntityDtoMapper
│   └── DtoGraphqlMapper
└── MinilogApplication
```

GraphQL 스키마는 다음 경로에 정의합니다.

```text
src/main/resources/graphql/schema.graphqls
```

## 계층 구조

기존 REST API와 GraphQL API가 동일한 Service와 Repository 계층을 재사용합니다.

```text
REST Controller ─────┐
                     ↓
                   Service
                     ↓
                 Repository
                     ↓
                JPA / Hibernate
                     ↓
                   MySQL
                     ↑
                     │
GraphQL Resolver ────┘
```

GraphQL 요청의 전체 흐름은 다음과 같습니다.

```text
Client
  ↓ Authorization: Bearer JWT
Spring Security
  ↓
JwtRequestFilter
  ↓
JWT 인증
  ↓
/graphql
  ↓
GraphQL Schema
  ↓
Query / Mutation Resolver
  ↓
Service
  ↓
Repository
  ↓
JPA / Hibernate
  ↓
MySQL
```

### 각 계층의 역할

- **Security**: JWT 생성·검증 및 사용자 인증 처리
- **REST Controller**: 기존 REST API 요청 및 응답 처리
- **GraphQL Resolver**: GraphQL Query / Mutation을 Java 로직과 연결
- **Service**: 비즈니스 로직 및 데이터 소유권 기반 인가 처리
- **Repository**: JPA를 이용한 데이터베이스 접근
- **Entity**: 데이터베이스 테이블 및 연관관계 정의
- **DTO**: 기존 REST API 요청 및 응답 데이터 전달
- **GraphQL Input / Response**: GraphQL 요청 및 응답 데이터 구조
- **Mapper**: Entity, DTO, GraphQL Response 간 변환

## 주요 기능

### GraphQL Query

- 사용자별 게시글 조회
- 게시글 단건 조회
- 피드 조회
- 팔로잉 목록 조회
- 사용자 목록 조회
- 사용자 단건 조회

### GraphQL Mutation

- 게시글 생성 / 수정 / 삭제
- 팔로우 / 언팔로우
- 사용자 생성 / 수정 / 삭제

### 인증 및 인가

- Spring Security + JWT 인증
- `/graphql` 요청에 JWT 인증 적용
- `SecurityContext`에서 현재 로그인 사용자 정보 조회
- 게시글 작성자 및 팔로우 요청자 ID를 인증 정보에서 사용

## 주요 구현 내용

- Spring for GraphQL 적용
- `schema.graphqls`를 이용한 GraphQL Schema 정의
- Query / Mutation Resolver 구현
- `@QueryMapping`, `@MutationMapping`, `@Argument` 적용
- GraphQL Input / Response 타입 구현
- `DtoGraphqlMapper`를 이용한 REST DTO → GraphQL Response 변환
- 기존 Service / Repository 계층 재사용
- `Long`, `DateTime` Custom Scalar 등록
- `RuntimeWiringConfigurer`를 이용한 Custom Scalar 연결
- `DataFetcherExceptionResolverAdapter`를 이용한 GraphQL 전역 예외 처리
- 기존 Spring Security + JWT 인증 구조 연동
- `SecurityContextHolder`를 이용한 현재 로그인 사용자 조회
- GraphiQL을 이용한 Query / Mutation 테스트

## GraphQL 엔드포인트

| 경로 | 용도 |
| --- | --- |
| `/graphql` | 실제 GraphQL API 요청 |
| `/graphiql` | GraphQL API 테스트 UI |
| `/swagger-ui/index.html` | 기존 REST API 테스트 UI |

## 실행 방법

### 1. MySQL 실행

Docker를 이용하여 MySQL 컨테이너를 실행합니다.

```bash
docker start mysql-minilog
```

> 최초 실행 시에는 프로젝트에서 사용하는 MySQL 컨테이너를 먼저 생성해야 합니다.  
> Minilog용 Docker MySQL은 호스트의 `3308` 포트를 사용합니다.

### 2. 프로젝트 빌드

WSL2 터미널에서 프로젝트 디렉토리로 이동하여 빌드합니다.

```bash
cd ~/eog-springboot4/minilog-graphql
gradle build
```

### 3. 애플리케이션 실행

```bash
gradle bootRun
```

### 4. Swagger UI 접속

기존 REST API를 확인하고 테스트합니다.

```text
http://localhost:8080/swagger-ui/index.html
```

### 5. GraphiQL 접속

GraphQL API를 확인하고 테스트합니다.

```text
http://localhost:8080/graphiql
```

## GraphQL 인증 테스트

### 1. JWT 발급

Swagger UI에서 다음 API를 이용하여 로그인합니다.

```text
POST /api/v2/auth/login
```

로그인 성공 시 반환된 JWT를 복사합니다.

### 2. GraphiQL에 JWT 등록

GraphiQL의 Request Headers에 다음과 같이 JWT를 입력합니다.

```json
{
  "Authorization": "Bearer 발급받은_JWT"
}
```

### 3. Query / Mutation 실행

예를 들어 게시글을 조회할 수 있습니다.

```graphql
query {
    getArticle(articleId: 1) {
        articleId
        content
        authorName
    }
}
```

GraphQL에서는 응답 타입에 정의된 필드 중 필요한 필드만 선택하여 요청할 수 있습니다.

## 테스트

전체 테스트는 다음 명령어로 실행합니다.

```bash
gradle test
```

기존 Minilog의 Controller, Service, Entity 테스트를 유지하면서  
GraphQL 적용 이후에도 기존 비즈니스 로직이 정상적으로 동작하는지 확인합니다.

## 학습 내용

이 프로젝트를 통해 기존 Minilog JPA with Auth 프로젝트의 기능에 더해 다음 내용을 학습했습니다.

- GraphQL과 REST API의 차이
- GraphQL Schema
- Query / Mutation
- Input Type / Response Type
- Custom Scalar
- Spring for GraphQL
- `@QueryMapping`
- `@MutationMapping`
- `@Argument`
- GraphQL Resolver
- REST DTO와 GraphQL Response 간 변환
- 기존 Service / Repository 재사용
- GraphQL 전역 예외 처리
- GraphQL과 Spring Security / JWT 연동
- `SecurityContextHolder`를 이용한 인증 사용자 조회
- GraphiQL을 이용한 GraphQL API 테스트

## 참고

이 프로젝트는 『스프링 부트 개발자 온보딩 가이드』의 예제를 참고하여 학습 목적으로 구현했습니다.

교재의 예제를 기반으로 하되, 현재 학습 환경에 맞게 다음 사항을 변경하여 진행했습니다.

- Spring Boot 3 → Spring Boot 4.1.0
- Spring Boot 버전 변경에 따른 GraphQL 의존성 및 일부 코드 수정