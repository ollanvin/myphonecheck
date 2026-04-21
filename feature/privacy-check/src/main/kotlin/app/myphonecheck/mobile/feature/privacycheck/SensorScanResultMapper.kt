package app.myphonecheck.mobile.feature.privacycheck

import app.myphonecheck.mobile.data.localcache.entity.SensorScanResultEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * SensorScanResultEntity ↔ SensorCheckState 변환 매퍼.
 *
 * JSON 직렬화: org.json (Android 내장, 외부 의존성 없음).
 * 서버 전송 없음 — Room DB 영속 저장 전용.
 */
object SensorScanResultMapper {

    /** SensorCheckState → SensorScanResultEntity */
    fun toEntity(
        sensorType: String,
        state: SensorCheckState,
    ): SensorScanResultEntity = SensorScanResultEntity(
        sensorType = sensorType,
        grantedAppCount = state.grantedAppCount,
        recentAppCount = state.recentAppCount,
        lastUsedAt = state.lastUsedAt,
        grantedAppsJson = appsToJson(state.grantedApps),
        recentAppsJson = appsToJson(state.recentApps),
        scannedAt = System.currentTimeMillis(),
        statusLevel = state.statusLevel.name,
    )

    /** SensorScanResultEntity → SensorCheckState (SCANNED 상태로 복원) */
    fun toState(entity: SensorScanResultEntity): SensorCheckState = SensorCheckState(
        scanStatus = ScanStatus.SCANNED,
        grantedApps = jsonToApps(entity.grantedAppsJson),
        recentApps = jsonToApps(entity.recentAppsJson),
        lastUsedAt = entity.lastUsedAt,
    )

    private fun appsToJson(apps: List<SensorAppInfo>): String {
        val array = JSONArray()
        for (app in apps) {
            val obj = JSONObject().apply {
                put("pkg", app.packageName)
                put("label", app.appLabel)
                put("safe", app.isKnownSafe)
                put("days", app.daysSinceInstall)
                if (app.lastUsedAt != null) put("lastUsed", app.lastUsedAt)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun jsonToApps(json: String): List<SensorAppInfo> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                SensorAppInfo(
                    packageName = obj.getString("pkg"),
                    appLabel = obj.getString("label"),
                    isKnownSafe = obj.optBoolean("safe", false),
                    daysSinceInstall = obj.optInt("days", 0),
                    lastUsedAt = if (obj.has("lastUsed")) obj.getLong("lastUsed") else null,
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
