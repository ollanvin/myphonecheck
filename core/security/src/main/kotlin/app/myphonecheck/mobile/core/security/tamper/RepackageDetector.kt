package app.myphonecheck.mobile.core.security.tamper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 리패키징/변조 탐지.
 *
 * APK 서명 해시, 패키지명, 설치 출처를 검증.
 * 올랑방 앱팩토리 공통 — 각 앱은 EXPECTED 값만 교체.
 */
@Singleton
class RepackageDetector @Inject constructor(
    private val context: Context,
) {
    companion object {
        /**
         * 앱 서명의 SHA-256 해시 (릴리스 키스토어 기준).
         * 빌드 후 실제 해시로 교체 필요.
         *
         * 추출 방법:
         * keytool -list -v -keystore your.keystore | grep SHA256
         */
        private const val EXPECTED_SIGNATURE_HASH =
            "PLACEHOLDER_REPLACE_WITH_ACTUAL_RELEASE_SIGNING_HASH"

        private const val EXPECTED_PACKAGE_NAME = "app.myphonecheck.mobile"

        /** Google Play Store installer package */
        private const val PLAY_STORE_INSTALLER = "com.android.vending"
    }

    /** 리패키징 탐지 점수 반환 (0-100) */
    fun detect(): Int {
        var score = 0

        score += checkSignature()
        score += checkPackageName()
        score += checkInstaller()
        score += checkDebugBuild()

        return score.coerceIn(0, 100)
    }

    /** APK 서명 해시 검증 (가중치: 40) */
    private fun checkSignature(): Int {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES,
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES,
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) return 40

            val sig = signatures[0]
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(sig.toByteArray())
            val hexHash = hash.joinToString("") { "%02x".format(it) }

            // 플레이스홀더 상태면 검증 건너뜀 (개발 중)
            if (EXPECTED_SIGNATURE_HASH.startsWith("PLACEHOLDER")) return 0

            return if (hexHash != EXPECTED_SIGNATURE_HASH) 40 else 0
        } catch (_: Exception) {
            return 0 // 검증 실패 시 점수 가산 안 함
        }
    }

    /** 패키지명 검증 (가중치: 30) */
    private fun checkPackageName(): Int {
        return if (context.packageName != EXPECTED_PACKAGE_NAME) 30 else 0
    }

    /** 설치 출처 검증 — Google Play 필수 (가중치: 20) */
    private fun checkInstaller(): Int {
        try {
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName)
                    .installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }

            // 디버그 빌드에서는 installer가 null일 수 있으므로 검증 건너뜀
            if (installer == null) {
                // adb install 또는 직접 설치 — 개발 중이면 허용
                return if (isDebugBuild()) 0 else 20
            }

            return if (installer != PLAY_STORE_INSTALLER) 20 else 0
        } catch (_: Exception) {
            return 0
        }
    }

    /** 디버그 빌드 검출 (가중치: 10) — release에서 debuggable이면 변조 */
    private fun checkDebugBuild(): Int {
        val appInfo = context.applicationInfo
        val isDebuggable = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        // BuildConfig.DEBUG가 false인데 debuggable이면 변조
        return if (isDebuggable && !isDebugBuild()) 10 else 0
    }

    private fun isDebugBuild(): Boolean {
        return try {
            val buildConfigClass = Class.forName("${context.packageName}.BuildConfig")
            val debugField = buildConfigClass.getField("DEBUG")
            debugField.getBoolean(null)
        } catch (_: Exception) {
            false
        }
    }
}
