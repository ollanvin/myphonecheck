package app.callcheck.mobile.feature.countryconfig

/**
 * 프라이버시 신뢰 메시지 번들 — 프리미엄 SaaS급 최종본.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 사용 위치 3곳:                                                │
 * │  1. 온보딩 (앱 최초 실행)                                     │
 * │  2. 설정 화면 (프라이버시 섹션)                                │
 * │  3. 결제 직전 (구독 확인 화면)                                 │
 * │                                                              │
 * │ 핵심 원칙 (자비스 리뷰 2차 반영):                              │
 * │  • 설명형 ❌ → 신뢰 확정형 ✔                                  │
 * │  • 각 언어 최강 절대 표현 사용                                │
 * │  • 설명에서도 핵심 메시지 반복 (신뢰 = 반복)                   │
 * │  • 녹색 박스: 극한 선언형                                     │
 * │  • 7개 언어 × 3개 화면 = 21개 메시지 세트                    │
 * └──────────────────────────────────────────────────────────────┘
 */
data class PrivacyTrustMessages(
    // ── 온보딩 ──
    /** 앱 한 줄 소개 (온보딩 첫 화면) */
    val onboardingTagline: String,
    /** 프라이버시 핵심 헤드라인 — 강력한 선언형 한 문장 */
    val onboardingPrivacyCore: String,
    /** 온보딩 보조 설명 (핵심 아래 작게 배치 — 핵심 반복 포함) */
    val onboardingPrivacyDetail: String,
    /** 3줄 압축 선언 (서버 없음 / 추적 없음 / 유출 리스크 없음) */
    val onboardingNoServerPledge: String,

    // ── 설정 화면 ──
    /** 설정 > 프라이버시 섹션 타이틀 */
    val settingsPrivacyTitle: String,
    /** 설정 > 프라이버시 설명 */
    val settingsPrivacyDescription: String,
    /** 설정 > 데이터 처리 방식 (녹색 박스 — 극한 선언형) */
    val settingsDataHandling: String,

    // ── 결제 직전 ──
    /** 구독 확인 화면 프라이버시 보장 문구 */
    val purchasePrivacyGuarantee: String,
    /** 구독 확인 화면 가치 한 줄 요약 */
    val purchaseValueProposition: String,
) {
    companion object {
        fun forLanguage(language: SupportedLanguage): PrivacyTrustMessages {
            return when (language) {
                SupportedLanguage.KO -> KO
                SupportedLanguage.JA -> JA
                SupportedLanguage.ZH -> ZH
                SupportedLanguage.RU -> RU
                SupportedLanguage.ES -> ES
                SupportedLanguage.AR -> AR
                else -> EN
            }
        }

        // ══════════════════════════════════════
        // 한국어 — "절대/일절" 강화
        // ══════════════════════════════════════
        private val KO = PrivacyTrustMessages(
            onboardingTagline = "당신만의 통화 판단 비서",
            onboardingPrivacyCore = "당신의 데이터는 절대 이 기기를 벗어나지 않습니다",
            onboardingPrivacyDetail =
                "CallCheck는 통화 기록, 검색 결과, 판단 데이터를 외부 서버로 일절 전송하지 않습니다. " +
                "모든 분석은 이 기기 안에서 완결됩니다.",
            onboardingNoServerPledge = "서버 없음\n추적 없음\n유출 리스크 없음",
            settingsPrivacyTitle = "프라이버시 보호",
            settingsPrivacyDescription =
                "CallCheck는 온디바이스 통신 보안 앱입니다. " +
                "통화 분석, 위험도 판정, 검색 스캔 모두 이 기기 안에서만 수행됩니다.",
            settingsDataHandling =
                "검색 데이터는 자동으로 사라집니다\n완전히 삭제됩니다",
            purchasePrivacyGuarantee =
                "구독 결제는 Google Play를 통해 안전하게 처리됩니다. " +
                "CallCheck는 결제 정보를 수집하거나 저장하지 않습니다.",
            purchaseValueProposition =
                "프리미엄 온디바이스 통화 보안 — 서버 없이, 당신의 기기만으로.",
        )

        // ══════════════════════════════════════
        // English — "never/absolutely" 강화
        // ══════════════════════════════════════
        private val EN = PrivacyTrustMessages(
            onboardingTagline = "Your Private Call Decision Assistant",
            onboardingPrivacyCore = "Your data never leaves this device",
            onboardingPrivacyDetail =
                "CallCheck never sends your call history, search results, or decision data to any external server. " +
                "Everything stays on your device.",
            onboardingNoServerPledge = "No servers\nNo tracking\nZero leak risk",
            settingsPrivacyTitle = "Privacy Protection",
            settingsPrivacyDescription =
                "CallCheck is an on-device communication security app. " +
                "Call analysis, risk assessment, and web scanning all happen entirely on this device.",
            settingsDataHandling =
                "Search data disappears automatically\nCompletely deleted",
            purchasePrivacyGuarantee =
                "Subscription payments are securely processed through Google Play. " +
                "CallCheck does not collect or store any payment information.",
            purchaseValueProposition =
                "Premium on-device call security — no servers, just your device.",
        )

        // ══════════════════════════════════════
        // 日本語 — "絶対/一切" 강화 (자비스 JA 튜닝)
        // ══════════════════════════════════════
        private val JA = PrivacyTrustMessages(
            onboardingTagline = "あなたのプライベート通話判断アシスタント",
            onboardingPrivacyCore = "あなたのデータは絶対に外に出ません",
            onboardingPrivacyDetail =
                "CallCheckは通話履歴・検索結果・判定データを外部サーバーには一切送信しません。" +
                "すべてこの端末内で完結します。",
            onboardingNoServerPledge = "サーバーなし\n追跡なし\n漏洩リスクなし",
            settingsPrivacyTitle = "プライバシー保護",
            settingsPrivacyDescription =
                "CallCheckはオンデバイス通信セキュリティアプリです。" +
                "通話分析、リスク評価、Web検索すべてがこの端末内でのみ実行されます。",
            settingsDataHandling =
                "検索データは自動的に消えます\n完全に削除されます",
            purchasePrivacyGuarantee =
                "サブスクリプションの支払いはGoogle Playを通じて安全に処理されます。" +
                "CallCheckは決済情報を一切収集・保存しません。",
            purchaseValueProposition =
                "プレミアムオンデバイス通話セキュリティ — サーバーなし、端末だけで。",
        )

        // ══════════════════════════════════════
        // 中文 — "绝对/丝毫" 강화
        // ══════════════════════════════════════
        private val ZH = PrivacyTrustMessages(
            onboardingTagline = "您的私人来电判断助手",
            onboardingPrivacyCore = "您的数据绝对不会离开这台设备",
            onboardingPrivacyDetail =
                "CallCheck绝不会将通话记录、搜索结果或判断数据发送到任何外部服务器。" +
                "一切分析在设备内完成。",
            onboardingNoServerPledge = "无服务器\n无追踪\n零泄露风险",
            settingsPrivacyTitle = "隐私保护",
            settingsPrivacyDescription =
                "CallCheck是一款设备端通信安全应用。" +
                "通话分析、风险评估和网络扫描全部在此设备上完成。",
            settingsDataHandling =
                "搜索数据自动消失\n彻底删除",
            purchasePrivacyGuarantee =
                "订阅付款通过Google Play安全处理。" +
                "CallCheck绝不收集或存储任何支付信息。",
            purchaseValueProposition =
                "高级设备端通话安全 — 无需服务器，仅靠您的设备。",
        )

        // ══════════════════════════════════════
        // Русский — "абсолютно/никогда" 강화
        // ══════════════════════════════════════
        private val RU = PrivacyTrustMessages(
            onboardingTagline = "Ваш личный помощник по оценке звонков",
            onboardingPrivacyCore = "Ваши данные абсолютно никогда не покинут это устройство",
            onboardingPrivacyDetail =
                "CallCheck не отправляет историю звонков, результаты поиска или данные оценки на внешние серверы. Абсолютно ничего. " +
                "Весь анализ выполняется на этом устройстве.",
            onboardingNoServerPledge = "Без серверов\nБез слежки\nНулевой риск утечки",
            settingsPrivacyTitle = "Защита конфиденциальности",
            settingsPrivacyDescription =
                "CallCheck — это приложение для безопасности связи, работающее на устройстве. " +
                "Анализ звонков, оценка рисков и веб-сканирование выполняются исключительно на этом устройстве.",
            settingsDataHandling =
                "Данные поиска исчезают автоматически\nПолностью удаляются",
            purchasePrivacyGuarantee =
                "Платежи за подписку безопасно обрабатываются через Google Play. " +
                "CallCheck не собирает и не хранит платёжную информацию.",
            purchaseValueProposition =
                "Премиум-безопасность звонков на устройстве — без серверов, только ваше устройство.",
        )

        // ══════════════════════════════════════
        // Español — "jamás/absolutamente" 강화
        // ══════════════════════════════════════
        private val ES = PrivacyTrustMessages(
            onboardingTagline = "Tu asistente privado de evaluación de llamadas",
            onboardingPrivacyCore = "Tus datos jamás salen de este dispositivo",
            onboardingPrivacyDetail =
                "CallCheck jamás envía tu historial de llamadas, resultados de búsqueda ni datos de evaluación a ningún servidor externo. " +
                "Todo se queda en tu dispositivo.",
            onboardingNoServerPledge = "Sin servidores\nSin rastreo\nCero riesgo de fuga",
            settingsPrivacyTitle = "Protección de privacidad",
            settingsPrivacyDescription =
                "CallCheck es una app de seguridad de comunicaciones en el dispositivo. " +
                "El análisis de llamadas, la evaluación de riesgos y el escaneo web se realizan únicamente en este dispositivo.",
            settingsDataHandling =
                "Los datos de búsqueda desaparecen automáticamente\nCompletamente eliminados",
            purchasePrivacyGuarantee =
                "Los pagos de suscripción se procesan de forma segura a través de Google Play. " +
                "CallCheck no recopila ni almacena ninguna información de pago.",
            purchaseValueProposition =
                "Seguridad premium de llamadas en el dispositivo — sin servidores, solo tu dispositivo.",
        )

        // ══════════════════════════════════════
        // العربية — "مطلقاً/أبداً" 강화
        // ══════════════════════════════════════
        private val AR = PrivacyTrustMessages(
            onboardingTagline = "مساعدك الخاص لتقييم المكالمات",
            onboardingPrivacyCore = "بياناتك لا تغادر هذا الجهاز مطلقاً",
            onboardingPrivacyDetail =
                "لا يرسل CallCheck سجل مكالماتك أو نتائج البحث أو بيانات التقييم إلى أي خادم خارجي مطلقاً. " +
                "كل شيء يبقى على جهازك.",
            onboardingNoServerPledge = "لا خوادم\nلا تتبع\nصفر مخاطر تسريب",
            settingsPrivacyTitle = "حماية الخصوصية",
            settingsPrivacyDescription =
                "CallCheck هو تطبيق أمان اتصالات يعمل على الجهاز. " +
                "يتم تحليل المكالمات وتقييم المخاطر ومسح الويب بالكامل على هذا الجهاز.",
            settingsDataHandling =
                "بيانات البحث تختفي تلقائياً\nيتم حذفها بالكامل",
            purchasePrivacyGuarantee =
                "تتم معالجة مدفوعات الاشتراك بشكل آمن عبر Google Play. " +
                "لا يجمع CallCheck أو يخزن أي معلومات دفع مطلقاً.",
            purchaseValueProposition =
                "أمان مكالمات متميز على الجهاز — بدون خوادم، فقط جهازك.",
        )
    }
}
