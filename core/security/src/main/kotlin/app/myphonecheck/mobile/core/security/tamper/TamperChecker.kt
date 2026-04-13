package app.myphonecheck.mobile.core.security.tamper

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * 탬퍼 탐지 통합 체커.
 *
 * RootDetector, HookDetector, RepackageDetector를 조합하여
 * TamperSignal 생성. 호출 타이밍 랜덤화로 정적 분석 회피.
 *
 * 올랑방 앱팩토리 공통 모듈.
 */
@Singleton
class TamperChecker @Inject constructor(
    private val rootDetector: RootDetector,
    private val hookDetector: HookDetector,
    private val repackageDetector: RepackageDetector,
) {
    /**
     * 전체 탬퍼 신호 수집.
     *
     * IO 디스패처에서 실행 (파일시스템 접근 포함).
     * 호출 간 랜덤 딜레이로 타이밍 분석 방해.
     */
    suspend fun check(): TamperSignal = withContext(Dispatchers.IO) {
        // 랜덤 딜레이 — 정적 분석/패턴 매칭 방해
        delay(Random.nextLong(50, 300))

        val rootScore = rootDetector.detect()

        delay(Random.nextLong(30, 150))

        val hookScore = hookDetector.detect()

        delay(Random.nextLong(20, 100))

        val repackageScore = repackageDetector.detect()

        val isEmulator = detectEmulator()

        TamperSignal(
            rootScore = rootScore,
            hookScore = hookScore,
            repackageScore = repackageScore,
            isEmulator = isEmulator,
        )
    }

    /**
     * 에뮬레이터 탐지 — 다중 신호.
     */
    private fun detectEmulator(): Boolean {
        var signals = 0

        // Build fingerprint 검사
        val fingerprint = Build.FINGERPRINT.lowercase()
        if (fingerprint.contains("generic") || fingerprint.contains("emulator") ||
            fingerprint.contains("sdk_gphone")
        ) {
            signals++
        }

        // Hardware 검사
        val hardware = Build.HARDWARE.lowercase()
        if (hardware.contains("goldfish") || hardware.contains("ranchu") ||
            hardware == "vbox86"
        ) {
            signals++
        }

        // Model 검사
        val model = Build.MODEL.lowercase()
        if (model.contains("sdk") || model.contains("emulator") ||
            model.contains("android sdk")
        ) {
            signals++
        }

        // Product 검사
        val product = Build.PRODUCT.lowercase()
        if (product.contains("sdk") || product.contains("emulator") ||
            product == "google_sdk"
        ) {
            signals++
        }

        // Manufacturer 검사
        val manufacturer = Build.MANUFACTURER.lowercase()
        if (manufacturer.contains("genymotion") || manufacturer == "unknown") {
            signals++
        }

        // 2개 이상 신호면 에뮬레이터로 판정
        return signals >= 2
    }
}
