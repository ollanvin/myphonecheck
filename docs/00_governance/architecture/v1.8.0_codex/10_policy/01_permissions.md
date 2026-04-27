### 24-6-1. Manifest 권한 정합 (Patch 36 재작성 — QUERY_ALL_PACKAGES 제거 + `<queries>` 블록)

**원본 출처**: v1.7.1 §24-6-1 (72줄)
**v1.8.0 Layer**: Policy
**의존**: `00_core/01_primary.md` + `50_test_infra/03_permission_matrix.md`
**변경 이력**: 본 파일은 v1.7.1 §24-6-1 (72줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/10_policy/01_permissions.md`

---


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

