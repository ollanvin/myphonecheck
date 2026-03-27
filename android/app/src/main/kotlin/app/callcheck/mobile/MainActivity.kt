package app.callcheck.mobile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import app.callcheck.mobile.navigation.CallCheckNavHost
import app.callcheck.mobile.ui.theme.CallCheckTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"
private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SYSTEM_ALERT_WINDOW 권한 요청
        // Truecaller 패턴: 전화 앱 위에 판정 결과를 표시하기 위해 필요
        requestOverlayPermissionIfNeeded()

        setContent {
            CallCheckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CallCheckNavHost()
                }
            }
        }
    }

    /**
     * SYSTEM_ALERT_WINDOW 권한이 없으면 설정 화면으로 안내한다.
     *
     * 이 권한이 있어야 ringing 중 전화 앱 위에 판정 결과를 표시할 수 있다.
     * 권한이 없으면 Notification fallback으로 동작 (알림 패널을 내려야 보임).
     */
    private fun requestOverlayPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Log.i(TAG, "Requesting SYSTEM_ALERT_WINDOW permission")
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"),
                    )
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to open overlay permission settings", e)
                }
            } else {
                Log.i(TAG, "SYSTEM_ALERT_WINDOW already granted")
            }
        }
    }

    @Deprecated("Deprecated in API 30+")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.i(TAG, "SYSTEM_ALERT_WINDOW granted by user")
                } else {
                    Log.w(TAG, "SYSTEM_ALERT_WINDOW denied — overlay will not work, notification fallback active")
                }
            }
        }
    }
}
