/**
 * v4.3 LEGACY: This file has been moved to legacy/ and is no longer used.
 * Retained for reference only. Do not import.
 */
package app.myphonecheck.mobile.legacy

import android.util.Log

@Deprecated("v4.3: moved to legacy, do not use")
object OverlayTrigger {
    fun fire(number: String?) {
        Log.d("MPC_OVERLAY", "FORCE_OVERLAY: " + number)
    }
}
