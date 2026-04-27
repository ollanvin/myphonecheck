package app.myphonecheck.mobile.core.globalengine.parsing.notification

/**
 * 알림 정규화 source (Architecture v2.0.0 §26 + §30).
 *
 * StatusBarNotification → 디바이스 로컬 식별자만 추출. 원문 본문은 별도 단계.
 *
 * 헌법 정합:
 *  - 1조 Out-Bound Zero: 디바이스 로컬만.
 *  - 2조 In-Bound Zero: 식별자(packageName/channelId)만 — 본문은 휴지통 기능에서 별도 처리.
 *  - 8조 SIM-Oriented: SIM 비의존 식별자.
 */
data class NotificationSource(
    val packageName: String,
    val channelId: String,
    val postTime: Long,
    val id: Int,
    val tag: String,
)

/**
 * 알림 본문 특성 (CardCheck 협업 hook).
 *
 * 휴지통이 아닌 카드 거래 추출 등 후속 분석에 사용.
 */
data class NotificationFeatures(
    val title: String,
    val text: String,
    val hasCurrencyPattern: Boolean,
    val bodyLength: Int,
)

/**
 * 종합 결과 — source + 선택적 features.
 */
data class NotificationParseResult(
    val source: NotificationSource,
    val features: NotificationFeatures?,
)
