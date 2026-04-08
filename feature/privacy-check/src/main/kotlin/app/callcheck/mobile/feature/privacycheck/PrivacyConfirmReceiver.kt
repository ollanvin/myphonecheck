package app.callcheck.mobile.feature.privacycheck

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.callcheck.mobile.data.localcache.dao.PrivacyHistoryDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PrivacyCheck 사용자 확인 BroadcastReceiver.
 *
 * 이상 탐지 알림에서 "내 활동 맞음"을 선택했을 때
 * Room DB의 userVerified를 "CONFIRMED"로 업데이트합니다.
 */
@AndroidEntryPoint
class PrivacyConfirmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var privacyHistoryDao: PrivacyHistoryDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != PrivacyCheckCollector.ACTION_PRIVACY_CONFIRM) return

        val entityId = intent.getLongExtra(PrivacyCheckCollector.EXTRA_ENTITY_ID, -1L)
        if (entityId < 0) return

        val pendingResult = goAsync()

        scope.launch {
            try {
                privacyHistoryDao.updateVerified(entityId, "CONFIRMED")
                Log.d("PrivacyConfirmReceiver", "Verified CONFIRMED: id=$entityId")
            } catch (e: Exception) {
                Log.e("PrivacyConfirmReceiver", "Failed to update verified status", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
