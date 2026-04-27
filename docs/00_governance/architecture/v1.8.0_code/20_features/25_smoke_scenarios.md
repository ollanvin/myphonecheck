# 18. 스모크런 시나리오 + Four Surfaces 본문

**원본 출처**: v1.7.1 §18 전문 (617줄)
**v1.8.0 Layer**: Feature
**의존**: `06_product_design/04_system_arch.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/20_features/25_smoke_scenarios.md`

---

# 18. 스모크런 시나리오 + Four Surfaces 본문

스모크런(Smoke Run)은 **구현 완료 즉시 수행**하는 제품 작동 검증. v1.5.2에서 11개 시나리오 정의, v1.6.0에서 Four Surfaces 관련 시나리오 확장, v1.6.1에서 Surface 본문 완성(Patch 25).

## 18-1. 스모크런 11개 시나리오

| # | 이름 | 목적 | Surface | SLA 레벨 |
|---|---|---|---|---|
| SmokeRun01 | 기본 Cold Start | 설치 직후 Day 0 부트 | 공통 | L1 |
| SmokeRun02 | 착신 오버레이 | 전화 수신 시 4속성 표시 | CallCheck | L1 |
| SmokeRun03 | Softmax 분포 | 신호 100개에서 topConfidence 산출 | 엔진 | L1 |
| SmokeRun04 | 사용자 Override | "안심 표시" 후 NKB 재계산 | 엔진 | L1 |
| SmokeRun05 | 연락처 상호작용 | 연락처 등록 번호는 SAFE 초기값 | Cold Start | L1 |
| SmokeRun06 | MessageCheck 3중 평가 | 발신번호 + URL + 기관명 평가 결합 | MessageCheck | L1 |
| SmokeRun07 | MessageCheck 시나리오 | "쿠팡 배송 알림" 사칭 SMS 검출 | MessageCheck | L1 |
| SmokeRun08 | MicCheck 기본 | RECORD_AUDIO 보유 앱 스캔 + 평가 | MicCheck | L1 |
| SmokeRun09 | CameraCheck 기본 | CAMERA 보유 앱 스캔 + 평가 | CameraCheck | L1 |
| SmokeRun10 | Billing 주기 | 구독 구매 → 상태 업데이트 → 만료 감지 | Billing | L1 |
| SmokeRun11 | L3 Offline 기준선 | 비행기 모드 + NKB 200건에서 p95 ≤ 5ms | 공통 | **L3** |

## 18-2. SmokeRun01: 기본 Cold Start

**조건**: 신규 설치, 모든 권한 허용, 네트워크 연결
**절차**:
1. 앱 최초 실행
2. 온보딩 슬라이드 4개 → "시작하기"
3. 권한 순차 요청 (READ_PHONE_STATE·READ_CALL_LOG·READ_SMS·READ_CONTACTS)
4. Cold Start 6단계 실행 (§11)
5. Self-Discovery 실행 → ClusterProfile 생성 확인
6. 메인 화면 진입

**검증 포인트**:
- NKB에 초기 엔트리 500~1000건 생성 (통화·문자·연락처 기반)
- ClusterProfile 1건 저장, discoveredEngines 비어있지 않음
- 전체 Cold Start 소요 시간 ≤ 30초

## 18-3. SmokeRun02: 착신 오버레이

**조건**: L1, NKB 캐시 있는 번호 "+821012345678" (MEDIUM Risk)
**절차**:
1. 에뮬레이터 또는 실기기에서 해당 번호로 통화 수신
2. 오버레이 렌더 시점 측정
3. FourAttributeCard 내용 확인
4. "차단 / 신고 / 안심" 3버튼 탭 → UserAction 기록 확인

**검증 포인트**:
- 수신 감지 → 오버레이 렌더 ≤ 500ms (p95)
- 4속성 모두 표시 (null·plaintext 없음)
- 버튼 탭 시 NKB 재계산 트리거

## 18-4. MessageCheck 본문 (Patch 29 — Mode A/B 2-모드 아키텍처)

v1.6.1-patch 시점 `§18-4-4`는 "Default SMS Handler 재설계 또는 NotificationListenerService"라는 **대안 경로만 제시**하고 어느 경로를 채택할지 미확정 상태였다. 자비스 Lane 4 검증(2026-04-24)이 "Play 심사에서 이 모호성이 곧 리젝 요인"이라고 지적. 본 Patch 29는 **2-모드 명시적 분기**로 이를 해결한다.

### 18-4-0. 2-모드 개요

| 모드 | 권한 | 감지 방식 | 사용자 경험 | 기본값 |
|---|---|---|---|---|
| **Mode B — Share Intent (기본)** | 없음 | 사용자 수동 트리거 | 문자 앱에서 "공유" → MyPhoneCheck 선택 | ✅ 기본 |
| **Mode A — Default SMS Handler (선택)** | `READ_SMS` + Default SMS 지정 | 자동 수신 감지 | 기본 SMS 앱으로 MyPhoneCheck 지정 | 선택 |

**원칙**:
- Mode B는 **권한 0**으로 전세계 즉시 작동 (헌법 4조 자가 작동 강화)
- Mode A는 사용자가 명시적으로 선택한 경우에만 활성화
- 두 모드 모두 **동일한 `MessageCheckEngine` + 동일한 `MessageRisk` 출력**
- 전환 UI: 설정 화면에서 "Default SMS 앱으로 지정하기" 버튼 제공, 비활성화 기본

### 18-4-1. Mode B — Share Intent (권한 0)

#### 18-4-1-1. 작동 흐름

```
1. 사용자가 기본 SMS 앱에서 의심 문자 수신
2. 사용자가 해당 문자를 길게 누름 → "공유" 메뉴
3. 공유 대상 목록에 "MyPhoneCheck" 표시
4. 사용자가 MyPhoneCheck 선택
5. MessageCheck 화면이 열려 문자 본문 자동 분석
6. MessageRisk 4속성 결과 표시
```

#### 18-4-1-2. AndroidManifest 선언

```xml
<activity
    android:name=".feature.message.MessageCheckShareActivity"
    android:exported="true"
    android:label="@string/msg_check_share_label">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

#### 18-4-1-3. 수신 처리

```kotlin
class MessageCheckShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: run {
            finish(); return
        }
        val sender = intent.getStringExtra(Intent.EXTRA_EMAIL)?.firstOrNull()
            ?: extractSenderFromSubject(intent.getStringExtra(Intent.EXTRA_SUBJECT))
            ?: UNKNOWN_SENDER_MARKER

        lifecycleScope.launch {
            val risk = messageCheckEngine.evaluate(
                IncomingSms(
                    senderE164 = sender,
                    body = body,
                    receivedAt = System.currentTimeMillis()
                )
            )
            setContent { MessageRiskScreen(risk) }
        }
    }
}
```

발신번호를 공유 Intent에서 확보하지 못하는 케이스가 대부분이므로, **sender 결측 시 URL·사칭만으로 평가**하는 경로를 `MessageCheckEngine` 내부에서 지원 (다음 §18-4-3 참조).

#### 18-4-1-4. 사용자 UX 보조

설정 화면에 **"빠른 공유 설정" 가이드**:
- 안드로이드 기본 SMS 앱에서 "MyPhoneCheck"를 공유 메뉴에 고정하는 방법 (OS 버전별 스크린샷)
- "복사 → 붙여넣기" 대안 경로 (텍스트 필드에 직접 입력)

### 18-4-2. Mode A — Default SMS Handler (사용자 명시 선택)

#### 18-4-2-1. 활성화 조건

- 사용자가 설정 화면에서 "Default SMS 앱으로 지정" 버튼 탭
- 시스템 `RoleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)` 호출
- 사용자가 시스템 다이얼로그에서 승인
- 이 시점부터 `READ_SMS`·`SEND_SMS`·`RECEIVE_SMS` 자동 부여

#### 18-4-2-2. 필수 구현

Default SMS Handler가 되려면 Play 정책상 **완전한 SMS 앱 기능**을 제공해야 한다:
- SMS 송수신 UI
- 대화 목록·검색
- MMS 지원
- 알림

본 앱에서는 최소 SMS 앱 기능을 `feature/message/sms-handler` 서브 모듈로 구현. 원 SMS 앱과 경쟁하는 UX를 목표로 하지 않고, **"보안 특화 SMS 앱"** 포지셔닝.

#### 18-4-2-3. 수신 감지

```kotlin
class SmsReceiverService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIncoming(it) }
        return START_NOT_STICKY
    }

    private fun handleIncoming(intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages.forEach { sms ->
            scope.launch {
                val risk = messageCheckEngine.evaluate(
                    IncomingSms(
                        senderE164 = normalizeE164(sms.originatingAddress ?: ""),
                        body = sms.messageBody,
                        receivedAt = sms.timestampMillis
                    )
                )
                if (risk.riskLevel >= RiskLevel.HIGH) {
                    notificationBuilder.showMessageRiskAlert(risk)
                }
                // 저장은 sms-handler 모듈에 위임 (정상 SMS 앱 기능)
            }
        }
    }
}
```

#### 18-4-2-4. 사용자 복귀 경로

사용자가 "다시 원래 SMS 앱으로 돌아가기"를 원하면 설정에서 **"Default SMS 해제"** 버튼 → 시스템 설정 → 원 앱 선택.

### 18-4-3. 공통 엔진 — MessageCheckEngine (Mode 무관)

```kotlin
data class IncomingSms(
    val senderE164: String,          // 결측 허용 (UNKNOWN_SENDER_MARKER)
    val body: String,
    val receivedAt: Long
)

class MessageCheckEngine(
    private val decisionEngine: DecisionEngineContract,
    private val urlExtractor: UrlExtractor,
    private val impersonationDetector: ImpersonationDetector
) {
    suspend fun evaluate(sms: IncomingSms): MessageRisk {
        // 1. 발신번호 평가 (결측 허용)
        val senderRisk: RiskKnowledge? = if (sms.senderE164 != UNKNOWN_SENDER_MARKER) {
            decisionEngine.evaluate(IdentifierType.PhoneNumber(sms.senderE164))
        } else null

        // 2. URL 추출 + 각 URL 평가
        val urls = urlExtractor.extract(sms.body)
        val urlRisks = urls.map { url ->
            val domainRisk = decisionEngine.evaluate(
                IdentifierType.UrlDomain(extractDomain(url))
            )
            UrlRiskEntry(
                url = url,
                domain = extractDomain(url),
                risk = domainRisk.riskLevel,
                reason = domainRisk.reasonSummary
            )
        }

        // 3. 기관 사칭 감지 (strings.xml 기반 키워드 + 공식 도메인 화이트리스트)
        val impersonationFlags = impersonationDetector.detect(sms.body)

        // 4. 결과 통합
        return combineResults(sms, senderRisk, urlRisks, impersonationFlags)
    }

    private fun combineResults(
        sms: IncomingSms,
        sender: RiskKnowledge?,
        urls: List<UrlRiskEntry>,
        impersonations: List<ImpersonationFlag>
    ): MessageRisk {
        val maxRisk = maxOf(
            sender?.riskLevel ?: RiskLevel.NONE,
            urls.maxOfOrNull { it.risk } ?: RiskLevel.NONE,
            if (impersonations.any { it.suspicionLevel >= 0.7f }) RiskLevel.HIGH else RiskLevel.NONE
        )

        return MessageRisk(
            identifier = MessageIdentifier(
                senderE164 = sms.senderE164,
                messageHash = sha256(sms.body),
                receivedAt = sms.receivedAt
            ),
            riskLevel = maxRisk,
            expectedDamage = deriveDamage(maxRisk, impersonations),
            damageTypes = deriveDamageTypes(impersonations, urls),
            reasonSummary = buildReason(sender, urls, impersonations),
            senderRisk = sender?.riskLevel ?: RiskLevel.NONE,
            urlRisks = urls,
            impersonationFlags = impersonations
        )
    }

    companion object {
        const val UNKNOWN_SENDER_MARKER = "__UNKNOWN__"
    }
}
```

### 18-4-4. MessageRisk 데이터 클래스

```kotlin
data class MessageRisk(
    override val identifier: IdentifierType,   // MessageIdentifier로 래핑
    override val riskLevel: RiskLevel,
    override val expectedDamage: DamageEstimate,
    override val damageTypes: List<DamageType>,
    override val reasonSummary: String,
    override val computedAt: Long = System.currentTimeMillis(),
    override val stalenessFlag: StalenessFlag = StalenessFlag.FRESH,

    // MessageCheck 고유 필드
    val senderRisk: RiskLevel,
    val urlRisks: List<UrlRiskEntry>,
    val impersonationFlags: List<ImpersonationFlag>
) : RiskKnowledge

data class MessageIdentifier(
    val senderE164: String,
    val messageHash: String,
    val receivedAt: Long
)

data class UrlRiskEntry(
    val url: String,
    val domain: String,
    val risk: RiskLevel,
    val reason: String
)

data class ImpersonationFlag(
    val suspectedOrganization: String,
    val suspicionLevel: Float,
    val reason: String
)
```

### 18-4-5. SmokeRun07 시나리오 — "쿠팡 배송 알림" 사칭 SMS

**입력 SMS**:
```
[쿠팡] 고객님 상품이 배송 중입니다.
배송조회: http://coupang-delivery.xyz/track?id=KR12345
```

**Mode B 검증 흐름**:
1. 사용자가 SMS 앱에서 문자 길게 누름 → 공유 → MyPhoneCheck
2. `MessageCheckShareActivity`가 body 수신, sender는 `UNKNOWN_SENDER_MARKER`
3. `MessageCheckEngine.evaluate()` 호출
4. URL `coupang-delivery.xyz` → 공식 쿠팡 도메인(`coupang.com`) 아님 → Risk HIGH
5. ImpersonationDetector: "쿠팡" 키워드 + 공식 도메인 불일치 → suspicionLevel 0.9
6. 결과: `MessageRisk(riskLevel=HIGH, reasonSummary="쿠팡 사칭 의심: 공식 도메인 아님")`

**Mode A 검증 흐름**:
- 동일한 결과. 추가로 `sms.senderE164` 확보 → senderRisk 평가도 병행
- HIGH 판정 시 자동 알림 전송

### 18-4-6. Mode 선택 UX

설정 화면 `MessageCheckSettingsScreen`:

```
┌────────────────────────────────────────┐
│ 📨 MessageCheck 작동 방식              │
├────────────────────────────────────────┤
│ ⦿ 공유 메뉴 (권장, 권한 없음)         │
│   문자 앱에서 "공유" → MyPhoneCheck    │
│                                        │
│ ○ 기본 SMS 앱으로 지정 (자동 감지)    │
│   문자 수신 시 자동으로 분석           │
│   → SMS 앱 기능 전환 필요             │
│                                        │
│ [도움말] [Default SMS 해제 방법]      │
└────────────────────────────────────────┘
```

### 18-4-7. Play 정책 정합

| 정책 | 준수 방식 |
|---|---|
| `BROADCAST_SMS` 금지 (Patch 17) | 사용 안 함, AndroidManifest 선언 없음 |
| Default SMS 정책 (Play Console) | Mode A 선택 시 사용자 명시 동의 + 완전 SMS 앱 기능 제공 |
| `READ_SMS` 정당화 (Permissions Declaration) | Mode A 경로에서만, core user benefit: "사용자가 기본 SMS 앱으로 지정한 경우 유해 문자 자동 감지" |
| `SEND_SMS` 사용 | Mode A에서 SMS 앱 기능 제공 시 한정 |
| Data Safety | Mode B: "데이터 수집 없음" / Mode A: "디바이스 내부 처리만, 외부 전송 없음" |

### 18-4-8. PrivacyCheck 폐기 기록 (Patch 21)

(이하 §18-5로 이동되지 않고 현 위치 유지)

## 18-5. PrivacyCheck 폐기 기록 (Patch 21)

v1.5.x에서 언급되었던 `PrivacyCheck` Surface는 **v1.6.0 Patch 21로 폐기**되었다. 폐기 사유:
- 추상적 범주 ("개인정보 위험")
- 구체적 Surface 원칙(제7조 구현) 위반
- MicCheck(§18-6) + CameraCheck(§18-7)로 **세분화·구체화**

이후 버전에서 PrivacyCheck 부활 금지. 구체적 기능은 새로운 Surface(§17-3 후행)로 신설.

## 18-6. MicCheck 본문 (Patch 30 — 단순 관리자 축소)

### 18-6-0. 재정의 배경 (Patch 30)

v1.6.0 ~ v1.6.1-patch 시점에는 `AppPermissionRisk`·`JustificationStatus`·`CveHistory`·`BreachHistory`·Decision Engine 평판 평가까지 포함된 **감시 엔진** 구조였다. 2026-04-24 대표님 지시로 **세 가지 단순 기능**으로 축소:

1. **권한 있는 앱 리스트 정리** — PackageManager 스캔, 정렬만
2. **최근 사용한 기록 정리** — UsageStatsManager 조회, 표시만
3. **사용자가 언제든 권한 회수할 수 있는 버튼** — 시스템 설정으로 원터치

축소 사유:
- 대표님 정의 기능 범위를 넘어선 "평판 감시 엔진" 설계는 설계자 과잉
- CVE·침해 이력 감시는 별도 Surface `AppSecurityWatch`(§17-3 후행)로 이관 (Patch 31)
- Play 심사 정합성 유지 (QUERY_ALL_PACKAGES 대안 §24-6과 결합)

### 18-6-1. 범위 및 헌법 정합

**MicCheck는 RECORD_AUDIO 권한을 요청하지 않는다.** PackageManager의 `getPackagesHoldingPermissions` 또는 `queryIntentActivities` 기반으로 "누가 권한을 갖고 있는가"만 조회한다.

**R5 네트워크 경계 (헐크 Lane 3 Top 1 반영)**: MicCheck Surface Layer는 **직접 네트워크 호출을 하지 않는다**. 본 Surface 단순 관리자 축소판은 Decision Engine·외부 API 조회도 없고, PackageManager + UsageStatsManager + Intent 호출만 수행한다 (§6-2 R5 준수).

권한 해제 UX는 `ACTION_APPLICATION_DETAILS_SETTINGS` 인텐트 원터치로 시스템 설정 화면 진입.

### 18-6-2. 데이터 모델 (단순화)

```kotlin
/**
 * MicCheck의 단일 항목.
 * Decision Engine · Risk · CVE · Breach · Justification 필드 모두 삭제됨 (Patch 30).
 * 이 구조체는 RiskKnowledge를 구현하지 않는다 (평판 판정 대상이 아님).
 */
data class MicPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,       // 로컬 PackageManager.getApplicationIcon 결과 캐시
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,          // UsageStatsManager.queryUsageStats 최근 값 (null 가능)
    val isCurrentlyForeground: Boolean
)
```

삭제된 것:
- ~~`AppPermissionRisk`~~ (Patch 30 삭제)
- ~~`JustificationStatus`~~ (Patch 30 삭제, 분류 판정 자체 제거)
- ~~`CveEntry` / `BreachEntry`~~ (AppSecurityWatch Surface로 이관)
- ~~`AppReputation`~~ (Decision Engine 호출 없음)

### 18-6-3. 구현

```kotlin
class MicCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<MicPermissionEntry>> {

    override suspend fun check(input: Unit): List<MicPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.RECORD_AUDIO
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                MicPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,  // 아이콘은 UI에서 직접 로드
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-6-4. Cold Start 및 스케줄 (코웍 87a9a3 §17-5-3a 흡수)

MicCheck는 CallCheck/MessageCheck와 달리 실시간 이벤트(수신 전화/문자)가 없으므로 별도 트리거 정책이 필요하다.

**트리거 조건 (OR)**:
- **앱 최초 실행**: Surface 등록 직후 1회 전체 스캔
- **주기적 스캔**: WorkManager `PeriodicWorkRequest`, 최소 간격 15분 (Android 제약), 권장 간격 6시간
- **앱 설치·업데이트 감지**: `ACTION_PACKAGE_ADDED` / `ACTION_PACKAGE_CHANGED` BroadcastReceiver
- **사용자 수동 새로고침**: UI 풀-투-리프레시 제스처

**Cold Start 흐름**:
```
앱 시작 → WorkManager 등록 → 즉시 1회 check(Unit) 실행
       → RECORD_AUDIO 보유 앱 목록 수집 (로컬, 네트워크 없음)
       → UsageStatsManager 최근 사용 조회 (로컬)
       → MicPermissionEntry 리스트 생성 → UI 갱신
```

**배터리·성능 제약**:
- `Constraints.Builder().setRequiresBatteryNotLow(true)` 적용
- 백그라운드 스캔은 로컬 조회만, **네트워크 호출 0**
- 결과 메모리 캐시 + 세션 내 유효 (NKB 저장 불필요 — Risk 정보 아님)
- 스캔 시간 중위값 < 200ms 목표 (앱 30개 기준)

### 18-6-5. UX 화면

**MicCheckScreen**: 마이크 권한 보유 앱 목록
- 각 카드: 앱 아이콘·이름·마지막 사용 시각·전경 표시 뱃지
- 탭: 권한 해제 인텐트 직접 실행 (상세 화면 없음, 단순화)
- 상단: "N개 앱이 마이크 권한 보유 중" 요약
- 정렬: 최근 사용 순 (내림차순), 사용 기록 없음은 하단

**MicPermissionActionSheet** (탭 시 하단 시트):
```
[앱 이름]
마지막 사용: 3일 전 (또는 "사용 기록 없음")

[ 🛑 권한 해제 ]   ← ACTION_APPLICATION_DETAILS_SETTINGS
[ 🗑️ 앱 삭제 ]     ← ACTION_UNINSTALL_PACKAGE
[ 취소 ]
```

평판·Justification·CVE 표시 없음. 사용자가 직접 판단.

### 18-6-6. 이관 기록 — AppSecurityWatch (§17-3 후행)

v1.6.1-patch 시점에 MicCheck/CameraCheck에 포함되었던 다음 기능은 **별도 Surface `AppSecurityWatch`**로 이관되었다 (Patch 31):

- 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 (메모리 #13-1)
- 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 (메모리 #13-2)
- NVD CVE API / CISA KEV / Have I Been Pwned 조회
- Decision Engine 기반 앱 평판 판정

본 후행 Surface는 §17-3에 Placeholder로 등록, 별도 스펙 워크오더로 진입 예정.

## 18-7. CameraCheck 본문 (Patch 30 — 단순 관리자 축소, MicCheck 병렬 구조)

### 18-7-1. 재정의

CameraCheck는 MicCheck와 **동일한 단순 관리자 구조**를 가지며, `PermissionScope`만 `CAMERA`로 다르다. 설계 중복 회피를 위해 본 섹션은 차이점만 기술.

**CameraCheck는 CAMERA 권한을 요청하지 않는다** (Patch 23 유지).

**R5 네트워크 경계**: MicCheck §18-6-1과 동일. 직접 네트워크 호출 없음.

### 18-7-2. 데이터 모델

```kotlin
data class CameraPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,
    val isCurrentlyForeground: Boolean
)
```

구조 완전 동일 (이름만 `Camera*`). 공통화 가능하지만 Surface 분리 원칙(§17-1)에 따라 각 Surface가 자기 타입을 가진다.

### 18-7-3. 구현

```kotlin
class CameraCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<CameraPermissionEntry>> {

    override suspend fun check(input: Unit): List<CameraPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.CAMERA
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                CameraPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-7-4. Cold Start·스케줄·UX

MicCheck §18-6-4, §18-6-5와 완전 동일 원칙. WorkManager·BroadcastReceiver·사용자 새로고침 트리거를 공유할 수 있도록 `feature/permission-scan` 공통 모듈로 구현 권장.

화면 이름만 `CameraCheckScreen` / `CameraPermissionActionSheet`.

### 18-7-5. 이관 기록

CameraCheck의 감시·CVE·침해 기능도 Patch 31로 `AppSecurityWatch`에 이관 (§18-6-6과 동일).

## 18-8. (§18-8 JustificationClassifier 삭제 — Patch 30)

v1.6.0~v1.6.1-patch 시점 §18-8에 있던 `JustificationClassifier`와 `AppCategory` 기반 자동 분류 로직은 **Patch 30으로 완전 삭제**되었다.

삭제 사유:
- 대표님 정의 기능 범위를 넘어섬 (사용자 직접 판단이 원칙)
- 카테고리 매핑이 "본사 매핑 아님"이라고 주장했으나, 실제로는 JUSTIFIED/SUSPICIOUS 라벨을 디바이스가 생성 → 헌법 3조 해석 여지
- 단순 리스트·시각·회수 버튼으로 충분

이후 버전에서 부활 금지. 앱 평판 판정이 필요하면 `AppSecurityWatch` Surface(§17-3 후행)에서 별도 설계.

## 18-9. SmokeRun10: Billing 주기

**조건**: L1, Play Billing Library v7 연동
**절차**:
1. 앱 시작 → 구독 상태 조회 (BillingClient.queryPurchasesAsync)
2. 미구독 상태 → "구독" 버튼 표시
3. 버튼 탭 → Play 구독 시트 → 결제
4. 결제 완료 → BillingClient 콜백 수신 → 로컬 구독 상태 업데이트
5. 앱 재시작 → 상태 복원 확인
6. 30일 경과 모사 (테스트 환경) → 갱신 수신 처리
7. 구독 취소 → 만료 시각까지 유지 → 만료 시각 이후 해제

**검증 포인트**:
- BillingClient 오프라인 대응 (네트워크 복귀 시 sync)
- Purchase Token 로컬 저장 (자체 영수증 검증 서버 없음 — 헌법 정합)
- 구독 상태 UI 즉시 반영

## 18-10. SmokeRun11: L3 Offline 기준선 (SLA-14-2 검증)

**조건**: 비행기 모드, NKB에 200건 엔트리 존재
**절차**:
1. 앱 실행
2. 비행기 모드 진입 → SlaLevelDetector가 L3 감지 → 상단 배너 표시
3. CallCheck 통화 수신 (에뮬레이터 트리거) → NKB Hit 경로로 4속성 출력
4. MessageCheck SMS 수신 → 발신번호 NKB Hit → MessageRisk 출력
5. MicCheckScreen 진입 → 이미 스캔된 앱 목록 표시 (외부 조회 없이)
6. 응답 시간 측정

**검증 포인트**:
- NKB Hit p95 ≤ 5ms (JMH 또는 Espresso timer)
- Stale 엔트리는 STALE_OFFLINE 플래그로 그대로 표시
- 사용자 경험 저하 없음 (UI 동일, 배너만 추가)

---
