package app.myphonecheck.mobile.core.security.tamper

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 후킹 프레임워크 탐지 — Frida, Xposed, LSPosed 등.
 *
 * 다중 독립 신호를 점수로 누적.
 * 올랑방 앱팩토리 공통 모듈.
 */
@Singleton
class HookDetector @Inject constructor() {

    /** 후킹 탐지 점수 반환 (0-100) */
    fun detect(): Int {
        var score = 0

        score += checkFridaPort()
        score += checkFridaLibrary()
        score += checkXposedFramework()
        score += checkHookingPackages()
        score += checkStackTrace()

        return score.coerceIn(0, 100)
    }

    /** Frida 기본 포트 탐지 (가중치: 25) */
    private fun checkFridaPort(): Int {
        val ports = intArrayOf(27042, 27043)
        for (port in ports) {
            try {
                val socket = Socket("127.0.0.1", port)
                socket.close()
                return 25
            } catch (_: Exception) {
                // 연결 실패 = 정상
            }
        }
        return 0
    }

    /** /proc/self/maps에서 Frida 라이브러리 탐지 (가중치: 30) */
    private fun checkFridaLibrary(): Int {
        try {
            val maps = File("/proc/self/maps")
            if (!maps.exists()) return 0
            val reader = BufferedReader(FileReader(maps))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line ?: continue
                if (l.contains("frida") || l.contains("gadget")) {
                    reader.close()
                    return 30
                }
            }
            reader.close()
        } catch (_: Exception) {
            // 무시
        }
        return 0
    }

    /** Xposed Framework 클래스 로딩 탐지 (가중치: 25) */
    private fun checkXposedFramework(): Int {
        val xposedClasses = arrayOf(
            "de.robv.android.xposed.XposedBridge",
            "de.robv.android.xposed.XC_MethodHook",
            "de.robv.android.xposed.XposedHelpers",
        )
        for (className in xposedClasses) {
            try {
                Class.forName(className)
                return 25
            } catch (_: ClassNotFoundException) {
                // 정상 — 클래스 없음
            }
        }
        return 0
    }

    /** 후킹 관련 패키지 탐지 (가중치: 10) */
    private fun checkHookingPackages(): Int {
        val packages = arrayOf(
            "de.robv.android.xposed.installer",
            "org.lsposed.manager",
            "com.saurik.substrate",
            "com.topjohnwu.magisk", // Zygisk 모듈
        )
        for (pkg in packages) {
            if (File("/data/data/$pkg").exists()) return 10
        }
        return 0
    }

    /** 스택 트레이스에서 후킹 흔적 탐지 (가중치: 10) */
    private fun checkStackTrace(): Int {
        val stackTrace = Thread.currentThread().stackTrace
        val suspiciousPatterns = arrayOf(
            "xposed", "substrate", "frida", "lsposed",
        )
        for (element in stackTrace) {
            val name = element.className.lowercase() + element.methodName.lowercase()
            for (pattern in suspiciousPatterns) {
                if (name.contains(pattern)) return 10
            }
        }
        return 0
    }
}
