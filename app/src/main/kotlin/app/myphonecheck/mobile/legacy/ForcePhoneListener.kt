/**
 * v4.3 LEGACY: This file has been moved to legacy/ and is no longer used.
 * Uses deprecated PhoneStateListener API.
 * Retained for reference only. Do not import.
 */
package app.myphonecheck.mobile.legacy

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.content.Context

@Deprecated("v4.3: moved to legacy, do not use")
class ForcePhoneListener(context: Context) {

    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun start() {
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Log.d("MPC_SCREEN", "RINGING DETECTED: " + phoneNumber)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }
}
