# 24. Day-by-Day 구현 가이드

**원본 출처**: v1.7.1 §24 (221줄)
**v1.8.0 Layer**: Implementation
**의존**: `60_implementation/05_repo_layout.md` + `60_implementation/06_ci_cd.md`
**변경 이력**: 본 파일은 v1.7.1 §24 (221줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/60_implementation/01_day_by_day.md`

---


Phase 1~4 구현을 날짜 단위로 분해. **각 Day 완료 기준**과 **검증 방법**을 명시.

## 24-1. Day 0 — 프로젝트 스캐폴드

- [ ] Android Studio Flamingo 이상 설치
- [ ] JDK 17 설치 (Temurin 또는 Zulu), `jvmToolchain(17)` 선언
- [ ] Android SDK 34 설치
- [ ] 프로젝트 생성 + §23 구조 반영
- [ ] Git init + GitHub 원격 연결
- [ ] 최초 커밋 "chore: initial scaffold"

**완료 기준**: `./gradlew assembleDebug` PASS

## 24-2. Day 1~2 — core/common Stage 0 계약 (이미 완료)

- [x] IdentifierType sealed class
- [x] RiskKnowledge interface
- [x] Checker<IN, OUT> interface
- [x] DecisionEngineContract interface
- [x] FreezeMarker 어노테이션
- [x] FREEZE.md 작성
- [x] 22개 테스트 PASS (Patch 37 통일)
- [x] CI 강제 설정

Stage 0 Contracts 워크오더(f1a85c)로 완료됨. 시그니처 변경 금지.

## 24-3. Day 3 — NKB Room 구조

- [ ] `engine/nkb` 모듈 생성
- [ ] Room Database 정의
- [ ] NumberKnowledge·UserAction·ClusterProfile Entity
- [ ] DAO 작성
- [ ] Migration 1→2 (v1.5.1 → v1.5.2 Patch 08 대응)

**완료 기준**: `MigrationCompatTest` PASS + DB 생성 후 dump 확인

## 24-4. Day 4 — SearchResultAnalyzer

- [ ] `engine/analyzer` 모듈
- [ ] KeywordLoader (strings.xml 로드)
- [ ] FeatureExtractor
- [ ] TierClassifier
- [ ] SearchResultAnalyzer 구현
- [ ] 단위 테스트

**완료 기준**: "쿠팡 배송 사칭 SMS" 등 테스트 케이스 20건 PASS

## 24-5. Day 5 — Decision Engine 구현

- [ ] `engine/decision` 모듈
- [ ] 통합 8단계 알고리즘 구현
- [ ] Softmax 정규화
- [ ] ConflictResolver
- [ ] 백그라운드 재검증 큐
- [ ] 단위 테스트

**완료 기준**: DecisionContractTest PASS + Softmax 분포 검증

## 24-6. Day 6 — Self-Discovery + Cold Start

- [ ] `engine/discovery` 모듈
- [ ] Search Engine probe (§7-1)
- [ ] Official Domain probe (§7-2)
- [ ] ClusterProfile 생성·저장
- [ ] Cold Start 6단계 (§11)
- [ ] WorkManager 스케줄링 (PeriodicMaintenance)

**완료 기준**: SmokeRun01 PASS

### 24-6-1. Manifest 권한 정합 (Patch 36 재작성 — QUERY_ALL_PACKAGES 제거 + `<queries>` 블록)

**변경 사유**: Patch 26(v1.6.1-patch)에서 `QUERY_ALL_PACKAGES`를 `tools:ignore`로 선언했으나, 자비스 Lane 4(2026-04-24) 검증 결과 **Play 심사 거의 100% 리젝 대상**. 자비스 대안 2 "Package Visibility 최소화"를 수용, `QUERY_ALL_PACKAGES` 제거하고 `<queries>` 블록으로 대체한다 (Patch 36).

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools">

    <!-- CallCheck 필수 권한 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <!-- MessageCheck Mode A 전용 (사용자가 Default SMS 지정 시에만 활성) -->
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.RECEIVE_SMS" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.WRITE_SMS"
        tools:ignore="ProtectedPermissions" />

    <!-- 네트워크 · SLA · 알림 · 오버레이 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

    <!-- MicCheck/CameraCheck용 사용 통계 (Special App Access, 사용자 수동 승인) -->
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
        tools:ignore="ProtectedPermissions" />

    <!-- Patch 36: QUERY_ALL_PACKAGES 제거 → <queries> 블록으로 대체 -->
    <queries>
        <!-- MicCheck: RECORD_AUDIO를 선언한 앱만 필터링 -->
        <intent>
            <action android:name="android.intent.action.MAIN" />
        </intent>

        <!-- Intent 기반 방식: 특정 action에 응답하는 앱만 조회 -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="http" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="https" />
        </intent>

        <!-- MessageCheck Share Intent (Mode B) 대상 식별 -->
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>

    <!-- 금지 권한 (정책 위반 또는 Patch로 제거됨) -->
    <!-- ❌ BROADCAST_SMS — Play 정책 위반 (Patch 17) -->
    <!-- ❌ RECORD_AUDIO — MicCheck는 스캔만, 녹음 안 함 (Patch 23) -->
    <!-- ❌ CAMERA — CameraCheck는 스캔만, 촬영 안 함 (Patch 23) -->
    <!-- ❌ QUERY_ALL_PACKAGES — Play 심사 리스크, <queries>로 대체 (Patch 36) -->
</manifest>
```

**핵심 변경점 (Patch 36)**:

1. **`QUERY_ALL_PACKAGES` 완전 제거** — Android 11+ Package Visibility 정책 정면 준수.
2. **`<queries>` 블록 신설** — MicCheck/CameraCheck는 "RECORD_AUDIO / CAMERA 선언 앱"만 필터링 (PackageManager.getPackagesHoldingPermissions 사용 시 자동 처리).
3. **SMS 권한 4종 추가** — Mode A(Default SMS) 활성화 시에만 사용자 동의로 부여.
4. **PACKAGE_USAGE_STATS 유지** — Special App Access(사용자 수동 설정) 경로로 부여.
5. **네트워크 · 알림 · 오버레이** — 기존 유지.

**Permissions Declaration 연결**: 위 권한 목록 각각의 core user benefit 선언은 §27-3 참조.

## 24-7. Day 7~8 — CallCheck Surface (Phase 1)

- [ ] `feature/call` 모듈
- [ ] IncomingCallScreen 오버레이
- [ ] InCallScreen (하단 띠)
- [ ] PostCallScreen 전체 카드
- [ ] CallLogScreen
- [ ] FourAttributeCard 공통 UI (`core/ui`)
- [ ] 온보딩 4개 슬라이드 (`feature/onboarding`)
- [ ] 권한 요청 UX

**완료 기준**: SmokeRun02~05 PASS + 실기기 통화 수신 테스트

## 24-8. Day 9 — MessageCheck Surface (Phase 2)

- [ ] `feature/message` 모듈
- [ ] MessageCheckEngine (3중 평가)
- [ ] UrlExtractor
- [ ] ImpersonationDetector
- [ ] MessageDetailScreen
- [ ] Default SMS Handler 또는 NotificationListenerService 경로 선택

**완료 기준**: SmokeRun06~07 PASS

## 24-9. Day 10 — MicCheck Surface (Phase 3)

- [ ] `feature/mic` 모듈
- [ ] MicCheckEngine (PackageManager 스캔)
- [ ] JustificationClassifier
- [ ] MicCheckScreen + MicPermissionDetailScreen
- [ ] AppInstallMonitor (외부 이벤트 1)
- [ ] CveWatchWorker (외부 이벤트 2)

**완료 기준**: SmokeRun08 PASS + 실기기 앱 30개 이상 스캔 확인

## 24-10. Day 11 — CameraCheck Surface (Phase 3)

- [ ] `feature/camera` 모듈 (MicCheck와 병렬 구조)
- [ ] CameraCheckEngine
- [ ] CameraCheckScreen + CameraPermissionDetailScreen
- [ ] 외부 이벤트 공유 (MicCheck와 같은 BroadcastReceiver)

**완료 기준**: SmokeRun09 PASS

## 24-11. Day 12 — Billing 통합 (Phase 4)

- [ ] `feature/subscription` 모듈
- [ ] BillingClient v7 연결
- [ ] SubscriptionScreen ($2.49/월 단일 상품)
- [ ] Purchase Token 로컬 저장 (Room Entity)
- [ ] 구독 상태 복원
- [ ] 만료 감지

**완료 기준**: SmokeRun10 PASS

## 24-12. Day 13 — 다국어 + 접근성 (Phase 5)

- [ ] strings.xml ko / en / ja 최소 3개 locale
- [ ] 동적 언어 설정 (LocaleContextWrapper)
- [ ] TalkBack 대응
- [ ] RTL 대응 (아랍어 후행)
- [ ] 다크/라이트 테마 토글

**완료 기준**: Accessibility Scanner PASS + 3개 locale 모든 화면 렌더 확인

## 24-13. Day 14 — CI/CD + 스토어 준비 (Phase 6)

- [ ] `.github/workflows/android-ci.yml` 작성
- [ ] Detekt + Kover(coverage) 설정
- [ ] 본사 매핑 0건 검증 스크립트 전부 통합
- [ ] Play App Signing 등록
- [ ] Play Console Data Safety 신고
- [ ] 개인정보처리방침 URL 등록

**완료 기준**: CI PASS + Play Console 내부 테스트 트랙 업로드 성공

---

