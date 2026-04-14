package app.myphonecheck.mobile.data.localcache.entity

enum class QuickLabel(
    val storageKey: String,
    val displayName: String,
) {
    IMPORTANT("IMPORTANT", "중요"),
    REVIEW("REVIEW", "다시 확인"),
    BUSINESS("BUSINESS", "거래선"),
    PICK_UP("PICK_UP", "받기 우선"),
    SMS_ONLY("SMS_ONLY", "문자 우선"),
    CAUTION("CAUTION", "주의"),
    DO_NOT_BLOCK("DO_NOT_BLOCK", "차단 금지"),
    DONE("DONE", "완료");

    companion object {
        fun fromStorage(raw: String): Set<QuickLabel> =
            raw.split("|")
                .mapNotNull { token -> entries.firstOrNull { it.storageKey == token } }
                .toSet()

        fun toStorage(labels: Set<QuickLabel>): String =
            labels.map { it.storageKey }
                .sorted()
                .joinToString("|")
    }
}
