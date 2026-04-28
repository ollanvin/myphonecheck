package app.myphonecheck.mobile.core.globalengine.search.publicfeed.download

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.Severity
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedDataType
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedFormat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 raw 본문 → MatchEntry 리스트 파서 (Architecture v2.1.0 §30-4).
 *
 * 단순 포맷만 지원 — 본 PR 범위. 출처별 커스텀 파서는 후속 PR.
 *  - CSV: 첫 컬럼 = key, 둘째 컬럼 = description (있으면).
 *  - JSON_ARRAY: [{"key": "...", "description": "..."}, ...] 또는 [{"url": "..."}, ...].
 *  - 기타 포맷: 빈 리스트 (후속 PR에서 확장).
 */
@Singleton
class FeedParser @Inject constructor() {

    private val gson = Gson()

    fun parse(raw: String, format: FeedFormat, dataType: FeedDataType): List<MatchEntry> = when (format) {
        FeedFormat.CSV -> parseCsv(raw)
        FeedFormat.JSON_ARRAY -> parseJsonArray(raw)
        FeedFormat.JSON_LINES -> parseJsonLines(raw)
        FeedFormat.PLAIN_TEXT -> parsePlainText(raw)
        FeedFormat.RSS, FeedFormat.XML -> emptyList()  // 후속 PR
    }

    private fun parseCsv(raw: String): List<MatchEntry> {
        return raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .mapNotNull { line ->
                val cols = line.split(',').map { it.trim().trim('"') }
                val key = cols.firstOrNull()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
                val description = cols.getOrNull(1).orEmpty()
                MatchEntry(sourceId = key, description = description, severity = null)
            }
            .toList()
    }

    private fun parseJsonArray(raw: String): List<MatchEntry> = runCatching {
        val element = JsonParser.parseString(raw)
        if (!element.isJsonArray) return@runCatching emptyList<MatchEntry>()
        val arr: JsonArray = element.asJsonArray
        arr.mapNotNull { item ->
            if (!item.isJsonObject) return@mapNotNull null
            val obj = item.asJsonObject
            val key = obj.get("key")?.asString
                ?: obj.get("url")?.asString
                ?: obj.get("phone_number")?.asString
                ?: return@mapNotNull null
            val description = obj.get("description")?.asString
                ?: obj.get("target")?.asString
                ?: ""
            val severity = obj.get("severity")?.asString
                ?.let { runCatching { Severity.valueOf(it.uppercase()) }.getOrNull() }
            MatchEntry(sourceId = key, description = description, severity = severity)
        }
    }.getOrDefault(emptyList())

    private fun parseJsonLines(raw: String): List<MatchEntry> = raw.lineSequence()
        .map { it.trim() }
        .filter { it.startsWith("{") }
        .flatMap { line -> parseJsonArray("[$line]").asSequence() }
        .toList()

    private fun parsePlainText(raw: String): List<MatchEntry> = raw.lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .map { MatchEntry(sourceId = it, description = "", severity = null) }
        .toList()
}
