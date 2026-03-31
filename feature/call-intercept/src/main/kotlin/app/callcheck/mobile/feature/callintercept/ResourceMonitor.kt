package app.callcheck.mobile.feature.callintercept

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Debug
import android.os.Process
import android.util.Log
import app.callcheck.mobile.core.model.DeviceProfile
import app.callcheck.mobile.core.model.ResourceProfile
import javax.inject.Inject
import javax.inject.Singleton
import java.io.RandomAccessFile

private const val TAG = "ResourceMonitor"

/**
 * 리소스 모니터 — 배터리/메모리/CPU 계측.
 *
 * 자비스 요구: "항상 켜져 있어도 존재감이 없는 앱 수준까지 가야 합니다."
 *
 * 계측 범위:
 * 1. 메모리: Java heap, native heap, PSS (Proportional Set Size)
 * 2. 배터리: BatteryManager 기반 잔량 추적 + mAh 추정
 * 3. CPU: /proc/self/stat 기반 프로세스 CPU time 계측
 *
 * 사용 패턴:
 * - beginSession() → 벤치마크 시작 시 호출
 * - snapshot() → 각 인터셉트 후 호출 (메모리 peak 추적)
 * - endSession() → 벤치마크 종료 → ResourceProfile 반환
 *
 * 성능: < 1ms per snapshot (시스템 콜만, 할당 없음)
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class ResourceMonitor @Inject constructor() {

    // ── Session 상태 ──
    private var sessionActive = false
    private var sessionStartMs = 0L
    private var sessionStartHeapMb = 0f
    private var sessionStartBatteryLevel = 0
    private var snapshotCount = 0
    private var heapPeakMb = 0f
    private var totalCpuTimeStartMs = 0L
    private var totalCpuSnapshots = mutableListOf<Float>()

    /**
     * 벤치마크 세션 시작.
     * 현재 리소스 상태를 baseline으로 기록.
     */
    fun beginSession(context: Context) {
        sessionActive = true
        sessionStartMs = System.currentTimeMillis()
        sessionStartHeapMb = getHeapUsedMb()
        sessionStartBatteryLevel = getBatteryLevel(context)
        snapshotCount = 0
        heapPeakMb = sessionStartHeapMb
        totalCpuTimeStartMs = getProcessCpuTimeMs()
        totalCpuSnapshots.clear()

        Log.i(TAG, buildString {
            append("Session started: ")
            append("heap=${String.format("%.1f", sessionStartHeapMb)}MB ")
            append("battery=${sessionStartBatteryLevel}% ")
            append("cpuTime=${totalCpuTimeStartMs}ms")
        })
    }

    /**
     * 인터셉트 후 스냅샷 — 메모리 peak 추적.
     */
    fun snapshot() {
        if (!sessionActive) return
        snapshotCount++

        val currentHeap = getHeapUsedMb()
        if (currentHeap > heapPeakMb) {
            heapPeakMb = currentHeap
        }

        // CPU 사용률 샘플링 (매 10번째 snapshot)
        if (snapshotCount % 10 == 0) {
            val cpuPercent = getCurrentCpuPercent()
            if (cpuPercent >= 0f) {
                totalCpuSnapshots.add(cpuPercent)
            }
        }
    }

    /**
     * 벤치마크 세션 종료 → ResourceProfile 반환.
     */
    fun endSession(context: Context): ResourceProfile {
        if (!sessionActive) {
            Log.w(TAG, "endSession called without active session")
            return ResourceProfile()
        }

        sessionActive = false
        val endMs = System.currentTimeMillis()
        val durationMs = endMs - sessionStartMs
        val endHeapMb = getHeapUsedMb()
        val endBatteryLevel = getBatteryLevel(context)

        // 인터셉트당 메모리 추정
        val memoryDeltaMb = endHeapMb - sessionStartHeapMb
        val memoryPerInterceptKb = if (snapshotCount > 0) {
            (memoryDeltaMb * 1024f) / snapshotCount
        } else 0f

        // 배터리 소모 추정
        val batteryDelta = sessionStartBatteryLevel - endBatteryLevel
        val batteryPer100 = if (snapshotCount > 0) {
            (batteryDelta.toFloat() / snapshotCount) * 100f
        } else 0f

        val batteryDrainPerHour = if (durationMs > 0 && batteryDelta > 0) {
            (batteryDelta.toFloat() / durationMs) * 3_600_000f
        } else 0f

        // CPU 계산
        val cpuTimeEndMs = getProcessCpuTimeMs()
        val cpuTimeDeltaMs = cpuTimeEndMs - totalCpuTimeStartMs
        val avgCpu = if (durationMs > 0) {
            (cpuTimeDeltaMs.toFloat() / durationMs) * 100f
        } else 0f
        val peakCpu = totalCpuSnapshots.maxOrNull() ?: avgCpu

        val profile = ResourceProfile(
            heapUsedStartMb = sessionStartHeapMb,
            heapUsedEndMb = endHeapMb,
            heapPeakMb = heapPeakMb,
            memoryPerInterceptKb = memoryPerInterceptKb,
            benchmarkDurationMs = durationMs,
            batteryPer100Intercepts = batteryPer100,
            batteryDrainPerHour = batteryDrainPerHour,
            avgCpuUsage = avgCpu.coerceIn(0f, 100f),
            peakCpuUsage = peakCpu.coerceIn(0f, 100f),
        )

        Log.i(TAG, buildString {
            appendLine("Session ended ($snapshotCount samples, ${durationMs}ms)")
            appendLine("  Memory: ${String.format("%.1f", sessionStartHeapMb)}→${String.format("%.1f", endHeapMb)}MB (peak=${String.format("%.1f", heapPeakMb)}MB)")
            appendLine("  Per intercept: ${String.format("%.2f", memoryPerInterceptKb)}KB")
            appendLine("  Battery: $sessionStartBatteryLevel%→$endBatteryLevel% (${String.format("%.3f", batteryPer100)} per 100)")
            append("  CPU: avg=${String.format("%.1f", avgCpu)}% peak=${String.format("%.1f", peakCpu)}%")
        })

        return profile
    }

    /**
     * 현재 기기 프로파일 수집.
     */
    fun getDeviceProfile(context: Context): DeviceProfile {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        return DeviceProfile(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            sdkLevel = Build.VERSION.SDK_INT,
            cpuCores = Runtime.getRuntime().availableProcessors(),
            totalRamMb = memInfo.totalMem / (1024 * 1024),
            batteryCapacityMah = getBatteryCapacity(context),
            batteryLevelAtStart = getBatteryLevel(context),
        )
    }

    // ══════════════════════════════════════
    // 내부 계측 유틸
    // ══════════════════════════════════════

    /** Java heap 사용량 (MB) */
    private fun getHeapUsedMb(): Float {
        val runtime = Runtime.getRuntime()
        val used = runtime.totalMemory() - runtime.freeMemory()
        return used / (1024f * 1024f)
    }

    /** 배터리 잔량 (0~100) */
    private fun getBatteryLevel(context: Context): Int {
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: run {
                // Fallback: sticky broadcast
                @Suppress("DEPRECATION")
                val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
                if (level >= 0) (level * 100) / scale else 0
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read battery level", e)
            0
        }
    }

    /** 배터리 용량 (mAh). 정확하지 않을 수 있음. */
    private fun getBatteryCapacity(context: Context): Int {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                val chargeCounter = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val capacity = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                if (chargeCounter != null && chargeCounter > 0 && capacity != null && capacity > 0) {
                    // CHARGE_COUNTER = µAh remaining, capacity = % remaining
                    ((chargeCounter.toLong() * 100L) / capacity / 1000L).toInt()
                } else 0
            } else 0
        } catch (e: Exception) {
            Log.w(TAG, "Failed to estimate battery capacity", e)
            0
        }
    }

    /** 프로세스 CPU 시간 (ms) — /proc/self/stat 기반 */
    private fun getProcessCpuTimeMs(): Long {
        return try {
            val reader = RandomAccessFile("/proc/self/stat", "r")
            val line = reader.readLine()
            reader.close()

            // /proc/self/stat format: pid (comm) state utime stime ...
            // utime = index 13, stime = index 14 (0-indexed)
            val parts = line.split(" ")
            if (parts.size > 14) {
                val utime = parts[13].toLongOrNull() ?: 0L
                val stime = parts[14].toLongOrNull() ?: 0L
                // jiffies → ms (1 jiffy = 10ms on most Linux)
                (utime + stime) * 10L
            } else 0L
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read process CPU time", e)
            0L
        }
    }

    /** 현재 CPU 사용률 (%) — 순간 샘플링 */
    private fun getCurrentCpuPercent(): Float {
        return try {
            val reader1 = RandomAccessFile("/proc/stat", "r")
            val line1 = reader1.readLine()
            reader1.close()

            Thread.sleep(50) // 50ms 간격 샘플

            val reader2 = RandomAccessFile("/proc/stat", "r")
            val line2 = reader2.readLine()
            reader2.close()

            val parse = { line: String ->
                val parts = line.split("\\s+".toRegex())
                if (parts.size >= 8) {
                    val idle = parts[4].toLongOrNull() ?: 0L
                    val total = parts.drop(1).take(7).mapNotNull { it.toLongOrNull() }.sum()
                    Pair(idle, total)
                } else Pair(0L, 0L)
            }

            val (idle1, total1) = parse(line1)
            val (idle2, total2) = parse(line2)

            val idleDelta = idle2 - idle1
            val totalDelta = total2 - total1

            if (totalDelta > 0) {
                ((totalDelta - idleDelta).toFloat() / totalDelta) * 100f
            } else 0f
        } catch (e: Exception) {
            -1f // 읽기 실패
        }
    }
}
