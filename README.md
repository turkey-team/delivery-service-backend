# 🍔 Delivery-Service

프로젝트 간단 요약 및 소개

- **프로젝트 목적**: 배달 서비스를 관리하기 위한 시스템으로 고객, 사장님, 관리자 등 다양한 사용자의 주문.리뷰.가게 운영 프로세스를 효율적으로 관리하기 위해 개발되었습니다.
- **주요 기능**: 주문, 리뷰, 가게 관리와 회원 인증/인가, AI 답글, 캐시, 알림 기능을 지원하는 백엔드 서비스
- **개발 기간**: 2025/9/26 ~ 2025/10/17

---

## 📌 프로젝트 목적 / 상세

### 1. 회원 관리
- 역할(Role) 기반 인증/인가 구현
- JWT 토큰 발급 및 유효성 검증
- 회원 정보 조회/수정/삭제 가능
- 이메일 인증 기능

### 2. 가게 관리 (Owner 전용)
- 가게 CRUD 기능
- 메뉴 등록, 수정, 삭제 및 상태 업데이트
- 배달가능지역 설정 및 변경

### 3. 주문 관리
- 주문 생성, 결제 상태 변경, 취소 기능
- 주문 내역 조회 및 필터링 지원

### 4. 리뷰 및 답글 기능
- 고객 리뷰 등록 및 조회
- 사장님 답글 작성 자동화(AI)
- Redis 캐싱으로 조회 성능 최적화

### 5. 주소
- 시·도, 시·군·구, 동 CRUD API 기능
- 주소 CRUD API 제공

### 5. 검색 및 필터링
- 가게, 메뉴, 리뷰 등 다양한 조건 검색 가능
- 평점, 날짜, 가격 등 정렬 지원

### 6. 알림 및 예외 처리
- Slack Webhook 연동으로 에러 알림 전송
- Controller AOP 기반 로깅으로 요청/응답 기록

### 7. AI 연동
- 리뷰 답글 자동 생성
- 가게 메뉴 상세 설명 생성
- Spring AI + OpenAI + Google AI Studio 연동

### 8. 이미지 업로드 및 관리
- 메뉴, 리뷰 이미지 AWS S3 업로드 및 조회
- URL 저장 및 조회 기능

### 9. PostGIS를 활용한 지역 기반 가게 검색
- 사용자의 위치 정보를 기반으로 가게 검색 기능 구현

### 10. 배포 및 문서화
- Docker 기반 컨테이너 배포
- AWS ECS, RDS 연동 가능
- Swagger UI로 API 문서 제공

---

## 👥 팀원 역할 분담

| 이름 | 역할 | 담당 내용                                                |
|------|------|------------------------------------------------------|
| 이나라힘 | 팀장 |Jwt 기반 인증/인가 구현, 이메일 인증 시스템, 인프라 설계, 배포 자동화      |
| 이수현 | 팀원 | 리뷰/답글 API 구현, 리뷰 조회 Redis 캐싱, Spring AI를 활용한 답글 자동생성 |
| 양지웅 | 팀원 | 주소 API, PostGIS를 활용한 가게 조회, S3 Presigned URL         |
| 이건희 | 팀원 | 지역(시·도, 시·군·구, 동) API, AI 프롬프트 API, Slack API        |
| 박수현 | 팀원 | 가게/장바구니/카테고리 API 구현                                  |
| 김준성 | 팀원 | 메뉴 / 주문 API 구현, UI 프레임 구성 / Flow 관리                  |

---

## ⚙ 서비스 구성 및 실행 방법

### 환경 설정 (Prerequisites)
- Java 17 이상
- Spring Boot 3.5.6 이상
- PostgreSQL 16.10(Ubuntu) 이상
- Gradle 8.8 이상
- Redis 8.2.2 이상
- Spring AI 1.0.3 이상

### 실행 방법

```
./gradlew clean build -x test
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
java -jar build/libs/*.jar
```
---

## 🗂 ERD

<img width="1000" height="1000" alt="image" src="https://github.com/user-attachments/assets/d651e969-9b17-499a-a615-15eec76ca1d8" />

---

## 🔧 기술 스택
### Back-end
- **Java**
- **Spring Boot**
    - Spring Security
    - Spring AI
    - JWT
    - JUnit
    - QueryDSL
    - Lombok
- **JPA (Hibernate)**

### Database
- PostgreSQL(with PostGIS)

### Infrastructure
- **AWS**
    - EC2
    - RDS
    - S3 Bucket
    - Redis
- **Google AI Studio**
- Docker

### CI/CD
- Github Actions

### Tools
- Github
- Figma
- dbdiagram.io
- Postman
- Swagger
- Slack
- Notion
