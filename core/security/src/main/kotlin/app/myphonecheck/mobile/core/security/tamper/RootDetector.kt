package app.myphonecheck.mobile.core.security.tamper

import android.os.Build
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 루팅 탐지 — 다중 신호 점수 기반.
 *
 * 단일 체크가 아닌 여러 독립 신호를 누적하여
 * 오탐(false positive)을 최소화하면서 탐지율을 높인다.
 *
 * 올랑방 앱팩토리 공통 — 전체 앱에서 재사용.
 */
@Singleton
class RootDetector @Inject constructor() {

    /** 루팅 점수 반환 (0-100) */
    fun detect(): Int {
        var score = 0

        score += checkSuBinary()
        score += checkBuildTags()
        score += checkRootManagementApps()
        score += checkDangerousProps()
        score += checkRWSystem()

        return score.coerceIn(0, 100)
    }

    /** su 바이너리 다중 경로 검사 (가중치: 30) */
    private fun checkSuBinary(): Int {
        val paths = arrayOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su",
            "/system/su", "/data/local/su", "/data/local/bin/su",
            "/data/local/xbin/su", "/su/bin/su",
            "/system/app/Superuser.apk",
        )
        var found = 0
        for (path in paths) {
            if (File(path).exists()) found++
        }
        return when {
            found >= 3 -> 30
            found >= 1 -> 20
            else -> 0
        }
    }

    /** Build 태그 검사 — test-keys는 커스텀 ROM 신호 (가중치: 15) */
    private fun checkBuildTags(): Int {
        val tags = Build.TAGS ?: return 0
        return if (tags.contains("test-keys")) 15 else 0
    }

    /** 루트 관리 앱 패키지명 검사 (가중치: 25) */
    private fun checkRootManagementApps(): Int {
        val packages = arrayOf(
            "com.topjohnwu.magisk",
            "com.koushikdutta.superuser",
            "com.noshufou.android.su",
            "eu.chainfire.supersu",
            "com.thirdparty.superuser",
            "me.phh.superuser",
            // Magisk Manager의 랜덤 패키지명은 탐지 불가하므로
            // 추가 신호(su binary, props)와 조합하여 판단
        )
        var found = 0
        for (pkg in packages) {
            val pkgDir = File("/data/data/$pkg")
            if (pkgDir.exists()) found++
        }
        return when {
            found >= 2 -> 25
            found >= 1 -> 15
            else -> 0
        }
    }

    /** 위험 시스템 프로퍼티 검사 (가중치: 15) */
    private fun checkDangerousProps(): Int {
        var score = 0
        try {
            val process = Runtime.getRuntime().exec("getprop")
            val output = process.inputStream.bufferedReader().readText()
            if (output.contains("ro.debuggable=1") ||
                output.contains("[ro.debuggable]: [1]")
            ) {
                score += 8
            }
            if (output.contains("ro.secure=0") ||
                output.contains("[ro.secure]: [0]")
            ) {
                score += 7
            }
        } catch (_: Exception) {
            // getprop 실패 — 점수 가산 없음
        }
        return score
    }

    /** 시스템 파티션 쓰기 가능 여부 검사 (가중치: 15) */
    private fun checkRWSystem(): Int {
        try {
            val mountFile = File("/proc/mounts")
            if (!mountFile.exists()) return 0
            val lines = mountFile.readLines()
            for (line in lines) {
                val parts = line.split(" ")
                if (parts.size >= 4 && parts[1] == "/system") {
                    if (parts[3].contains("rw")) return 15
                }
            }
        } catch (_: Exception) {
            // 무시
        }
        return 0
    }
}
