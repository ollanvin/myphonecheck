package app.myphonecheck.mobile.matrix

/**
 * Phase 4 매트릭스 검증 시나리오 17종 (헌법 §9-6 정합).
 *
 * S01~S11: Architecture v2.3.0 §18-1 SmokeRun 11종 (CallCheck/MessageCheck/MicCheck/CameraCheck 핵심).
 * S12: Initial Scan 베이스데이터 일괄 구축 (§28).
 * S13: SIM-Oriented Locale 자동 추종 (§29, MCC/MNC -> 통화·전화양식).
 * S14: Real-time Action — CallScreening 50ms PASS fallback (§31).
 * S15: Tag System — SUSPICIOUS 매칭 -> SILENT (§32).
 * S16: FeedRegistry — KISA 활성 출처 매칭 (§30-4).
 * S17: 4-Layer Data Model — Layer 우선순위 2·1·3·4 (§30-3-A).
 */
enum class ScenarioId(val displayName: String, val surface: String) {
    S01("CallCheck inbound — known number", "CallCheck"),
    S02("CallCheck inbound — unknown number", "CallCheck"),
    S03("CallCheck outbound — local-relationship profile", "CallCheck"),
    S04("MessageCheck — bank notification (legitimate)", "MessageCheck"),
    S05("MessageCheck — delivery phishing (suspicious)", "MessageCheck"),
    S06("MessageCheck — Coupang impersonation", "MessageCheck"),
    S07("MicCheck — voice quality probe", "MicCheck"),
    S08("CameraCheck — permission probe", "CameraCheck"),
    S09("CardCheck — month spend aggregation", "CardCheck"),
    S10("PushCheck — notification quarantine", "PushCheck"),
    S11("Six Surfaces integration — single core", "Integration"),
    S12("Initial Scan — base data construction", "InitialScan"),
    S13("SIM-Oriented — Locale auto-follow", "SIMCore"),
    S14("Real-time Action — 50ms PASS fallback", "RealTime"),
    S15("Tag System — SUSPICIOUS to SILENT", "TagSystem"),
    S16("FeedRegistry — KISA active source match", "FeedRegistry"),
    S17("4-Layer Data Model — priority 2-1-3-4", "DataModel"),
    ;

    companion object {
        const val ARG_SCENARIO = "scenario"
        fun fromArg(arg: String?): ScenarioId? = arg?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}
