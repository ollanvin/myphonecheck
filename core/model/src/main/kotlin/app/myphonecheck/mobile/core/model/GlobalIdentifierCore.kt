package app.myphonecheck.mobile.core.model

/**
 * Global single-core entry point shared by call and SMS analysis.
 *
 * The identifier itself is the primary analysis target.
 * Message content remains optional metadata and must not become a separate engine.
 */
interface GlobalIdentifierCore {
    suspend fun analyzeIdentifier(
        input: IdentifierAnalysisInput,
    ): DecisionResult

    suspend fun analyzeIdentifierTwoPhase(
        input: IdentifierAnalysisInput,
    ): TwoPhaseDecision
}

enum class IdentifierChannel {
    CALL,
    SMS,
}

data class IdentifierMessageMetadata(
    val hasUrl: Boolean = false,
    val urlCount: Int = 0,
    val longestUrlLength: Int = 0,
    val hasShortLink: Boolean = false,
)

data class IdentifierAnalysisInput(
    val normalizedNumber: String,
    val deviceCountryCode: String?,
    val channel: IdentifierChannel,
    val isSavedContact: Boolean = false,
    val actionState: ActionState? = null,
    val messageMetadata: IdentifierMessageMetadata? = null,
)
