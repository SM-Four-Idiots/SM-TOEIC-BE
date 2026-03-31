# 영단어 마스터 (English Word Master) - Back-end (Spring Boot + MySQL)

영단어 마스터(English Word Master) Back-end 레포지토리입니다. [cite_start]Spring Boot 기반의 프로젝트이며, 폼/검증, API 통신, 데이터 관리를 위한 견고한 아키텍처로 구성되어 있습니다[cite: 346, 347].

## 프로젝트 소개

[cite_start]바쁜 학업과 취업 준비로 꾸준한 영단어 학습에 어려움을 겪고, 단순 텍스트 나열식 암기 방식에 지루함을 느끼는 대학생들을 위해, 워들(Wordle), 지뢰찾기 부활 찬스 등 게이미피케이션(Gamification) 요소를 결합한 맞춤형 영어 단어 학습 Web App입니다[cite: 269, 270].

## API 명세 (API Docs)

현재 API 명세는 다음 도구를 활용하여 자동화 및 문서화될 예정입니다:

  * [cite_start]**Swagger (Springdoc)** 또는 **Spring RestDocs** (도입 확정 후 링크 업데이트 예정) [cite: 80, 206]

## 기술 스택

  * [cite_start]**Main**: Java, Spring Boot [cite: 74, 200, 346]
  * [cite_start]**Database**: MySQL, Spring Data JPA [cite: 77, 203, 348]
  * [cite_start]**Build Tool**: Gradle (또는 Maven - *팀 내 최종 통일 예정*) [cite: 74, 200]
  * [cite_start]**Infrastructure**: Docker (`docker-compose.yml`을 통한 로컬 DB 컨테이너 세팅) [cite: 76, 202, 354]
  * [cite_start]**Deployment**: AWS EC2 프리티어, Koyeb, Render 등 클라우드 서버 (선정 예정) [cite: 84, 208]
  * [cite_start]**AI Tools (Vibe Coding)**: Gemini 3.1 Pro, GPT-5 5.3 Codex, CodeRabbit AI [cite: 357, 359, 361]
  * [cite_start]**IDE**: IntelliJ IDEA 권장 [cite: 351]

## 빠른 시작

### 사전 요구사항

  * Java 17 이상 권장
  * [cite_start]Docker 및 Docker Compose 설치 (로컬 DB 세팅용) [cite: 76, 202]

### 실행 방법

```bash
# 1. 레포지토리 클론
git clone https://github.com/SM-Four-Idiots/SM-TOEIC-BE.git
cd SM-TOEIC-BE

# 2. 로컬 MySQL DB 컨테이너 빌드 및 실행
docker-compose up -d

# 3. Spring Boot 애플리케이션 빌드
./gradlew build

# 4. 서버 실행 (기본 포트: http://localhost:8080)
./gradlew bootRun
```

[cite_start][cite: 76, 77, 202, 203]

## 개발 환경 설정

프로젝트의 보안과 원활한 실행을 위해 모든 팀원은 아래 환경을 반드시 세팅해야 합니다.

### 1\. 보안 및 환경 변수 (Environment Variables)

  * [cite_start]`.env` 또는 `application-dev.yml` 등 민감한 정보(DB 비밀번호, 시크릿 키 등)가 담긴 파일은 절대 GitHub에 올리지 않고 `.gitignore`에 추가하여 보안을 유지합니다[cite: 366].
  * [cite_start]초기 세팅하는 팀원을 위해 레포지토리에 제공된 `.env.example` 파일을 참고하여, 각자의 로컬 환경에 맞게 필수 환경 변수를 세팅해 주세요[cite: 367].

### 2\. 코드 자동완성 및 리뷰 도구

  * [cite_start]PR 생성 시 **CodeRabbit AI**가 자동으로 1차 코드 리뷰를 진행하므로 리뷰 결과 및 개선 사항을 적극 반영합니다[cite: 361, 374].

## 프로젝트 규약 (Conventions)

### Git 협업 전략

```bash
[cite_start]main: 배포용 브랜치 (안정 버전) [cite: 390]
[cite_start]develop: 개발 메인 브랜치 (다음 배포 버전) [cite: 391]
[cite_start]feature/[기능이름]: 기능 개발 브랜치 (예: feature/auth) [cite: 392]
[cite_start]fix/[수정내용]: 버그 수정 브랜치 (예: fix/word-crud) [cite: 393]
[cite_start]chore/[작업내용]: 설정 및 환경 구성 브랜치 (예: chore/setup-docker) [cite: 394]
```

### 작업 순서

프로젝트 진행자 모두가 지켜야 할 작업 순서입니다.

1.  [cite_start]develop 브랜치에서 `feature/[기능이름]` 브랜치를 생성합니다[cite: 396].
2.  [cite_start]기능 개발 완료 후, develop 브랜치로 Pull Request (PR)를 생성합니다[cite: 397].
3.  [cite_start]CodeRabbit AI의 1차 자동 코드 리뷰를 거친 후, 팀원 최소 1명 이상의 승인(Approve)을 받습니다[cite: 398].
4.  [cite_start]승인(Approve)을 완료한 후, develop 브랜치에 병합(Merge)합니다[cite: 399].

### 병합 시 충돌(Conflict)이 발생한 경우

[cite_start]PR 병합 과정에서 충돌이 발생하면, PR 작성자가 직접 로컬 환경에서 충돌을 해결한 후 다시 PR을 업데이트해야 합니다[cite: 399].

#### 진행 순서

1.  **최신 develop 브랜치 동기화**

<!-- end list -->

```bash
git checkout develop
git pull origin develop
```

2.  **작업 브랜치로 이동 및 병합**

<!-- end list -->

```bash
git checkout feature/[기능이름]
git merge develop
```

3.  **충돌(Conflict) 해결**
    IDE를 열고 충돌이 발생한 파일들을 확인합니다.
    Current Change와 Incoming Change 중 알맞은 코드를 선택하거나 직접 수정한 후 저장합니다.
4.  **해결된 코드 커밋 및 푸시**

<!-- end list -->

```bash
git add .
git commit -m "chore: 병합 충돌 해결"
git push origin feature/[기능이름]
```

5.  **PR 확인 및 최종 병합**
    GitHub의 PR 페이지에서 충돌 해결 여부를 확인한 후 승인(Approve)을 거쳐 develop 브랜치에 병합합니다.

### 커밋 메시지 컨벤션

[cite_start]커밋 메시지는 Conventional Commits 규칙을 따르며, 어떤 작업을 수행했는지 직관적으로 파악할 수 있게 작성합니다[cite: 401].

```bash
[cite_start]feat: 새로운 기능 추가 [cite: 402]
[cite_start]fix: 버그 수정 [cite: 403]
[cite_start]docs: 문서 수정 (README 등) [cite: 404]
[cite_start]style: 코드 스타일 수정 (포맷팅, 세미콜론 등 로직 변경 없음) [cite: 405]
[cite_start]refactor: 코드 리팩토링 [cite: 406]
[cite_start]chore: 빌드 설정, 패키지 매니저 설정 등 (코드 로직 변경 없음) [cite: 409]

예시: "feat: 관리자 단어 추가 CRUD 로직 구현, fix: 회원가입 API NullPointerException 해결"
```

### 디렉토리 구조 (Layered Architecture 예시)

데이터 관리 전략에 따라 계층형 폴더 구조를 사용합니다.

```bash
src/main/java/com/sm/toeic/
├── config/         # Spring Security, Swagger, CORS 등 설정 클래스
├── controller/     # 웹 계층 (API Endpoints, Request 매핑)
├── service/        # 비즈니스 로직 및 트랜잭션 처리
├── repository/     # 데이터 접근 계층 (Spring Data JPA)
├── domain/         # 데이터베이스 엔티티 (Entity) 클래스
├── dto/            # 데이터 전송 객체 (Request, Response)
└── exception/      # 커스텀 예외 처리 및 글로벌 예외 핸들러
```

### 네이밍 컨벤션

[cite_start]프레임워크 및 DB 특성에 맞는 표준 컨벤션을 엄격히 준수합니다[cite: 377].

1.  [cite_start]**Java 클래스 (Class)**: PascalCase 사용 (예: `WordService.java`) [cite: 386]
2.  [cite_start]**Java 메서드 및 변수**: camelCase 사용 (예: `getWordList`) [cite: 386]
3.  [cite_start]**DB 테이블 및 컬럼**: snake\_case 사용 (예: `word_list`, `user_id`) [cite: 387]

## 연관 레포지토리

  * Front-end 레포지토리: [https://github.com/SM-Four-Idiots/SM-TOEIC-FE](https://www.google.com/search?q=https://github.com/SM-Four-Idiots/SM-TOEIC-FE)
