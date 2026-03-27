package app.callcheck.mobile.feature.callintercept

import android.util.Log
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.feature.decisionengine.DecisionEngine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val TAG = "CallInterceptRepo"
private const val DEVICE_EVIDENCE_TIMEOUT_MS = 1000L
private const val SEARCH_TIMEOUT_MS = 3000L

/**
 * Production implementation of the call intercept pipeline.
 *
 * Gathers device evidence and search evidence in parallel,
 * then feeds both to DecisionEngine for final scoring.
 */
class CallInterceptRepositoryImpl @Inject constructor(
    private val deviceEvidenceProvider: DeviceEvidenceProvider,
    private val searchEvidenceProvider: SearchEvidenceProvider,
    private val decisionEngine: DecisionEngine,
) : CallInterceptRepository {

    override suspend fun processIncomingCall(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): DecisionResult {
        return coroutineScope {
            try {
                Log.d(TAG, "Pipeline start: $normalizedNumber (country=$deviceCountryCode)")

                // Parallel: device evidence + search enrichment
                val deviceJob = async {
                    withTimeoutOrNull(DEVICE_EVIDENCE_TIMEOUT_MS) {
                        try {
                            deviceEvidenceProvider.gather(normalizedNumber)
                        } catch (e: Exception) {
                            Log.e(TAG, "Device evidence error", e)
                            null
                        }
                    }
                }

                val searchJob = async {
                    withTimeoutOrNull(SEARCH_TIMEOUT_MS) {
                        try {
                            searchEvidenceProvider.gather(normalizedNumber, deviceCountryCode)
                        } catch (e: Exception) {
                            Log.w(TAG, "Search evidence error", e)
                            null
                        }
                    }
                }

                val deviceEvidence = deviceJob.await()
                val searchEvidence = searchJob.await()

                Log.d(TAG, "Evidence gathered - device: ${deviceEvidence != null}, search: ${searchEvidence != null}")

                // Decision engine — synchronous, < 50ms
                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                )

                Log.d(TAG, "Decision: ${result.category} / ${result.riskLevel} / ${result.action}")
                result

            } catch (e: Exception) {
                Log.e(TAG, "Pipeline error", e)
                DecisionResult.fallback()
            }
        }
    }
}
