package app.myphonecheck.mobile.data.localcache.entity

enum class NumberProfileBlockState(val storageKey: String) {
    NONE("NONE"),
    BLOCKED("BLOCKED"),
    DO_NOT_BLOCK("DO_NOT_BLOCK");

    companion object {
        fun fromStorage(raw: String?): NumberProfileBlockState =
            entries.firstOrNull { it.storageKey == raw } ?: NONE
    }
}
