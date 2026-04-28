/**
 * v1.1 PRIMARY: PhoneStateListener 기반 통화 감지.
 *
 * v1.1 아키텍처 결정:
 * - CallScreeningService → PhoneStateListener로 전환
 * - 이유: CallScreeningService는 기본 전화 앱 설정 필요,
 *   PhoneStateListener는 READ_PHONE_STATE 퍼미션만으로 동작
 *
 * 전환 완료 시 MyPhoneCheckScreeningService는 제거 대상.
 */
package app.myphonecheck.mobile.legacy

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.content.Context

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

    fun stop() {
        telephonyManager.listen(object : PhoneStateListener() {}, PhoneStateListener.LISTEN_NONE)
    }
}
