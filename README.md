# 🍱 Delivery Review System

사용자 리뷰와 답글을 관리하는 웹 서비스 프로젝트입니다.  
- **프로젝트 목적**: 사용자 리뷰 관리 및 조회 성능 개선, Redis 캐싱 적용  
- **주요 기능**: 리뷰/답글 CRUD, JWT 인증, API 문서화, Redis 캐싱

---

## 👥 팀원 역할 분담

| 이름 | 역할 | 담당 내용 |
|------|------|-----------|
| 이수현 | 팀장 | 전체 프로젝트 관리, 발표 자료 작성, 배포 |
| 김민재 | 백엔드 | 리뷰/답글 API 구현, Redis 캐싱, DB 설계 |
| 박지연 | 프론트엔드 | 화면 설계, UI/UX 구현, 프론트 연동 |
| 최지훈 | 백엔드 | 인증/인가, JWT 적용, API 문서화

---

## ⚙ 서비스 구성 및 실행 방법

### 환경 설정 (Prerequisites)
- Java 17 이상, Spring Boot 3.x  
- PostgreSQL 15 이상  
- Redis 7 이상  
- Node.js 20 이상 (프론트엔드 사용 시)  

### 실행 방법
1. GitHub 레포지토리 클론
   ```bash
   git clone https://github.com/YourTeam/delivery-review-system.git

📌 프로젝트 목적/상세

사용자 리뷰 데이터를 효율적으로 관리

리뷰 첫 페이지 트래픽을 Redis 캐시로 처리

REST API 설계 및 Swagger 문서화

JWT 인증 기반 권한 관리 및 보안 강화

🔧 기술 스택
Back-end

Java, Spring Boot, JPA (Hibernate), Redis, JWT, QueryDSL

Front-end

React.js, Tailwind CSS, Axios

Database

PostgreSQL, H2 (테스트용)

Infra / Tools

Docker, GitHub Actions, Swagger, Notion

