# 27-3. Permissions Declaration — 권한별 정당화 (Patch 33 신설)

**원본 출처**: v1.7.1 §27 H1 + §27-3
**v1.8.0 Layer**: Policy
**의존**: `10_policy/04_data_safety.md` + `10_policy/01_permissions.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/10_policy/05_permissions_declaration.md`

---

# 27. Google Play Data Safety + Permissions Declaration (Patch 32·33·34)

**본 장은 Patch 32·33·34로 전면 재작성되었다**. 사유:
- **Patch 32**: v1.6.1-patch 시점의 "데이터 수집 없음" 선언이 실제 SMS·통화·앱 읽기 동작과 모순됨을 자비스 Lane 4가 지적 (2026-04-24). Play Console Data Safety 기준에서 "디바이스 내부 접근(on-device access)"도 넓은 의미의 "collection"에 포함될 수 있으므로, **정직한 분류로 전면 재선언**한다.
- **Patch 33**: Permissions Declaration 본문을 신설. 각 민감 권한별로 `core user benefit` + `less-invasive alternative 검토 결과` + `사용자 고지 방식` 3요소를 명시하여 Play 심사 통과 자격을 갖춘다.
- **Patch 34**: NKB at-rest 암호화(§27-5) 신설. Lane 1 D06이 지적한 "v1.0에서 DONE이었던 SQLCipher/AES-256-GCM이 v1.6.1에서 실종"을 복원 (코웍 87a9a3 §8-0 흡수).

헌법 제1·2·5조(정직성) 정면 구현.
## 27-3. Permissions Declaration — 권한별 정당화 (Patch 33 신설)

Play Console **Permissions Declaration Form** 제출 시 각 민감 권한에 대해 다음 3요소를 **작성된 그대로 제출**한다. Truecaller·Hiya·Whoscall 등 스팸 필터 카테고리 앱의 통과 전례에 기반.

### 27-3-1. READ_CALL_LOG

- **Core user benefit**: CallCheck의 Cold Start 단계에서 기존 통화 이력으로 스팸 필터 초기화. 사용자는 앱 설치 직후부터 알려진 스팸 번호에 대한 경고를 받을 수 있다.
- **Less-invasive alternative 검토**: 실시간 `CALL_STATE_RINGING` 이벤트만 사용하는 방식 검토. 그러나 Cold Start 정보 없이는 첫 수신 시 SAFE/NONE 초기값으로만 표시되어, 사용자가 앱 설치 후 수십 통의 통화를 받아야 학습이 시작됨 → **초기 가치 전달 실패**.
- **사용자 고지**: 온보딩 3번째 슬라이드에서 "통화 이력 읽기 권한 — 스팸 필터 초기화에 사용, 외부 전송 없음" 명시. 권한 거부 가능, 거부 시 Cold Start 없이 실시간 학습만 작동.
- **Play Category**: `COMMUNICATION` (Default Dialer 후보로 신고, 단 본 앱은 Dialer가 아닌 Caller ID 보조 앱 카테고리).

### 27-3-2. READ_PHONE_STATE

- **Core user benefit**: 통화 수신(`CALL_STATE_RINGING`) 감지 → 착신 오버레이 표시. CallCheck의 핵심 기능.
- **Less-invasive alternative**: Telecom Framework의 `CallScreeningService`가 있으나 **사용자가 Default Dialer 역할을 부여해야** 작동. 본 앱은 Default Dialer를 요구하지 않는 전략이므로 `READ_PHONE_STATE` 경로 사용.
- **사용자 고지**: 온보딩 2번째 슬라이드 + 설정 화면 "작동 원리".

### 27-3-3. READ_SMS / RECEIVE_SMS / SEND_SMS / WRITE_SMS (Mode A 전용)

- **Core user benefit**: MessageCheck Mode A에서 사용자가 본 앱을 **기본 SMS 앱으로 명시 지정**한 경우에만 활성화. SMS 자동 수신 감지로 유해 문자 실시간 차단.
- **Less-invasive alternative (이미 채택)**: **Mode B (Share Intent, 권한 0)** 이 기본값. 사용자가 Mode A의 편의성을 선택할 때만 권한 부여. Mode B만으로도 핵심 기능 작동.
- **Play 정책 준수**: Mode A 활성화 시 완전한 SMS 앱 기능(송수신·대화 목록·MMS·검색) 제공. `RoleManager.ROLE_SMS` 승인 플로우 준수.
- **사용자 고지**: 설정 → "Default SMS로 지정하기" 토글. 토글 탭 시 시스템 다이얼로그로 승인. 해제도 토글.

### 27-3-4. READ_CONTACTS

- **Core user benefit**: Cold Start 단계에서 저장된 연락처의 번호를 `SAFE` 초기값으로 NKB에 등록. 지인 전화를 스팸으로 오탐하지 않도록.
- **Less-invasive alternative**: 연락처 없이 통화 이력만으로 학습. 그러나 "전화를 자주 받지 않은 지인" 번호가 `UNKNOWN`으로 분류되어 오탐 경고 발생 가능.
- **사용자 고지**: 온보딩 4번째 슬라이드. 거부 가능.

### 27-3-5. PACKAGE_USAGE_STATS (Special App Access)

- **Core user benefit**: MicCheck·CameraCheck에서 "마지막 사용 시각"을 표시하여 사용자가 **오랫동안 사용하지 않은 앱의 권한을 회수**할 수 있도록 돕는다.
- **Less-invasive alternative**: 사용 통계 없이 단순 권한 보유 앱 목록만 표시. 이 경우 "쓰지도 않는 앱의 권한"을 식별할 수 없어, 사용자 결정 품질 저하.
- **사용자 고지**: MicCheck·CameraCheck 첫 진입 시 "앱 사용 통계 접근 허용하기" 안내 → 시스템 설정으로 이동 → 사용자 수동 활성화. **자동 부여되지 않는 Special App Access**이므로 사용자 의도 확실.
- **거부 시 동작**: 앱 리스트는 표시하되 "최근 사용: 정보 없음"으로 표시. 회수 버튼은 정상 작동.

### 27-3-6. SYSTEM_ALERT_WINDOW

- **Core user benefit**: 통화 수신 시 오버레이로 위험도·4속성을 즉시 표시. 전체 화면 탈취 없이 비침습적.
- **Less-invasive alternative**: 일반 알림만 사용. 그러나 통화 화면 위에 즉각 표시 불가, 사용자가 알림 서랍을 내려야 함 → 착신 결정 시점 놓침.
- **사용자 고지**: 시스템 다이얼로그. 거부 시 일반 알림으로 폴백.

### 27-3-7. POST_NOTIFICATIONS

- **Core user benefit**: 새 통화·문자·고위험 발견 시 사용자에게 알림. Android 13+ 런타임 권한.
- **사용자 고지**: 첫 실행 시 시스템 다이얼로그. 거부 가능.

### 27-3-8. Play Integrity API (Patch 38, 런타임 권한 아님)

- **Core user benefit**: 구독 결제 활성화 시점에 기기 환경 무결성(루팅·에뮬레이터·Frida 탐지)을 확인하여 결제 우회·크랙을 방지한다. 정품 사용자 보호.
- **기술 스코프**: Google Play Services `com.google.android.gms:play-services-integrity` Gradle 의존성. **런타임 권한 요청 없음**, Manifest 변경 없음, 사용자 상호작용 없음.
- **데이터 처리**: Play Integrity 토큰은 Google Play Services에서 직접 반환받아 **로컬에서만 파싱**. 자체 서버로 전송하지 않음 (`classicRequest` 모드). 헌법 1조 "스토어 공식 API 허용" 범위 내.
- **사용자 고지**: 온보딩 결제 화면에 1줄 고지: "결제 활성화 시 Google Play가 기기 무결성을 확인합니다. 이 정보는 Google Play와 본 앱 사이에서만 사용됩니다."
- **거부 가능성**: 사용자 거부 메커니즘 불가 (Google Play Services 내장 기능). 단, 네트워크 단절·Google Play 미지원 디바이스에서는 자동 스킵되며 1계층 검증만으로 활성화 허용 (헌법 4조 fail-open).
