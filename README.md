# RunTracker Server

AI 기반 러닝 크루 애플리케이션의 백엔드 서버입니다. 개인 러닝 기록 관리, 크루 기반 그룹 러닝, 실시간 랭킹, 커뮤니티 기능을 제공하는 AI 러닝 앱입니다.

## 목차

- [프로젝트 개요](#프로젝트-개요)
- [서비스 화면](#서비스-화면)
- [아키텍처](#아키텍처)
- [기술 스택](#기술-스택)
- [성능 최적화](#성능-최적화)
- [보안 기능](#보안-기능)
- [이벤트 기반 아키텍처](#이벤트-기반-아키텍처)
- [API 엔드포인트](#api-엔드포인트)
- [프로젝트 구조](#프로젝트-구조)
- [구현된 주요 기능](#구현된-주요-기능)
- [모니터링 및 로깅](#모니터링-및-로깅)
- [라이선스](#라이선스)
- [팀](#팀)
- [지원](#지원)

## 프로젝트 개요

RunTracker는 Spring 기반의 크루 중심 러닝 애플리케이션 백엔드 서버로 사용자에게 다음을 제공합니다.

- 러닝 크루 생성 및 참여를 통한 그룹 활동
- 거리, 페이스, 심박수 등 상세한 개인 러닝 기록 추적
- AI 기반 추천 러닝 코스 추천
- 크루 기반 실시간 랭킹 시스템
- 커뮤니티를 통한 러닝 성과 공유
- 크루 모임 일정 관리
- 크루 활동에 대한 실시간 푸시 알림

### 주요 성과

- 성능 최적화: 코스 조회 응답시간 90% 개선 (840ms → 84ms, 10배 향상)
- 이미지 최적화: WebP 변환을 통한 최대 95% 파일 크기 감소 (4.5MB → 225KB)
- 실시간 랭킹: Redis 기반 크루 랭킹 시스템으로 응답시간 94% 개선 (34.2ms → 2.0ms, 17배 향상)
- 13개 이상의 알림 시나리오 지원
- GitHub Actions CI/CD와 Docker를 통한 무중단 배포

## 서비스 화면

| 코스 선택 | 코스 런닝 | 런닝 완료 |
|---------|---------|---------|
| ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/main.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/running.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/run_complete.png) |

| 기록 공유 | 러닝 기록 | 기록 상세 |
|---------|---------|---------|
| ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/record_share.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/record.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/record_id.png) |

| 피드 리스트 | 피드 상세 | 크루 관리 |
|---------|---------|---------|
| ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/community.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/feed.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/crew.png) |

| 일정 관리 | 크루 랭킹 | 설정 |
|---------|---------|---------|
| ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/scheduler.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/rank.png) | ![](https://raw.githubusercontent.com/HHG-RunTracker/RunTracker/main/images/setting.png) |

## 아키텍처

RunTracker는 도메인 주도 설계 패턴을 따르며 7개의 핵심 비즈니스 도메인과 전역 계층으로 구성됩니다.

```
┌─────────────────────────────────────────────────────────────┐
│                     API 계층 (Controllers)                    │
├─────────────────────────────────────────────────────────────┤
│  서비스 계층 (비즈니스 로직)                                  │
│  ├── 회원 서비스         ├── 크루 서비스                      │
│  ├── 기록 서비스         ├── 코스 서비스                      │
│  ├── 일정 서비스         ├── 커뮤니티 서비스                  │
│  └── 업로드 서비스       └── 인증 서비스                      │
├─────────────────────────────────────────────────────────────┤
│  저장소 계층 (데이터 접근)                                    │
│  └── Spring Data JPA + QueryDSL                              │
├─────────────────────────────────────────────────────────────┤
│  전역 계층 (횡단 관심사)                                      │
│  ├── JWT 인증 및 토큰 관리                                   │
│  ├── OAuth2 소셜 로그인 (카카오)                             │
│  ├── Firebase Cloud Messaging (FCM)                          │
│  ├── 이벤트 기반 알림                                        │
│  ├── AWS S3 이미지 저장                                      │
│  ├── Redis 캐싱                                              │
│  ├── 예외 처리 및 응답 포맷팅                                │
│  └── 요청/응답 로깅                                          │
├─────────────────────────────────────────────────────────────┤
│               MySQL 데이터베이스 + Redis 캐시                 │
└─────────────────────────────────────────────────────────────┘
```

### 핵심 도메인

#### 1. 회원 (사용자 관리)
- 사용자 프로필 및 인증
- OAuth2 기반 카카오 소셜 로그인
- JWT 토큰 기반 세션 관리
- 푸시 알림을 위한 FCM 토큰 등록
- 사용자 설정 및 선호도 관리
- 러닝 기록 백업/복원 기능

#### 2. 기록 (러닝 메트릭)
- 러닝 기록 생성 및 조회
- 상세 추적: 거리, 시간, 페이스, 좌표, 심박수
- 기간별 통계 (일일, 주간, 월간)
- 좌표 시퀀스를 이용한 러닝 경로 시각화
- 구간별 페이스 분석

#### 3. 코스 (러닝 경로)
- 난이도 정보를 포함한 코스 생성 및 관리
- 사용자 히스토리 및 선호도 기반 추천 시스템
- 지리적 그리드 캐싱을 이용한 근처 코스 (반경 500m)
- 경로 분석 및 코스 상세 정보 관리
- 성능 최적화를 위한 2단계 Redis 캐싱 전략
- AI 서비스와의 연동으로 개인화된 코스 추천

#### 4. 크루 (그룹 관리)
- 크루 생성, 수정, 삭제
- 가입 신청, 승인, 거절 등 회원 관리
- 역할 기반 접근 제어 (리더, 매니저, 일반 회원)
- 회원 승격/강등, 추방, 차단 기능
- 실시간 크루 랭킹 시스템
- 거리 및 시간 기반 개별 크루원 랭킹

#### 5. 일정 
- 크루 활동을 위한 이벤트 생성 및 스케줄(일정) 관리
- 참가자 관리 및 출석 추적
- 일정 수정 및 취소
- 참가자를 위한 다양한 알림 기능

#### 6. 커뮤니티 (소셜 기능)
- 크루 기반 게시판
- 게시글 작성, 수정, 삭제
- 댓글 작성, 수정, 삭제
- 좋아요 기능

#### 7. 업로드 (파일 관리)
- 이미지 업로드 처리 및 유효성 검사
- AWS S3를 이용한 클라우드 저장
- 자동 WebP 형식 변환
- 이미지 리사이징 및 최적화
- 다중 파일 배치 처리

#### 8. 인증 (보안)
- OAuth2 클라이언트 카카오 설정
- JWT 토큰 생성 및 검증
- 토큰 갱신 메커니즘
- 로그아웃 시 토큰 블랙리스트 관리
- 사용자 상세정보 및 권한 확인

## 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-%23ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-%236DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-%2300A0D0?style=for-the-badge&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-%234E9ADE?style=for-the-badge&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

### DevOps & Infrastructure
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

## 성능 최적화

### 1. 코스 조회를 위한 Redis 2단계 캐싱
**문제**: 근처 코스 조회 시 반복된 전체 DB 스캔 (840ms 응답시간)

**해결책**:
- 레벨 1: 그리드 기반 근처 코스 ID 캐싱 (반경 500m)
  - Redis Hash 구조에 코스 ID 캐싱
  - 반경 기반 그리드 인덱싱으로 효율적인 조회
- 레벨 2: 개별 코스 상세정보 캐싱
  - 배치 코스 상세정보 조회를 위한 Redis multiGet
  - N회 왕복을 1회 왕복으로 단축
- 캐싱 테이블 교체 정책: LFU
  - 사용 빈도가 낮은 캐시 항목부터 제거
  - 인기 있는 코스 정보를 우선적으로 메모리에 유지

**성과**:
- 응답시간: 840ms → 84ms (90% 개선, 10배 향상)
- 레벨 1 최적화: 117ms → 20ms (83% 개선, 5.8배 향상)
- 레벨 2 최적화: 121ms → 58ms (52% 개선, 2배 향상)
- 평균 캐시 히트율: 84%

### 2. Hash 기반 슬롯 업데이트 최적화
**문제**: List 구조에서 슬롯 업데이트 시 전체 삭제 후 재생성 필요 (O(N))

**해결책**:
- List 구조에서 Hash 구조로 마이그레이션
- 단일 키 업데이트 방식으로 개선 (O(1) 복잡도)
- Redis 명령 90% 감소 (11회 → 1회)

**성과**:
- 슬롯 업데이트 성능: 10배 향상
- Redis 명령 감소: 90% 감소

### 3. 크루 랭킹을 위한 Redis Sorted Set
**문제**: 반복된 DB 쿼리로 인한 랭킹 계산 (34.2ms 응답시간)

**해결책**:
- 자동 정렬되는 Redis Sorted Set으로 랭킹 데이터 캐싱
- Cache-Aside 패턴 적용 (DB 폴백)
- IN 쿼리를 이용한 배치 회원정보 조회 (N+1 문제 해결)
- 1일 TTL로 자동 캐시 무효화

**성과**:
- 응답시간: 34.2ms → 2.0ms (94% 개선, 17배 향상)
- DB 부하 감소: 98% (반복 조회 시)
- 쿼리 감소: 21회 → 2회 (요청당)

### 4. 이벤트 기반 비동기 알림 시스템
**문제**:
- 알림 발송을 동기 처리 시 메인 비즈니스 로직의 응답 시간 증가
- 크루원 N명에게 알림 전송 시 메인 트랜잭션 대기 시간 발생
- FCM 전송 실패 시 메인 트랜잭션 롤백 위험

**해결책**:
- Spring Event와 @TransactionalEventListener로 알림 로직 분리
- @Async 비동기 처리로 메인 트랜잭션과 알림 전송 독립 실행
- AFTER_COMMIT 단계에서 알림 발송으로 데이터 일관성 보장
- FCM 토큰 유효성 검증 및 예외 처리 로직 추가

**성과**:
- 메인 API 응답 속도 개선 (알림 전송 대기 시간 제거)
- 알림 전송 실패 시에도 메인 비즈니스 로직 정상 처리
- 13가지 알림 시나리오 안정적 운영
- 알림 로직과 비즈니스 로직을 독립적 관리로 유지보수성 향상

### 5. 크루원 랭킹 조회 N+1 쿼리 최적화
**문제**:
- 크루원 랭킹 조회 시 각 회원 정보를 개별로 조회하는 N+1 쿼리 문제 발생
- 20명 크루 기준 21번의 DB 쿼리 발생 (랭킹 1회 + 회원 정보 20회)
- 크루원 수 증가에 비례하여 응답 시간 증가

**해결책**:
- 모든 회원 ID를 먼저 수집하여 IN 쿼리로 한 번에 조회
- Map 자료구조를 활용한 O(1) 시간복잡도 조회
- Stream API를 통한 효율적인 데이터 변환

**성과**:
- DB 쿼리 90% 감소 (21회 → 2회)
- 응답 속도 60% 개선
- 크루원 수 증가에도 일정한 성능 보장

### 6. WebP 이미지 압축
**문제**: 원본 형식 이미지 (JPG, PNG)로 인한 과도한 저장 공간

**해결책**:
- 업로드 시 자동 WebP 형식 변환
- 최대 1920x1920 픽셀로 자동 리사이징
- Scrimage 라이브러리를 이용한 배치 처리

**성과**:
- 이미지 압축: 최대 95% 감소
  - 예: 4.5MB → 225KB (20배 축소)
  - 예: 221KB → 20KB (91% 감소)
- 저장 공간 비용 감소 및 빠른 네트워크 전송

## 보안 기능

### 인증 및 권한
- OAuth2 소셜 로그인: 카카오 제공자와의 보안 토큰 교환
- JWT 토큰 시스템:
  - API 요청을 위한 액세스 토큰
  - 토큰 갱신을 위한 리프레시 토큰
  - 자동 토큰 만료
- 토큰 블랙리스팅: 로그아웃 및 계정 삭제 시 Redis 기반 블랙리스트
- 역할 기반 접근 제어:
  - 크루 레벨 역할 (리더, 매니저, 회원)
  - 메서드 레벨 권한 확인
  - 크루 권한 검증 유틸리티

### 에러 처리
- 커스텀 예외 계층: 도메인별 예외 클래스
- 전역 예외 핸들러: 중앙집중식 오류 처리 및 일관된 응답 형식
- 에러 코드: 표준화된 에러 응답을 위한 CustomErrorCode Enum

## 이벤트 기반 아키텍처

RunTracker는 Spring Event와 @TransactionalEventListener를 이용한 이벤트 기반 알림 시스템을 구현합니다:

### 이벤트 유형

**크루 이벤트**
- CrewJoinRequestCreatedEvent - 가입 신청 제출
- CrewJoinApprovedEvent - 가입 신청 승인
- CrewJoinRejectedEvent - 가입 신청 거절
- CrewMemberRoleChangedEvent - 회원 역할 변경
- CrewMemberLeftEvent - 회원 크루 이탈
- CrewMemberBannedEvent - 회원 차단
- CrewDeletedEvent - 크루 삭제

**일정 이벤트**
- ScheduleCreatedEvent - 새 일정 생성
- ScheduleUpdatedEvent - 일정 정보 수정
- ScheduleParticipantJoinedEvent - 사용자 참가
- ScheduleParticipantCancelledEvent - 사용자 참가 취소
- ScheduleDeletedEvent - 일정 취소

**커뮤니티 이벤트**
- PostCreatedEvent - 새 게시글 발행
- PostUpdatedEvent - 게시글 수정
- PostDeletedEvent - 게시글 삭제
- PostCommentCreatedEvent - 댓글 추가
- PostLikedEvent - 게시글 좋아요

**인증 이벤트**
- OAuth2SuccessEvent - 소셜 로그인 성공

### 이점
- 느슨한 결합: 알림 로직을 비즈니스 로직과 분리
- 비동기 처리: 메인 트랜잭션 완료 후 알림 전달
- 데이터 일관성: 트랜잭션 커밋 후 이벤트 발행 (AFTER_COMMIT 단계)
- 장애 허용: FCM 실패 시 메인 트랜잭션 롤백 없음
- 유지보수성: 13+ 알림 시나리오를 독립적으로 관리

## API 엔드포인트

### 회원 API
```
POST   /api/members/refresh             - 토큰 갱신
POST   /api/members/logout              - 로그아웃
DELETE /api/members/withdrawal          - 계정 탈퇴
GET    /api/members/profile             - 프로필 조회
PATCH  /api/members/update              - 프로필 수정
PATCH  /api/members/notification        - 알림 설정 수정
GET    /api/members/running-setting     - 러닝 설정 조회
PATCH  /api/members/running-setting     - 러닝 설정 수정
POST   /api/members/backup              - 러닝 기록 백업
POST   /api/members/restore             - 러닝 기록 복원
GET    /api/members/backup/info         - 백업 정보 조회
POST   /api/members/fcm-token           - FCM 토큰 등록
POST   /api/members/fcm-token/remove    - FCM 토큰 제거
```

### 기록 API
```
GET    /api/records/summary             - 기간별 기록 조회
GET    /api/records/user                - 모든 기록 조회
GET    /api/records/{recordId}          - 기록 상세 조회
DELETE /api/records/{recordId}          - 기록 삭제
```

### 코스 API
```
POST   /api/courses/save                - 코스 저장
GET    /api/courses/nearby              - 근처 코스 조회
GET    /api/courses/{courseId}          - 코스 상세 조회
POST   /api/courses/{courseId}/running  - 정해진 코스로 러닝 시작
POST   /api/courses/finish              - 러닝 완료 저장
GET    /api/courses/recommend/record    - 기록 기반 추천
GET    /api/courses/recommend/setting   - 설정 기반 추천
PATCH  /api/courses/{courseId}          - 코스 수정
DELETE /api/courses/{courseId}          - 코스 삭제
```

### 크루 API
```
POST   /api/crew/create                 - 크루 생성
POST   /api/crew/join/{crewId}          - 크루 가입 신청
POST   /api/crew/join/cancel/{crewId}   - 가입 신청 취소
POST   /api/crew/approval/{crewId}      - 가입 신청 승인/거절
POST   /api/crew/member/role/{crewId}   - 크루원 역할 변경
PATCH  /api/crew/update/{crewId}        - 크루 정보 수정
DELETE /api/crew/delete/{crewId}        - 크루 삭제
POST   /api/crew/ban/{crewId}           - 크루원 차단
POST   /api/crew/leave/{crewId}         - 크루 탈퇴
GET    /api/crew/list                   - 모든 크루 목록
GET    /api/crew/search                 - 크루 검색
GET    /api/crew/{crewId}               - 크루 상세 정보
GET    /api/crew/list/pending/{crewId}  - 대기 중인 신청자
GET    /api/crew/list/banned/{crewId}   - 차단된 크루원
GET    /api/crew/member/{memberId}      - 크루원 프로필
GET    /api/crew/ranking/list           - 크루 일일 랭킹
POST   /api/crew/ranking/recalculate    - 크루 랭킹 재계산
GET    /api/crew/{crewId}/member-ranking - 크루원 랭킹
POST   /api/crew/{crewId}/member-ranking/recalculate - 크루원 랭킹 재계산
```

### 일정 API
```
POST   /api/schedules/create            - 일정 생성
GET    /api/schedules/list              - 일정 목록
GET    /api/schedules/{scheduleId}      - 일정 상세 조회
PATCH  /api/schedules/update/{scheduleId} - 일정 수정
DELETE /api/schedules/delete/{scheduleId} - 일정 취소
POST   /api/schedules/join/{scheduleId}    - 일정 참가
POST   /api/schedules/cancel/{scheduleId}  - 일정 참가 취소
GET    /api/schedules/participants/{scheduleId} - 참가자 목록
```

### 커뮤니티 API
```
POST   /api/community/posts             - 게시글 작성
PATCH  /api/community/posts/{postId}    - 게시글 수정
DELETE /api/community/posts/{postId}    - 게시글 삭제
POST   /api/community/posts/{postId}/like   - 게시글 좋아요
POST   /api/community/posts/{postId}/unlike - 게시글 좋아요 취소
GET    /api/community/posts             - 게시글 목록
GET    /api/community/posts/{postId}    - 게시글 상세
GET    /api/community/posts/search      - 게시글 검색
POST   /api/community/posts/{postId}/comments - 댓글 작성
PATCH  /api/community/comments/{commentId}   - 댓글 수정
DELETE /api/community/comments/{commentId}   - 댓글 삭제
```

### 업로드 API
```
POST   /api/upload/image                - 이미지 업로드
GET    /api/upload/image/{filename}     - 이미지 조회
```

## 프로젝트 구조

```
runtracker/
├── src/
│   ├── main/
│   │   ├── java/com/runtracker/
│   │   │   ├── domain/                 # 비즈니스 도메인
│   │   │   │   ├── member/
│   │   │   │   ├── record/
│   │   │   │   ├── course/
│   │   │   │   ├── crew/
│   │   │   │   ├── schedule/
│   │   │   │   ├── community/
│   │   │   │   ├── upload/
│   │   │   │   └── auth/
│   │   │   └── global/                 # 횡단 관심사
│   │   │       ├── config/
│   │   │       ├── exception/
│   │   │       ├── jwt/
│   │   │       ├── fcm/
│   │   │       ├── security/
│   │   │       ├── response/
│   │   │       └── util/
│   │   └── resources/
│   │       ├── application.yml         # 메인 설정
│   │       ├── messages.properties     # 알림 메세지
│   │       ├── firebase/               # FCM 서비스 설정
│   │       └── static/                
│   └── test/
│       └── java/                       # 통합 및 컨트롤러 테스트
├── build.gradle                        
├── gradle/                             
├── Dockerfile                         
├── docker-compose.yml                  
└── README.md                           
```

## 구현된 주요 기능

### 인증 및 사용자 관리
- OAuth2 소셜 로그인 (카카오)
- JWT 기반 세션 관리
- 사용자 프로필 관리
- 로그아웃 시 토큰 블랙리스팅
- FCM 디바이스 토큰 등록

### 러닝
- 기록 추적 (거리, 페이스, 시간, 심박수)
- 기간별 통계 (일일, 주간, 월간)
- GPS 좌표를 이용한 경로 시각화
- 구간별 페이스 분석

### 코스 관리
- 코스 생성 및 조회
- 캐싱을 이용한 근처 코스 발견
- AI 기반 개인화 추천

### 크루/그룹 기능
- 러닝 크루 생성 및 관리
- 역할 기반 회원 권한
- 실시간 크루 랭킹
- 크루 내 회원 랭킹
- 추방 및 차단 관리

### 소셜 커뮤니티
- 크루 기반 게시판
- 댓글 및 토론
- 좋아요 반응 시스템
- 콘텐츠 공유

### 푸시 알림
- FCM 기반 실시간 알림
- 이벤트 기반 아키텍처
- 13+ 알림 시나리오
- 비동기 전달

### 이미지 관리
- S3 기반 클라우드 저장
- 자동 WebP 변환
- 이미지 리사이징 및 최적화
- 배치 업로드 지원

## 모니터링 및 로깅

### 요청 로깅
- Logbook을 통한 모든 HTTP 요청/응답 로깅
- 구조화된 로깅을 위한 커스텀 JSON 포매터
- 비밀번호, 토큰 등 민감 데이터 마스킹

### 애플리케이션 로깅
- Logback 설정을 갖춘 SLF4J
- Logstash 인코더를 통한 구조화된 로깅
- 환경 기반 로그 레벨

## 라이선스

이 프로젝트는 동양미래대학교 형해조 졸업작품 프로젝트로 개발된 소유권 있는 소프트웨어입니다.

## 팀

- 백엔드: 김준형
- 프론트엔드: 이승재, 김경훈
- AI: 김명준


## 지원

[![Gmail Badge](https://img.shields.io/badge/Gmail-d14836?style=for-the-badge&logo=Gmail&logoColor=white&link=mailto:kjunh972@gmail.com)](mailto:kjunh972@gmail.com)
