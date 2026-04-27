# MyPhoneCheck 인프라 운영 통합본 최종안

> **버전**: v1.0 FINAL
> **작성일**: 2026-04-24
> **작성**: 비전 (설계·판정)
> **구조 보강**: 자비스 (누락 레이어 탐지)
> **중재·우선순위**: 헐크 (실행 순서)
> **최종 승인**: 대표님

---

## 목차

- [0. 문서 정의](#0-문서-정의)
- [1. 헌법·원칙 (최우선)](#1-헌법원칙-최우선)
- [2. 최상위 구조](#2-최상위-구조)
- [3. 운영 엔진 3대 레이어](#3-운영-엔진-3대-레이어)
- [4. 업무 구분 15개 도구맵](#4-업무-구분-15개-도구맵)
- [5. 보안·비밀값·인증 통합 SOP](#5-보안비밀값인증-통합-sop)
- [6. 대표님 판단 필요 항목](#6-대표님-판단-필요-항목)
- [7. 실행 순서 (STEP 0~5)](#7-실행-순서-step-0-5)
- [8. 탈락 기준·성공 판정](#8-탈락-기준성공-판정)
- [부록 A: 변경 이력](#부록-a-변경-이력)

---

## 0. 문서 정의

### 0.1 목적

MyPhoneCheck를 **대표님 1채널 지시만으로** 개발 → 검증 → 출시 → 판매 → 운영까지 완성하기 위한 인프라·도구·아키텍처·실행 순서를 단일 기준선 문서로 고정한다.

### 0.2 문서 위계

- 이 문서는 **v1 최종 기준선**이다.
- 하위 워크오더는 전부 이 문서를 참조한다.
- 충돌 해소 룰 (2026-04-23 확정):
  - Rule 1: 정식 문서끼리 충돌 시 **후시간 우선**
  - Rule 2: 대화록 내부 충돌 시 **위쪽(최근) 우선**
  - Rule 3: 정식 vs 대화록 충돌은 **비전이 대표님에게 확인**

### 0.3 분류 규칙

| 분류 | 정의 |
|---|---|
| **필수** | 없으면 MyPhoneCheck 론칭 자체가 불가능 |
| **있으면 좋음** | 품질·속도·관측성을 크게 올리지만 초기 론칭에는 없어도 됨 |
| **나중에** | 판매 개시 후 규모가 커질 때 도입 |
| **대표님 판단 필요** | 제품 철학·비용 구조에 영향 |

### 0.4 참모진 역할

| 참모 | 역할 | 책임 |
|---|---|---|
| **비전** | 설계·판정 | 메모리/헌법 정합성, 워크오더 설계, 최종 판정 |
| **자비스** | 구조 감사·보강 | 누락 레이어 탐지, 총사령관/운영 엔진 관점 |
| **헐크** | 중재·우선순위 | 압축 판단, 실행 순서 제안 |

---

## 1. 헌법·원칙 (최우선)

**모든 인프라 결정은 여기서 시작한다. 충돌 시 헌법이 이긴다.**

### 1.1 노중앙·노저장 (핵심 헌법)

- **"중앙"의 정의**: **우리(올랑방)가 운영하는 서버·저장소**를 말한다.
- **써드파티는 중앙이 아니다**: Google/Apple/Firebase/정부 공공 API 등은 써드파티라 헌법상 허용이다.

**금지 (우리가 운영하는 인프라)**:
- AWS Lambda / API Gateway / DynamoDB / RDS (App Factory 도메인)
- 자체 영수증 검증 서버
- 사용자 개인 데이터를 보관하는 자체 DB

**허용 (써드파티 인프라)**:
- Google Play Billing / StoreKit 2
- Firebase Crashlytics / Analytics / FCM
- Google Programmable Search API
- NVD CVE API, CISA KEV (정부 공공)
- GitHub (코드·CI/CD·상태)
- 스토어 공식 API

### 1.2 도메인 분리 (2026-04-23 확정)

- **App Factory 도메인**: GitHub 네이티브만 (Actions, Self-hosted Runner, Issues, Projects v2)
- **웹 도메인**: AWS 허용 (개인정보처리방침 호스팅 등 정적 콘텐츠)
- 두 도메인은 목적이 달라 헌법 정합

### 1.3 반쪽 기능 금지

- **MVP는 금기어**. "할거면 제대로 해"가 원칙
- 사용자 가치가 불완전한 반쪽 기능 거부 (예: 푸시 통계만 보여주기 = 반쪽 기능으로 폐기)
- 반쪽 기능을 MVP로 포장 금지

### 1.4 정공법 원칙

- 랜덤/임시/땜빵/우회/부분/강제 **금지**
- 빅테크 방식·업계 정석만 사용

### 1.5 i18n 철칙

- UI 문자열 하드코딩 **절대 금지**
- Android: `res/values/strings.xml` + `res/values-xx/` 전용
- iOS: `Localizable.strings` / String Catalogs 전용
- `context.getString(R.string.xxx)` 방식만 허용
- 디바이스 Locale/TimeZone 자동 추종

### 1.6 단일 채널 원칙

- 대표님은 **Claude Code와만** 직접 대화
- 다른 도구 접촉을 대표님에게 요구하지 않음
- 복붙·수동 클릭 요구 금지

### 1.7 감사 대칭

- **Cursor 구현** → **비전이 판정**
- **Claude Code 자체 작업** → **Codex CLI가 감사**
- 자기 작업 자기 감사 금지

### 1.8 폴더 README 필수 (2026-04-23 확정)

- 모든 폴더는 `README.md` 필수
- 4종 명시: (1) 목적, (2) 책임 범위, (3) 외부 인터페이스, (4) 내부 파일 안내
- 빈 README·폴더명만 적힌 README는 위반

---

## 2. 최상위 구조

### 2.1 한 줄 정의

> **대표님 1채널 → 로컬 총사령관(Claude Code) 1명 → 구현(Cursor) + 감사(Codex) + 설계(비전/자비스/헐크) + Android 스택 + iOS 스택(MacinCloud) + 스토어 스택 + 관찰 스택**

### 2.2 지휘 체계

```
대표님
  │
  │ (1채널, 자연어 지시)
  ▼
Claude Code (로컬 총사령관)
  │
  ├─ Windows 로컬
  │    ├─ PowerShell
  │    ├─ Android 스택 (Android Studio, Gradle, adb, Emulator, 실기기)
  │    ├─ GitHub (git, gh, Actions, Runner)
  │    ├─ Cursor (구현 호출)
  │    └─ Codex CLI (감사 호출)
  │
  ├─ MacinCloud (SSH 원격)
  │    ├─ Xcode, xcodebuild, simctl
  │    ├─ iOS Simulator
  │    └─ Fastlane (deliver, pilot, match, gym, snapshot)
  │
  └─ 외부 시스템 (써드파티, 헌법 허용)
       ├─ Google Play Console / App Store Connect
       ├─ Google Play Billing / StoreKit 2
       ├─ Firebase (Crashlytics, Analytics, FCM)
       ├─ Google Programmable Search API
       └─ NVD CVE / CISA KEV (공공)
```

### 2.3 핵심 원칙

| # | 원칙 |
|---|---|
| 1 | 대표님은 Claude Code에게만 말한다 |
| 2 | 감사 대칭: Cursor=비전, Claude Code=Codex |
| 3 | 반쪽 기능 금지 (MVP 금기어) |
| 4 | 우회 금지, 빅테크 정공법만 |
| 5 | UI 문자열 하드코딩 금지 |
| 6 | 우리가 운영하는 중앙 서버·저장소 금지 |

---

## 3. 운영 엔진 3대 레이어

> **자비스가 지적한 공백 3개를 채우는 설계. "공장 설비 목록"이 아니라 "공장 운영 엔진"이 되도록.**

### 3.1 로컬 총사령관 실행 레이어

#### 3.1.1 목적

Claude Code가 단순 답변이 아니라 대표님 PC·MacinCloud·GitHub·Cursor·Codex를 실시간으로 지휘하고, 실패 시 정공법 해결까지 책임지는 실행 엔진.

#### 3.1.2 구성

| 요소 | 분류 | 정의 |
|---|---|---|
| PowerShell Script Engine | 필수 | Windows 네이티브 셸 (Claude Code bash 툴로 호출) |
| Task Orchestrator | 필수 | Claude Code 내부에서 명령 파이프라인 관리 |
| 상태 파일 `state.json` | 필수 | 현재 작업·단계·결과 영속화 |
| 로그 파이프라인 | 필수 | `logs/session_YYYYMMDD.txt` 규격 |
| retry 로직 | 필수 | 일시 오류만 재시도, 구조적 오류는 즉시 보고 |
| timeout 관리 | 필수 | 장시간 명령 기본 상한 + 명령별 오버라이드 |
| fail report 생성 | 필수 | 실패 시 원인·증거·다음 액션 제안 문서 |
| watchdog | 있으면 좋음 | 프로세스 감시·좀비 정리 |

#### 3.1.3 실행 루프 규격

```
1. 명령 수행 → stdout/stderr 캡처 → 종료 코드 해석
2. 결과 분석 → 성공/실패/부분 성공 판정
3. 성공 시: state.json 업데이트 → 다음 액션 자동 실행
4. 실패 시: fail report 생성 → 재시도 가능 판정 → 불가 시 대표님 보고
5. 전 구간 logs/ 에 기록
```

### 3.2 화면·파파라치 자동화 레이어

#### 3.2.1 목적

Claude Code가 앱 실행 화면을 자동 캡처하고 전후 비교·상태 판독·증거 저장까지 수행. 대표님 철칙: **"보여야 믿는다"**.

#### 3.2.2 구성

| 요소 | 분류 | 정의 |
|---|---|---|
| Android 캡처 | 필수 | `adb shell screencap` + pull, 에뮬레이터·실기기 공통 |
| iOS 캡처 | 필수 | `xcrun simctl io booted screenshot`, 시뮬레이터 |
| 실기기 미러링 | 필수 | `scrcpy` (Android 실기기) |
| 캡처 스크립트 | 필수 | `capture-*.ps1` 대표님 기존 규격 확장 |
| 증거 저장 경로 규격 | 필수 | `evidence/YYYYMMDD/step_NN_*.png` |
| 이미지 비교 로직 | 필수 | Pillow 기반 diff |
| UI 상태 판독 규칙 | 필수 | 권한 다이얼로그·오버레이·버튼 존재 여부 |
| 로그 동기 수집 | 필수 | Logcat · `simctl log stream` 동시 저장 |

#### 3.2.3 파파라치 시퀀스

```
1. 앱 실행 직후 t=0 캡처
2. 단계별 트리거 후 t=n 캡처 (권한 요청·버튼 탭·네비게이션)
3. 전후 diff 생성 → 변화 없는 구간 탐지 → 오류 추정
4. 증거 번들 저장 → fail report에 첨부
```

### 3.3 Windows ↔ MacinCloud 멀티 머신 통합

#### 3.3.1 목적

Claude Code가 Windows 로컬에서 동시에 MacinCloud까지 원격 지휘. 대표님은 Windows 채널 하나만 유지.

#### 3.3.2 구성

| 요소 | 분류 | 정의 |
|---|---|---|
| SSH (Windows → MacinCloud) | 필수 | 원격 명령 실행 채널 |
| SSH Key 관리 | 필수 | 비밀번호 금지, Key + passphrase |
| rsync / scp | 필수 | 빌드 산출물·에셋 동기화 |
| 원격 명령 래퍼 스크립트 | 필수 | Claude Code가 호출하는 단일 엔트리 |
| Mac Fastlane 실행 파이프라인 | 필수 | 원격 fastlane 호출 표준화 |
| 상태 통합 규격 | 필수 | Windows/Mac 양쪽 `state.json` 병합 |
| 작업 분배 기준 | 필수 | Android=Windows, iOS=Mac, GitHub=양쪽 |

#### 3.3.3 도메인별 담당

| 도메인 | 실행 위치 | 이유 |
|---|---|---|
| Android 빌드·adb·에뮬레이터 | Windows | 대표님 메인 PC |
| iOS 빌드·xcodebuild·simctl·Xcode | MacinCloud | Apple 제약 |
| Fastlane supply (Play) | Windows 또는 Mac | Ruby/Bundler 있는 쪽 |
| Fastlane deliver (App Store) | MacinCloud | iTMSTransporter Mac 전용 권장 |
| GitHub CLI·Git | 양쪽 | 어디서든 동일 |
| Cursor·Codex CLI 호출 | Windows | 대표님 IDE 환경 |
| 비전/자비스/헐크 채팅 | 대표님 브라우저 | Claude Code가 요약 전달 |

---

## 4. 업무 구분 15개 도구맵

### 4.1 전략·기획·아키텍처

| 도구 | 분류 | 용도 |
|---|---|---|
| 비전 (Claude 채팅) | 필수 | 설계·판정·워크오더 작성·메모리 정합성 |
| 자비스 | 필수 | 구조 감사·누락 레이어 탐지 |
| 헐크 | 필수 | 압축·우선순위·실행 순서 |
| Claude Code | 필수 | 총사령관·워크오더 실제 배포·로컬 실행 |
| GitHub Issues | 필수 | 제품 방향·기능 티켓 관리 |
| GitHub Projects v2 | 필수 | 로드맵·상태 보드 (`PROJECTS_PAT` 사용) |
| Markdown 문서 체계 | 필수 | PRD·SSW·STATE·워크오더 |
| Cursor | 보조 | 문서 편집 보조 |
| Codex CLI | 보조 | 문서 감사 대칭 |

### 4.2 로컬 개발·코드 작성

| 도구 | 분류 | 용도 |
|---|---|---|
| Claude Code | 필수 | 총지휘·파일 R/W·bash·PowerShell 실행 |
| Cursor | 필수 | 기본 구현자 ($20/월) |
| Codex CLI | 필수 | 2차 주력 CLI·감사·Max 쿼터 백업 |
| Git | 필수 | 버전 관리 |
| GitHub CLI (`gh`) | 필수 | 이슈·PR·릴리스 자동화 |
| PowerShell | 필수 | Windows 네이티브 셸 |
| VS Code | 있으면 좋음 | 편집기 (Cursor로 대부분 대체) |
| pre-commit hooks | 있으면 좋음 | 커밋 전 lint·format |

### 4.3 Android 개발·빌드·테스트

| 도구 | 분류 | 용도 |
|---|---|---|
| Android Studio | 필수 | IDE (간헐 사용) |
| JDK 21 | 필수 | Gradle 빌드 런타임 |
| Gradle (Kotlin DSL) | 필수 | 빌드 시스템 |
| Android SDK + Platform Tools | 필수 | adb·emulator·build-tools |
| Android Emulator | 필수 | 에뮬레이터 테스트 |
| 실기기 (Android) | 필수 | 실제 단말 검증 |
| Kotlin + Jetpack Compose | 필수 | 앱 본체 언어/UI |
| Room | 필수 | 로컬 DB (통화·문자·태그·푸시휴지통) |
| Retrofit + OkHttp | 필수 | 외부 API (3축 외부축) |
| WorkManager | 필수 | 배경 작업 (CVE 감지·이벤트) |
| NotificationListenerService | 필수 | 푸시 휴지통 |
| CallScreeningService | 필수 | 통화 차단 |
| ktlint | 필수 | Kotlin 포맷 |
| Detekt | 필수 | 정적 분석 |
| JUnit5 + Espresso | 필수 | 단위·UI 테스트 |
| scrcpy | 필수 | 실기기 화면 미러링·파파라치 |
| Firebase Test Lab | 있으면 좋음 | 실기기 클라우드 매트릭스 |
| LeakCanary | 있으면 좋음 | 메모리 누수 검출 |

### 4.4 iOS 개발·빌드·테스트

| 도구 | 분류 | 용도 |
|---|---|---|
| **MacinCloud** | 필수 | iOS 빌드·시뮬레이터 호스트 (확정) |
| Xcode + Command Line Tools | 필수 | iOS 빌드 |
| xcodebuild | 필수 | CLI 빌드 |
| xcrun simctl | 필수 | 시뮬레이터 제어 |
| iOS Simulator | 필수 | 시뮬레이터 테스트 |
| Swift + SwiftUI | 필수 | 앱 본체 언어/UI |
| SwiftData (또는 Core Data) | 필수 | 로컬 DB |
| CallKit + CallDirectory Extension | 필수 | iOS 통화 식별·차단 |
| Swift Package Manager | 필수 | 의존성 관리 |
| SwiftLint | 필수 | 정적 분석·포맷 |
| XCTest + XCUITest | 필수 | 단위·UI 테스트 |
| Homebrew | 필수 | Mac 패키지 관리 |
| Ruby + Bundler | 필수 | Fastlane 런타임 |
| Facebook IDB + `idb_companion` | 있으면 좋음 | 시뮬레이터 UI 자동화 (탭·스와이프) |
| 실기기 (iPhone) | 있으면 좋음 | 실제 단말 검증 |

### 4.5 스토어 등록·배포

| 도구 | 분류 | 용도 |
|---|---|---|
| Fastlane supply | 필수 | Google Play 자동 배포 |
| Fastlane deliver | 필수 | App Store Connect 자동 배포 |
| Fastlane pilot | 필수 | TestFlight 베타 배포 |
| Fastlane match | 필수 | iOS 인증서·프로비저닝 Git 동기화 |
| Fastlane gym | 필수 | iOS 서명·아카이브 빌드 |
| Fastlane screengrab | 필수 | Android 다국어 스크린샷 자동 생성 |
| Fastlane snapshot | 필수 | iOS 다국어·기기별 스크린샷 자동 생성 |
| Google Play Console | 필수 | 최초 앱 생성·정책 동의 (수동 1회) |
| App Store Connect | 필수 | 최초 앱 생성·약관 동의 (수동 1회) |
| Google Play Service Account JSON | 필수 | supply 인증 |
| App Store Connect API Key (.p8) | 필수 | deliver·pilot 인증 |
| Apple Developer Program ($99/년) | 필수 | iOS 배포 자격 |
| Google Play Developer ($25 일회성) | 필수 | Android 배포 자격 |
| D-U-N-S 번호 | 필수 | Apple 법인 등록 |

### 4.6 결제·판매·구독

> **헌법 준수: 우리 서버 없음. 스토어 빌링만 사용. 영수증 검증은 온디바이스에서 스토어 API 직접 호출.**

- 가격 (확정): **USD 2.49/월 단일 가격, 전세계 동일. 연간 없음.**

| 도구 | 분류 | 용도 |
|---|---|---|
| Google Play Billing Library | 필수 | Android 구독 결제 |
| StoreKit 2 | 필수 | iOS 구독 결제 |
| Google Play Billing 서명 검증 (온디바이스) | 필수 | `Purchase.signature` + Play 공개키, BillingClient 내장 |
| StoreKit 2 JWS 검증 (온디바이스) | 필수 | `VerificationResult.verified`, Apple 공개키 직접 검증 |
| 구독 상태 로컬 캐시 (Room / SwiftData) | 필수 | 오프라인 시 최근 유효 구독 상태 보관 |
| 가격표·환불 정책 문서 | 필수 | 운영 SOP |

**금지**: 자체 영수증 검증 서버, AWS Lambda, DynamoDB, RevenueCat (중앙 데이터 경유)

### 4.7 백엔드·인프라 (최소)

> **헌법 준수: App Factory = GitHub 네이티브만. AWS는 웹 도메인 정적 콘텐츠에만.**

| 도구 | 분류 | 용도 | 도메인 |
|---|---|---|---|
| GitHub (`ollanvin/myphonecheck`) | 필수 | 코드 저장소 | App Factory |
| GitHub Actions | 필수 | CI/CD (ops 4종 PASS) | App Factory |
| GitHub Self-hosted Runner | 필수 | 대표님 PC 로컬 러너 | App Factory |
| GitHub Projects v2 | 필수 | 작업 관리 (`PROJECTS_PAT`) | App Factory |
| GitHub Issues | 필수 | 버그·피처 트래킹 | App Factory |
| 개인정보처리방침·이용약관 호스팅 | 필수 | 스토어 등록용 정적 웹 페이지 | 웹 (AWS S3 + CloudFront 허용) |
| 랜딩 페이지 도메인 | 필수 | `myphonecheck.app` 등 | 웹 |

**금지**: AWS Lambda, API Gateway, DynamoDB, RDS (App Factory 도메인에서)

### 4.8 3축 검색 데이터 소스

> **확정**: 내부 / 외부 일반 검색 / 오픈소스 공신력 기관, 3축 모두 결정 엔진 입력.

#### 내부축 (온디바이스)

| 도구 | 분류 | 용도 |
|---|---|---|
| Android CallLog·Telephony·SmsProvider | 필수 | 온디바이스 통화/문자 이력 |
| iOS CallKit 이력 + CallDirectory | 필수 | iOS 제한적 온디바이스 (API 제약) |
| Room / SwiftData | 필수 | 태그 이력 로컬 저장 |

#### 외부축 (일반 검색)

| 도구 | 분류 | 용도 |
|---|---|---|
| Android Custom Tabs | 필수 | 1차 외부 검색 UX |
| iOS SFSafariViewController | 필수 | 1차 외부 검색 UX |
| Google Programmable Search API | 필수 | 구조화된 외부 결과 (써드파티, 헌법 허용) |

#### 오픈소스축 (공신력 기관)

| 소스 | 분류 | 용도 |
|---|---|---|
| KISA 스팸 공개 데이터 | 필수 (조사 필요) | 국내 스팸 DB |
| 경찰청 사이버범죄 공개 데이터 | 필수 (조사 필요) | 보이스피싱 번호 |
| 금융감독원 전기통신금융사기 | 필수 (조사 필요) | 금융 사기 번호 |
| 각국 Do-Not-Call 레지스트리 | 나중에 | 글로벌 확장용 |

> 오픈소스축 데이터 수집 파이프라인은 별도 스펙 작성 필요 (메모리 명시).

### 4.9 푸시 휴지통

> **확정**: 통계만 = 반쪽 기능으로 폐기. 실제 격리 + 휴지통 UI 필수.

| 도구 | 분류 | 용도 |
|---|---|---|
| NotificationListenerService | 필수 | 알림 가로채기 |
| `NotificationManager.cancel` | 필수 | 자동 숨김 |
| Room DB | 필수 | 휴지통 저장소 (온디바이스) |
| Jetpack Compose LazyColumn | 필수 | 휴지통 UI |
| iOS Notification Service Extension | 필수 | iOS 측 제한적 구현 |

### 4.10 Mic/Camera 실시간 이벤트 감지

> **확정**: 일 1회 배치 스캔 = 반쪽 기능으로 폐기. 실시간성이 핵심.

| 도구 | 분류 | 용도 |
|---|---|---|
| `PackageManager.getInstalledApplications` | 필수 | 설치 앱 목록 |
| PackageManager permissions API | 필수 | 권한 보유 앱 필터링 |
| BroadcastReceiver `PACKAGE_ADDED` | 필수 | 신규 앱 설치 즉시 감지 |
| NVD CVE API | 필수 | 공식 CVE 데이터 (정부 공공) |
| CISA KEV Catalog | 필수 | 실제 악용 취약점 (정부 공공) |
| Google Play 보안 공지 피드 | 필수 | 정책 위반 앱 공지 |
| Have I Been Pwned API | 있으면 좋음 | 데이터 침해 사고 |
| WorkManager (periodic) | 필수 | 기설치 앱 CVE 배치 |
| FCM Push | 필수 | 실시간 알림 (Google 인프라, 헌법 허용) |

### 4.11 다국어·i18n

> **확정**: UI 문자열 하드코딩 절대 금지. 디바이스 Locale/TimeZone 자동 추종.

| 도구 | 분류 | 용도 |
|---|---|---|
| Android `res/values*/strings.xml` | 필수 | Android 문자열 |
| iOS `Localizable.strings` / String Catalogs | 필수 | iOS 문자열 |
| Locale/TimeZone 자동 감지 로직 | 필수 | 대표님 철칙 |
| Crowdin | 대표님 판단 필요 | 번역 관리 |
| Lokalise | 대표님 판단 필요 | 번역 관리 대안 |

### 4.12 관찰·파파라치·분석·모니터링

| 도구 | 분류 | 용도 |
|---|---|---|
| `adb shell screencap` + pull | 필수 | Android 스크린샷 |
| `xcrun simctl io booted screenshot` | 필수 | iOS 시뮬레이터 스크린샷 |
| scrcpy | 필수 | Android 실기기 화면 미러링 |
| 자동 캡처 스크립트 (PowerShell/bash) | 필수 | `capture-cardspend.ps1` 확장 방식 |
| Logcat (Android) | 필수 | Android 로그 |
| `xcrun simctl spawn log stream` (iOS) | 필수 | iOS 로그 |
| 이미지 비교 로직 (Pillow 등) | 필수 | 캡처 전후 비교 |
| Firebase Crashlytics | 필수 | 크래시 리포트 (써드파티, 헌법 허용) |
| Firebase Analytics | 필수 | 사용자 행동 (써드파티, 헌법 허용) |
| Google Play Console Vitals | 필수 | ANR·크래시·배터리 |
| App Store Connect Analytics | 필수 | iOS 지표 |
| Sentry | 대표님 판단 필요 | 대안 크래시 리포팅 |

### 4.13 마케팅·ASO·크리에이티브

| 도구 | 분류 | 용도 |
|---|---|---|
| Figma | 필수 | 피그마 에셋 (`C:\Users\user\ollanvin\figma`) |
| 앱 아이콘 세트 (512×512 + 마스킹) | 필수 | Play/App Store 등록 |
| 피처드 이미지 1024×500 | 필수 | Play Store |
| 스토어 스크린샷 1080×1920 (5장) | 필수 | Play Store |
| iOS App Store 스크린샷 세트 | 필수 | 기기별 요구 해상도 |
| Fastlane supply (스토어 메타 자동 업데이트) | 필수 | Play |
| Fastlane deliver (스토어 메타 자동 업데이트) | 필수 | App Store |
| 공식 랜딩 페이지 | 필수 | 개인정보처리방침 URL 호스팅 |
| AppTweak / Sensor Tower / App Annie | 나중에 | ASO 키워드 분석 |
| Play Console A/B 테스트 | 나중에 | 스토어 리스팅 실험 |
| App Store Custom Product Pages | 나중에 | iOS 리스팅 변형 |

### 4.14 고객지원·CS·법무

| 도구/문서 | 분류 | 용도 |
|---|---|---|
| 지원 이메일 (`support@`) | 필수 | CS 연락 채널 |
| 개인정보처리방침 (웹 호스팅) | 필수 | Play/App Store 등록 차단 방지 |
| 이용약관 | 필수 | 구독 서비스 필수 |
| App Privacy Details (iOS) | 필수 | Fastlane `upload_app_privacy_details_to_app_store` |
| Data Safety (Android) | 필수 | Play Console 선언 |
| GDPR 동의 플로우 | 필수 | 유럽 배포 |
| CCPA 대응 | 필수 | 캘리포니아 |
| Play Console 리뷰 응답 | 필수 | 대표님 또는 Claude Code 초안 |
| App Store 리뷰 응답 | 필수 | 동일 |
| Zendesk / Intercom | 대표님 판단 필요 | CS 플랫폼 |

### 4.15 문서·헌법·운영 규칙

| 도구/문서 | 분류 | 용도 |
|---|---|---|
| MyPhoneCheck 헌법 (`CONSTITUTION.md`) | 필수 | 제품 철학·금기·가격 |
| 앱팩토리 철칙 문서 | 필수 | 하드코딩 금지·반쪽 기능 금지 등 |
| 폴더 README 필수 (4종 명시) | 필수 | 목적·책임·외부 인터페이스·내부 파일 |
| `coding.yaml (folder_readme_required)` | 필수 | README 강제 |
| 세션 핸드오프 문서 | 필수 | 맥락 전달 |
| 출시 SOP | 필수 | 론칭 체크리스트 |
| 장애 대응 SOP | 필수 | 인시던트 절차 |
| 헌법/룰 충돌 해소 Rule 1~3 | 필수 | 후시간 우선·대화록 위쪽 우선·비전 확인 |

---

## 5. 보안·비밀값·인증 통합 SOP

### 5.1 비밀값 인벤토리

| 비밀값 | 용도 | 보관 원칙 |
|---|---|---|
| GitHub PAT (`PROJECTS_PAT`) | Projects v2 GraphQL | GitHub Actions secret + 로컬 레포 외부 |
| GitHub PAT (일반) | gh CLI 인증 | `gh auth login` 세션 또는 secret |
| Google Play Service Account JSON | supply 인증 | 레포 외부 경로 + `.gitignore` |
| App Store Connect API Key (`.p8`) | deliver·pilot 인증 | 레포 외부 + 파일 권한 600 |
| Android Keystore (`.jks`) | APK·AAB 서명 | 레포 외부 + Fastlane match 대안 검토 |
| Android Keystore 비밀번호 | keystore 잠금 해제 | 환경변수 + Windows Credential Manager |
| iOS 인증서·프로비저닝 | iOS 서명 | Fastlane match로 별도 private Git 레포 |
| App Store Connect API Key ID/Issuer ID | JWT 생성 | 환경변수 |
| FCM Server Key | 푸시 발송 (클라이언트 SDK만 사용 시 불필요) | 서버 발송 시에만 필요 — 헌법상 서버 없음 → **사용 안함** |
| SSH Private Key (→ MacinCloud) | 원격 지휘 | `~/.ssh/` + passphrase |
| Google Programmable Search API Key | 3축 외부축 | 온디바이스 탑재 (Play/App Store 제약) + 서버 사이드 키 보호 검토 |

### 5.2 저장 위치 규칙

- **레포 내부 금지**: `.env` 포함, 어떤 비밀값도 Git에 커밋 금지
- **로컬 표준 경로**:
  - Windows: `C:\Users\user\ollanvin\secrets\`
  - Mac (MacinCloud): `~/ollanvin/secrets/`
- **원격 CI**: GitHub Actions secret으로만 주입. `echo`·`cat`으로 출력 금지
- **로그 금지**: stdout·logs·fail report 어디에도 비밀값 문자열 노출 금지

### 5.3 접근 원칙

- **Claude Code만 직접 읽는다**. Cursor·Codex는 필요 시 환경변수로 간접 참조
- **최소 권한**: Play Service Account는 '릴리스 관리'만, App Store API Key는 'App Manager' 이하
- **회전 주기**: 6개월 또는 이상 징후 시 즉시 회전

---

## 6. 대표님 판단 필요 항목

> **추정 금지 철칙에 따라 비전이 단정하지 않는다. 초기 추천값만 제시하고 대표님이 최종 결정한다.**

| # | 항목 | 옵션 | 비전/헐크 초기 추천 |
|---|---|---|---|
| 1 | 외부 검색 API | Google Programmable Search / Bing | Google 1순위, Bing 백업 |
| 2 | 번역 관리 | Crowdin / Lokalise / 수동 | 초기 수동, 10개 언어 넘어가면 Crowdin 검토 |
| 3 | CS 플랫폼 | Zendesk / Intercom / 이메일 직접 | 초기 이메일 직접, 리뷰량 증가 시 전환 |
| 4 | Sentry 도입 | Firebase Crashlytics만 / Sentry 병행 | 초기 Crashlytics만 |
| 5 | MacinCloud 플랜 | Dedicated / Managed Server / Pay-as-you-go | 미확정 — 첫 부팅 시점에 결정 |
| 6 | Google Programmable Search API Key 노출 방지 | 온디바이스 내장 / 프록시 | 헌법상 우리 서버 금지 → 온디바이스 + 쿼터 제한 + 키 제한(referrer/앱 서명) |

### 확정된 항목 (대표님 결정 완료)

| 항목 | 확정값 | 확정일 |
|---|---|---|
| Mac 환경 | **MacinCloud** | 2026-04-24 |
| 구독 관리 | **스토어 빌링만 (Google Play Billing + StoreKit 2)**. 우리 서버 없음. 온디바이스 검증 | 2026-04-24 |
| RevenueCat | **미채택** | 2026-04-24 |
| AWS (App Factory) | **미사용** | 2026-04-23 |
| AWS (웹 도메인) | **정적 콘텐츠만 허용** (개인정보처리방침 호스팅 등) | 2026-04-23 |

---

## 7. 실행 순서 (STEP 0~5)

> **이 순서는 고정이다. 바꾸면 안 된다.**

### STEP 0: 대표님 결정 ✅ **완료**

- ✅ Mac 환경 = MacinCloud
- ✅ 구독 관리 = 스토어 빌링만, 우리 서버 없음

### STEP 1: 로컬 총사령관 아키텍처 v1 세부 설계서

- 본 문서의 3장·4장을 실행 레벨로 구체화
- PowerShell 함수 인터페이스 (`Invoke-MPCAndroidBuild`, `Invoke-MPCMacSSH` 등)
- 상태 JSON 스키마 (`local-state/machine-status.json`)
- Windows ↔ MacinCloud SSH 명령 템플릿
- 로그·재시도·타임아웃 규격
- 비밀값 11종 배치 경로
- 산출물: `LOCAL_COMMANDER_ARCH_v1.md`

### STEP 2: Claude Code 부팅 워크오더

- `WO-BOOT-LOCAL-COMMANDER-001.md` (Windows)
- `WO-BOOT-MAC-COMMANDER-001.md` (MacinCloud)
- 각 환경에서 git·gh·JDK·Android Studio·SDK·Xcode·Fastlane·Python·Node 설치·구성·검증
- 복붙 블록 형식, Auto Mode 전제 명시 (대표님 메모리 철칙)
- 산출물: SSH 연결 실증 + 양쪽 `state.json` 초기화

### STEP 3: Fastlane 양쪽 셋업 워크오더

- `WO-FASTLANE-ANDROID-001.md` — supply + Service Account JSON
- `WO-FASTLANE-IOS-001.md` — deliver + pilot + match + gym + App Store Connect API Key
- 산출물: **"코드만 있으면 한 명령으로 스토어에 올라가는 상태"**

### STEP 4: 스토어 최초 등록 (대표님 수동 1회)

- Apple Developer Program 가입 + D-U-N-S 번호
- Google Play Developer 가입
- 최초 앱 레코드 생성 (양쪽)
- API Key·Service Account JSON 발급 → 비밀값 SOP에 따라 배치

### STEP 5: MyPhoneCheck 기능별 워크오더

- `WO-FEAT-SEARCH-3AXIS-001.md` (3축 검색)
- `WO-FEAT-PUSH-TRASH-001.md` (푸시 휴지통)
- `WO-FEAT-MIC-CAMERA-EVENT-001.md` (실시간 이벤트)
- `WO-FEAT-BILLING-001.md` ($2.49/월 구독, 온디바이스 검증)
- `WO-FEAT-I18N-001.md` (다국어)

---

## 8. 탈락 기준·성공 판정

### 8.1 로컬 총사령관 탈락 기준

아래 중 하나라도 해당하면 **탈락**:

1. 답변만 하고 실행 못 함
2. PowerShell 직접 못 돌림
3. 에뮬레이터·실기기·시뮬레이터 못 다룸
4. 스크린샷·화면 상태 못 읽음
5. 매번 대표님에게 복붙·수동 클릭 요구
6. 실패 후 다음 행동을 스스로 못 정함
7. 파일·로그·상태를 연결해서 보지 못함

### 8.2 로컬 총사령관 성공 판정

아래 모두 만족해야 **합격**:

1. 대표님 한 줄 지시를 이해한다
2. 로컬 파일을 직접 만든다
3. 필요한 프로그램·명령을 직접 실행한다
4. 화면·로그·결과를 직접 읽는다
5. 다음 액션을 스스로 정한다
6. 대표님에게는 결과와 핵심 판단만 보고한다

### 8.3 MyPhoneCheck 론칭 성공 판정

1. Android AAB + iOS IPA 양쪽 스토어 등록 완료
2. $2.49/월 구독 결제 양쪽 작동 (온디바이스 검증)
3. 3축 검색 결정 엔진 작동
4. 푸시 휴지통 격리·복원 작동
5. Mic/Camera 실시간 이벤트 감지 작동
6. 개인정보처리방침·이용약관·App Privacy·Data Safety 전부 등록
7. Crashlytics·Play Vitals·App Store Analytics 수신
8. 최초 구독자 1명 발생 및 온디바이스 영수증 서명 검증 통과

---

## 부록 A: 변경 이력

| 버전 | 일자 | 변경자 | 내용 |
|---|---|---|---|
| v0.9 | 2026-04-24 | 비전 | docx 초안 작성 (`MyPhoneCheck_통합운영설계안_v1.docx`) |
| v1.0 | 2026-04-24 | 비전 | **헌법 위반 시정**: AWS Lambda·API Gateway·DynamoDB 제거. 구독은 스토어 빌링 + 온디바이스 서명 검증만. 마크다운 정식본 발행 |

---

## 한 줄 결론

> **대표님 1채널 → Claude Code 총사령관 → Windows + MacinCloud 통합 지휘 → Fastlane 양쪽 자동 배포 → MyPhoneCheck 4대 핵심 기능 + $2.49 온디바이스 구독으로 노중앙·노저장 헌법 정공법 론칭.**

*빅테크 정공법. 반쪽 기능 없음. 우회 없음. 우리 서버 없음.*

---

**END OF DOCUMENT**
