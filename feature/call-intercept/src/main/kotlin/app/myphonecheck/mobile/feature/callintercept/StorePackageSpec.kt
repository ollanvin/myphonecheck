package app.myphonecheck.mobile.feature.callintercept

/**
 * Stage 15 — 스토어 패키지 사양 확정.
 *
 * 자비스 기준:
 * "앱 설명 (글로벌 기준 단일 메시지), 스크린샷 5~8장,
 *  권한 설명 (Call intercept 관련), 개인정보/데이터 처리 문구 고정.
 *
 *  핵심 문장 반드시 포함:
 *  '이 앱은 전화를 자동으로 차단하거나 수신하지 않습니다.
 *   사용자의 판단을 돕기 위한 정보만 제공합니다.'"
 *
 * 모든 문구는 상수로 고정. 런타임 변경 불가.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
object StorePackageSpec {

    // ══════════════════════════════════════════════════════════════
    // 1. 앱 기본 정보
    // ══════════════════════════════════════════════════════════════

    const val APP_NAME = "MyPhoneCheck"
    const val PACKAGE_NAME = "app.myphonecheck.mobile"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1
    const val MIN_SDK = 26
    const val TARGET_SDK = 34
    const val CATEGORY = "Communication"
    const val CONTENT_RATING = "Everyone"
    const val PRICE = "$9.99/month"

    // ══════════════════════════════════════════════════════════════
    // 2. 앱 설명 (글로벌 단일 메시지)
    // ══════════════════════════════════════════════════════════════

    /** 짧은 설명 (80자 이내) — Play Store 검색 결과에 표시 */
    const val SHORT_DESCRIPTION =
        "Real-time caller information to help you decide before you answer."

    /** 전체 설명 — Play Store 상세 페이지 */
    const val FULL_DESCRIPTION = """MyPhoneCheck provides real-time information about incoming calls to help you make informed decisions.

When a call comes in, MyPhoneCheck instantly analyzes the phone number using on-device search technology and presents you with relevant information — all within 2 seconds.

KEY FEATURES:
• Real-time caller analysis in under 2 seconds
• Works in 190+ countries with localized search
• 100% on-device processing — your data never leaves your phone
• No automatic blocking — you always make the decision
• Color-coded risk indicators (Green/Yellow/Red)
• Country-specific search engines for maximum accuracy
• Works offline with cached results

IMPORTANT: This app does NOT automatically block or answer calls. It only provides information to help your judgment.

PRIVACY FIRST:
• Zero data collection — no server, no cloud
• No personal information stored or transmitted
• No tracking, no analytics, no ads
• All processing happens on your device

SUPPORTED COUNTRIES:
190+ countries with Tier A coverage for KR, JP, CN, RU, CZ and Tier B/C/D coverage worldwide.

MyPhoneCheck — Know before you answer."""

    // ══════════════════════════════════════════════════════════════
    // 3. 핵심 고지 문구 (자비스 필수 포함 지시)
    // ══════════════════════════════════════════════════════════════

    /** 자비스 필수 포함 문장 — 한국어 */
    const val MANDATORY_NOTICE_KO =
        "이 앱은 전화를 자동으로 차단하거나 수신하지 않습니다. 사용자의 판단을 돕기 위한 정보만 제공합니다."

    /** 자비스 필수 포함 문장 — 영어 */
    const val MANDATORY_NOTICE_EN =
        "This app does NOT automatically block or answer calls. It only provides information to help your judgment."

    /** 자비스 필수 포함 문장 — 일본어 */
    const val MANDATORY_NOTICE_JA =
        "このアプリは自動的に電話をブロックしたり応答したりしません。判断を助けるための情報のみを提供します。"

    /** 자비스 필수 포함 문장 — 중국어 */
    const val MANDATORY_NOTICE_ZH =
        "本应用不会自动拦截或接听电话。仅提供信息以帮助您做出判断。"

    /** 모든 필수 고지 문구 (언어별) */
    val MANDATORY_NOTICES: Map<String, String> = mapOf(
        "ko" to MANDATORY_NOTICE_KO,
        "en" to MANDATORY_NOTICE_EN,
        "ja" to MANDATORY_NOTICE_JA,
        "zh" to MANDATORY_NOTICE_ZH,
        "ru" to "Это приложение НЕ блокирует и НЕ принимает звонки автоматически. Оно предоставляет только информацию для вашего решения.",
        "es" to "Esta aplicación NO bloquea ni contesta llamadas automáticamente. Solo proporciona información para ayudar en su decisión.",
        "fr" to "Cette application NE bloque PAS et NE répond PAS automatiquement aux appels. Elle fournit uniquement des informations pour vous aider à décider.",
        "de" to "Diese App blockiert oder beantwortet Anrufe NICHT automatisch. Sie liefert nur Informationen, um Ihre Entscheidung zu unterstützen.",
        "pt" to "Este aplicativo NÃO bloqueia ou atende chamadas automaticamente. Apenas fornece informações para ajudar na sua decisão.",
        "ar" to "هذا التطبيق لا يحظر أو يرد على المكالمات تلقائيًا. يوفر فقط معلومات لمساعدتك في اتخاذ القرار.",
        "hi" to "यह ऐप स्वचालित रूप से कॉल ब्लॉक या उत्तर नहीं देता। यह केवल आपके निर्णय में मदद के लिए जानकारी प्रदान करता है।",
        "th" to "แอปนี้ไม่บล็อกหรือรับสายโดยอัตโนมัติ ให้เฉพาะข้อมูลเพื่อช่วยในการตัดสินใจของคุณ",
        "vi" to "Ứng dụng này KHÔNG tự động chặn hoặc trả lời cuộc gọi. Chỉ cung cấp thông tin để hỗ trợ quyết định của bạn.",
        "tr" to "Bu uygulama aramaları otomatik olarak engellemez veya yanıtlamaz. Yalnızca kararınıza yardımcı olacak bilgi sağlar.",
        "id" to "Aplikasi ini TIDAK memblokir atau menjawab panggilan secara otomatis. Hanya menyediakan informasi untuk membantu keputusan Anda.",
    )

    // ══════════════════════════════════════════════════════════════
    // 4. 권한 설명 (Google Play Data Safety 대응)
    // ══════════════════════════════════════════════════════════════

    /** 필요 권한 목록 + 사유 */
    data class PermissionSpec(
        val permission: String,
        val reason: String,
        val required: Boolean,
    )

    val REQUIRED_PERMISSIONS = listOf(
        PermissionSpec(
            permission = "android.permission.READ_PHONE_STATE",
            reason = "To detect incoming calls and display caller information",
            required = true,
        ),
        // v4.3: READ_CALL_LOG removed per PERMISSION POLICY (DENY)
        PermissionSpec(
            permission = "android.permission.BIND_SCREENING_SERVICE",
            reason = "To provide call screening information before you answer",
            required = true,
        ),
        PermissionSpec(
            permission = "android.permission.SYSTEM_ALERT_WINDOW",
            reason = "To display caller information overlay during incoming calls",
            required = true,
        ),
        PermissionSpec(
            permission = "android.permission.INTERNET",
            reason = "To search public information about incoming phone numbers",
            required = true,
        ),
        PermissionSpec(
            permission = "android.permission.POST_NOTIFICATIONS",
            reason = "To notify you about call analysis results",
            required = false,
        ),
    )

    // ══════════════════════════════════════════════════════════════
    // 5. 개인정보/데이터 처리 정책
    // ══════════════════════════════════════════════════════════════

    /** Data Safety Section — Google Play Console 대응 */
    object DataSafety {
        const val DATA_COLLECTED = false
        const val DATA_SHARED = false
        const val DATA_ENCRYPTED_IN_TRANSIT = true // HTTPS 통신
        const val DATA_DELETION_AVAILABLE = true // 앱 삭제 시 모든 데이터 삭제

        const val PRIVACY_POLICY_SUMMARY = """MyPhoneCheck Privacy Policy Summary:

1. DATA COLLECTION: None.
   MyPhoneCheck does not collect, store, or transmit any personal data.

2. ON-DEVICE PROCESSING: All call analysis is performed entirely on your device.
   No data is sent to any server.

3. SEARCH QUERIES: Phone number lookups are performed through public search engines
   (Google, Naver, Baidu, etc.) directly from your device, the same as if you
   searched manually in a browser.

4. LOCAL STORAGE: Only cached search results and your personal settings are stored
   locally on your device. This data is never transmitted.

5. NO TRACKING: No analytics, no advertising IDs, no user tracking of any kind.

6. DATA DELETION: Uninstalling the app removes all locally stored data.
   There is no server-side data to delete because none was ever collected.

7. THIRD PARTIES: MyPhoneCheck does not share any information with third parties.

Contact: privacy@myphonecheck.app"""

        /** 개인정보 처리 방침 URL */
        const val PRIVACY_POLICY_URL = "https://myphonecheck.app/privacy"
        /** 이용약관 URL */
        const val TERMS_OF_SERVICE_URL = "https://myphonecheck.app/terms"
    }

    // ══════════════════════════════════════════════════════════════
    // 6. 스크린샷 사양 (5~8장)
    // ══════════════════════════════════════════════════════════════

    data class ScreenshotSpec(
        val index: Int,
        val filename: String,
        val description: String,
        val requiredElements: List<String>,
    )

    val SCREENSHOTS = listOf(
        ScreenshotSpec(
            index = 1,
            filename = "01_incoming_call_safe.png",
            description = "Incoming call — Green ring (Safe)",
            requiredElements = listOf("Green ring", "Caller info", "Safe badge", "Answer/Reject buttons"),
        ),
        ScreenshotSpec(
            index = 2,
            filename = "02_incoming_call_warning.png",
            description = "Incoming call — Yellow ring (Warning)",
            requiredElements = listOf("Yellow ring", "Risk reason text", "Warning badge", "Answer/Reject/Block buttons"),
        ),
        ScreenshotSpec(
            index = 3,
            filename = "03_incoming_call_danger.png",
            description = "Incoming call — Red ring (Danger)",
            requiredElements = listOf("Red ring", "Scam warning text", "Danger badge", "Reject/Block buttons"),
        ),
        ScreenshotSpec(
            index = 4,
            filename = "04_call_detail.png",
            description = "Call detail view — Full analysis results",
            requiredElements = listOf("Risk score", "Evidence list", "Search results summary", "Disclaimer"),
        ),
        ScreenshotSpec(
            index = 5,
            filename = "05_call_history.png",
            description = "Call history with risk tags",
            requiredElements = listOf("Call list", "Color-coded risk tags", "Date/time", "Country flags"),
        ),
        ScreenshotSpec(
            index = 6,
            filename = "06_settings.png",
            description = "Settings — Privacy-first configuration",
            requiredElements = listOf("On-device processing toggle", "Notification settings", "Language preference"),
        ),
        ScreenshotSpec(
            index = 7,
            filename = "07_first_run.png",
            description = "First run — Permission setup and tutorial",
            requiredElements = listOf("Permission explanation", "Tutorial steps", "Mandatory notice text"),
        ),
        ScreenshotSpec(
            index = 8,
            filename = "08_widget.png",
            description = "Home widget — Recent call decisions",
            requiredElements = listOf("Widget preview", "Recent decisions", "Quick status"),
        ),
    )

    // ══════════════════════════════════════════════════════════════
    // 7. 국가 배포 설정
    // ══════════════════════════════════════════════════════════════

    /** 글로벌 배포: 191개국 동시 출시 */
    const val DISTRIBUTION_MODE = "GLOBAL_SIMULTANEOUS"
    const val SUPPORTED_COUNTRY_COUNT = 191
    const val DEFAULT_LANGUAGE = "en"

    val SUPPORTED_LANGUAGES = listOf(
        "en", "ko", "ja", "zh", "ru", "es", "fr", "de", "pt",
        "ar", "hi", "th", "vi", "tr", "id",
    )

    // ══════════════════════════════════════════════════════════════
    // 8. 검증
    // ══════════════════════════════════════════════════════════════

    data class StoreReadinessResult(
        val mandatoryNoticePresent: Boolean,
        val shortDescriptionOk: Boolean,
        val fullDescriptionOk: Boolean,
        val permissionsDocumented: Boolean,
        val dataSafetyComplete: Boolean,
        val screenshotSpecReady: Boolean,
        val allLanguagesCovered: Boolean,
        val passed: Boolean,
    )

    /**
     * 스토어 패키지 준비 상태 검증.
     */
    fun verifyStoreReadiness(): StoreReadinessResult {
        // 필수 고지 문구 포함 확인
        val mandatoryNoticePresent = FULL_DESCRIPTION.contains("does NOT automatically block") &&
                MANDATORY_NOTICE_KO.isNotBlank() &&
                MANDATORY_NOTICE_EN.isNotBlank()

        // 짧은 설명 80자 이내
        val shortDescriptionOk = SHORT_DESCRIPTION.length <= 80 && SHORT_DESCRIPTION.isNotBlank()

        // 전체 설명 4000자 이내 (Play Store 제한)
        val fullDescriptionOk = FULL_DESCRIPTION.length <= 4000 && FULL_DESCRIPTION.isNotBlank()

        // 권한 전부 문서화
        val permissionsDocumented = REQUIRED_PERMISSIONS.all { it.reason.isNotBlank() }

        // Data Safety 완성
        val dataSafetyComplete = !DataSafety.DATA_COLLECTED &&
                !DataSafety.DATA_SHARED &&
                DataSafety.PRIVACY_POLICY_SUMMARY.isNotBlank()

        // 스크린샷 5~8장
        val screenshotSpecReady = SCREENSHOTS.size in 5..8

        // 15개 언어 커버
        val allLanguagesCovered = MANDATORY_NOTICES.size >= 15 &&
                SUPPORTED_LANGUAGES.all { lang -> MANDATORY_NOTICES.containsKey(lang) }

        val passed = mandatoryNoticePresent && shortDescriptionOk && fullDescriptionOk &&
                permissionsDocumented && dataSafetyComplete && screenshotSpecReady && allLanguagesCovered

        return StoreReadinessResult(
            mandatoryNoticePresent = mandatoryNoticePresent,
            shortDescriptionOk = shortDescriptionOk,
            fullDescriptionOk = fullDescriptionOk,
            permissionsDocumented = permissionsDocumented,
            dataSafetyComplete = dataSafetyComplete,
            screenshotSpecReady = screenshotSpecReady,
            allLanguagesCovered = allLanguagesCovered,
            passed = passed,
        )
    }

    /**
     * 스토어 준비 보고서 포맷팅.
     */
    fun formatReport(result: StoreReadinessResult): String {
        val sb = StringBuilder()

        sb.appendLine("╔══════════════════════════════════════════════════════════════╗")
        sb.appendLine("║           [STORE READY] 스토어 패키지 확정 보고서              ║")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        val overallStatus = if (result.passed) "✅ PASS" else "❌ FAIL"
        sb.appendLine("║  종합 판정: $overallStatus")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        sb.appendLine("║  [1] 필수 고지 문구     — ${mark(result.mandatoryNoticePresent)}")
        sb.appendLine("║  [2] 짧은 설명 (≤80자)  — ${mark(result.shortDescriptionOk)} (${SHORT_DESCRIPTION.length}자)")
        sb.appendLine("║  [3] 전체 설명 (≤4000자) — ${mark(result.fullDescriptionOk)} (${FULL_DESCRIPTION.length}자)")
        sb.appendLine("║  [4] 권한 문서화         — ${mark(result.permissionsDocumented)} (${REQUIRED_PERMISSIONS.size}개)")
        sb.appendLine("║  [5] Data Safety 완성    — ${mark(result.dataSafetyComplete)}")
        sb.appendLine("║  [6] 스크린샷 사양       — ${mark(result.screenshotSpecReady)} (${SCREENSHOTS.size}장)")
        sb.appendLine("║  [7] 15개 언어 커버      — ${mark(result.allLanguagesCovered)} (${MANDATORY_NOTICES.size}개 언어)")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")
        sb.appendLine("║  앱 이름: $APP_NAME")
        sb.appendLine("║  패키지: $PACKAGE_NAME")
        sb.appendLine("║  버전: $VERSION_NAME ($VERSION_CODE)")
        sb.appendLine("║  가격: $PRICE")
        sb.appendLine("║  배포: $DISTRIBUTION_MODE (${SUPPORTED_COUNTRY_COUNT}개국)")

        sb.appendLine("╚══════════════════════════════════════════════════════════════╝")
        return sb.toString()
    }

    private fun mark(pass: Boolean): String = if (pass) "✅" else "❌"
}
