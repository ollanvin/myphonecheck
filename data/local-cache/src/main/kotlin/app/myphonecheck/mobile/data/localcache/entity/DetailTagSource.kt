package app.myphonecheck.mobile.data.localcache.entity

enum class DetailTagSource(val storageKey: String) {
    USER("user"),
    SYSTEM_SUGGESTED("systemSuggested");

    companion object {
        fun fromStorage(raw: String?): DetailTagSource =
            entries.firstOrNull { it.storageKey == raw } ?: USER
    }
}
