package app.myphonecheck.mobile.matrix

/**
 * Phase 4 매트릭스 검증 SIM 11개국 (헌법 §9-6 정합).
 *
 * CORE 4 (PR 게이트): KR, US, JP, DE.
 * EXTENDED 4 (야간): GB, IN, BR, CN.
 * SPECIALIZED 3 (야간, 라틴·CJK·RTL·동남아 커버): TW, SA(RTL), TH.
 *
 * 각 SIM은 adb emu gsm 시뮬레이션으로 적용한다 (실 SIM 카드 0).
 */
enum class SimMatrixContext(
    val countryIso: String,
    val locale: String,
    val mccMnc: String,
    val tier: SimTier,
) {
    KR("kr", "ko-KR", "45005", SimTier.CORE),
    US("us", "en-US", "310260", SimTier.CORE),
    JP("jp", "ja-JP", "44010", SimTier.CORE),
    DE("de", "de-DE", "26201", SimTier.CORE),
    GB("gb", "en-GB", "23410", SimTier.EXTENDED),
    IN("in", "hi-IN", "40410", SimTier.EXTENDED),
    BR("br", "pt-BR", "72402", SimTier.EXTENDED),
    CN("cn", "zh-CN", "46000", SimTier.EXTENDED),
    TW("tw", "zh-TW", "46692", SimTier.SPECIALIZED),
    SA("sa", "ar-SA", "42001", SimTier.SPECIALIZED),
    TH("th", "th-TH", "52001", SimTier.SPECIALIZED),
    ;

    companion object {
        const val ARG_SIM = "sim"
        fun fromArg(arg: String?): SimMatrixContext? = arg?.let { runCatching { valueOf(it) }.getOrNull() }
        val coreTier: List<SimMatrixContext> get() = values().filter { it.tier == SimTier.CORE }
        val nightlyTier: List<SimMatrixContext> get() = values().toList()
    }
}

enum class SimTier { CORE, EXTENDED, SPECIALIZED }
