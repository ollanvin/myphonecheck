# MessageCheck (문자 위험도 판정, Mode A/B)

**원본 출처**: v1.7.1 §18-4 (0줄)
**v1.8.0 Layer**: Feature
**의존**: `07_engine/05_decision_formula.md` + `10_policy/02_sms_mode.md`
**변경 이력**: 본 파일은 v1.7.1 §18-4 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/20_features/22_message.md`

---

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

