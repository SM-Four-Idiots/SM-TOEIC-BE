# 토스트 (TOEST, TOAST + QUEST) - Back-end (Spring Boot + MySQL)

토스트 (TOEST, TOAST + QUEST) Back-end 레포지토리입니다. Spring Boot 기반의 프로젝트이며, 폼/검증, API 통신, 데이터 관리를 위한 견고한 아키텍처로 구성되어 있습니다.

## 프로젝트 소개

바쁜 학업과 취업 준비로 꾸준한 영단어 학습에 어려움을 겪고, 단순 텍스트 나열식 암기 방식에 지루함을 느끼는 대학생들을 위해, 워들(Wordle), 지뢰찾기 부활 찬스 등 게이미피케이션(Gamification) 요소를 결합한 맞춤형 영어 단어 학습 Web App입니다.

## API 명세 (API Docs)

현재 API 명세는 다음 도구를 활용하여 자동화 및 문서화될 예정입니다:

* **Swagger (Springdoc)** 또는 **Spring RestDocs** (도입 확정 후 링크 업데이트 예정)

## 기술 스택

* **Main**: Java, Spring Boot
* **Database**: MySQL, Spring Data JPA
* **Build Tool**: Gradle
* **Infrastructure**: Docker (`docker-compose.yml`을 통한 로컬 DB 컨테이너 세팅)
* **Deployment**: AWS EC2 프리티어
* **AI Tools (Vibe Coding)**: Gemini 3.1 Pro, GPT-5 5.3 Codex, Claude opus 4.6 CodeRabbit AI
* **IDE**: IntelliJ IDEA 권장

## 빠른 시작

### 사전 요구사항

* Java 21 이상
* Docker Desktop 설치

### 1. Docker Desktop 설치

Docker Desktop은 컨테이너 실행 환경입니다. 설치하면 DB, 서버 등을 명령어 한 줄로 실행할 수 있습니다.

**Windows**
1. [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop) 접속
2. `Download for Windows` 클릭 후 설치
3. 설치 완료 후 Docker Desktop 실행 (고래 아이콘이 트레이에 뜨면 정상)
4. 터미널에서 확인
```bash
   docker -v
```

**macOS**
1. 동일한 링크에서 `Download for Mac` 클릭 후 설치
2. 설치 완료 후 Docker Desktop 실행 (상단 메뉴바에 고래 아이콘이 뜨면 정상)
3. 터미널에서 확인
```bash
   docker -v
```

> Docker Desktop이 실행 중인 상태에서만 docker 명령어가 동작합니다.
> 개발할 때는 항상 Docker Desktop을 먼저 켜두세요.

### 2. 두 레포 클론

SM-TOEIC-BE와 SM-TOEIC-FE는 **반드시 같은 폴더 안에 나란히** 위치해야 합니다.

```bash
# 원하는 폴더로 이동 후
git clone https://github.com/SM-Four-Idiots/SM-TOEIC-BE.git
git clone https://github.com/SM-Four-Idiots/SM-TOEIC-FE.git
```

클론 후 구조:
아무 폴더명/
├── SM-TOEIC-BE/
└── SM-TOEIC-FE/

### 3. application.yaml 설정

```bash
cd SM-TOEIC-BE
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

### 4. 백엔드 빌드

Docker 실행 전에 반드시 빌드가 먼저 되어야 합니다.

```bash
# Windows
gradlew build -x test

# macOS / Linux
./gradlew build -x test
```

`BUILD SUCCESSFUL` 메시지가 뜨면 완료입니다.

### 5. 전체 환경 실행

```bash
docker compose up -d
```

처음 실행 시 이미지를 다운로드하므로 수 분 정도 소요될 수 있습니다.

실행 확인:
```bash
docker compose ps
```

아래처럼 3개가 모두 `running` 상태면 정상입니다.

NAME        STATUS
backend     running
db          running
frontend    running

---

### 팀원 PC에 필요한 것
```
- Docker Desktop 설치
- Java 21 설치 (빌드용)
- IntelliJ IDEA (또는 VS Code)
```

## 개발 환경 설정

프로젝트의 보안과 원활한 실행을 위해 모든 팀원은 아래 환경을 반드시 세팅해야 합니다.

### 1. 보안 및 환경 변수 (Environment Variables)

* `.env` 또는 `application-dev.yml` 등 민감한 정보(DB 비밀번호, 시크릿 키 등)가 담긴 파일은 절대 GitHub에 올리지 않고 `.gitignore`에 추가하여 보안을 유지합니다.
* 초기 세팅하는 팀원을 위해 레포지토리에 제공된 `.env.example` 파일을 참고하여, 각자의 로컬 환경에 맞게 필수 환경 변수를 세팅해 주세요.

### 2. 코드 자동완성 및 리뷰 도구

* PR 생성 시 **CodeRabbit AI**가 자동으로 1차 코드 리뷰를 진행하므로 리뷰 결과 및 개선 사항을 적극 반영합니다.

## 프로젝트 규약 (Conventions)

### Git 협업 전략

```bash
main: 배포용 브랜치 (안정 버전)
develop: 개발 메인 브랜치 (다음 배포 버전)
feature/[기능이름]: 기능 개발 브랜치 (예: feature/auth)
fix/[수정내용]: 버그 수정 브랜치 (예: fix/word-crud)
chore/[작업내용]: 설정 및 환경 구성 브랜치 (예: chore/setup-docker)
```

### 작업 순서

프로젝트 진행자 모두가 지켜야 할 작업 순서입니다.

1. develop 브랜치에서 `feature/[기능이름]` 브랜치를 생성합니다.
2. 기능 개발 완료 후, develop 브랜치로 Pull Request (PR)를 생성합니다.
3. CodeRabbit AI의 1차 자동 코드 리뷰를 거친 후, 팀원 최소 1명 이상의 승인(Approve)을 받습니다.
4. 승인(Approve)을 완료한 후, develop 브랜치에 병합(Merge)합니다.

### 병합 시 충돌(Conflict)이 발생한 경우

PR 병합 과정에서 충돌이 발생하면, PR 작성자가 직접 로컬 환경에서 충돌을 해결한 후 다시 PR을 업데이트해야 합니다.

#### 진행 순서

1. **최신 develop 브랜치 동기화**

```bash
git checkout develop
git pull origin develop
```

2. **작업 브랜치로 이동 및 병합**

```bash
git checkout feature/[기능이름]
git merge develop
```

3. **충돌(Conflict) 해결**
   IDE를 열고 충돌이 발생한 파일들을 확인합니다.
   Current Change와 Incoming Change 중 알맞은 코드를 선택하거나 직접 수정한 후 저장합니다.

4. **해결된 코드 커밋 및 푸시**

```bash
git add .
git commit -m "chore: 병합 충돌 해결"
git push origin feature/[기능이름]
```

5. **PR 확인 및 최종 병합**
   GitHub의 PR 페이지에서 충돌 해결 여부를 확인한 후 승인(Approve)을 거쳐 develop 브랜치에 병합합니다.

### 커밋 메시지 컨벤션

커밋 메시지는 Conventional Commits 규칙을 따르며, 어떤 작업을 수행했는지 직관적으로 파악할 수 있게 작성합니다.

```bash
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정 (README 등)
style: 코드 스타일 수정 (포맷팅, 세미콜론 등 로직 변경 없음)
refactor: 코드 리팩토링
chore: 빌드 설정, 패키지 매니저 설정 등 (코드 로직 변경 없음)

예시: "feat: 관리자 단어 추가 CRUD 로직 구현, fix: 회원가입 API NullPointerException 해결"
```

### 디렉토리 구조 (Layered Architecture 예시)

데이터 관리 전략에 따라 계층형 폴더 구조를 사용합니다.

```bash
src/main/java/com/
├── config/         # Spring Security, Swagger, CORS 등 설정 클래스
├── controller/     # 웹 계층 (API Endpoints, Request 매핑)
├── service/        # 비즈니스 로직 및 트랜잭션 처리
├── repository/     # 데이터 접근 계층 (Spring Data JPA)
├── domain/         # 데이터베이스 엔티티 (Entity) 클래스
├── dto/            # 데이터 전송 객체 (Request, Response)
└── exception/      # 커스텀 예외 처리 및 글로벌 예외 핸들러
```

### 네이밍 컨벤션

프레임워크 및 DB 특성에 맞는 표준 컨벤션을 엄격히 준수합니다.

1. **Java 클래스 (Class)**: PascalCase 사용 (예: `WordService.java`)
2. **Java 메서드 및 변수**: camelCase 사용 (예: `getWordList`)
3. **DB 테이블 및 컬럼**: snake_case 사용 (예: `word_list`, `user_id`)

## 트러블슈팅 (Troubleshooting)

깃허브 이슈 템플릿을 토대로 이슈를 생성한 후 팀원과의 의견 조율 후 결정합니다.

## 연관 레포지토리

* Front-end 레포지토리: [https://github.com/SM-Four-Idiots/SM-TOEIC-FE](https://github.com/SM-Four-Idiots/SM-TOEIC-FE)
