package app.myphonecheck.mobile.core.globalengine.search.publicfeed.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 HTTP 다운로더 (Architecture v2.1.0 §30-4).
 *
 * `java.net.HttpURLConnection` 사용 — 외부 라이브러리 의존 0 (자가 작동 §4 정합).
 * 옵트인된 출처 URL만 호출 — FeedDownloadWorker가 호출 게이트 담당.
 */
@Singleton
class FeedDownloader @Inject constructor() {

    suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.setRequestProperty("Accept", "text/csv, application/json, */*")
            val code = connection.responseCode
            if (code !in 200..299) error("HTTP $code")
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        const val CONNECT_TIMEOUT_MILLIS = 15_000
        const val READ_TIMEOUT_MILLIS = 30_000
        const val USER_AGENT = "MyPhoneCheck-FeedDownloader/1.0"
    }
}
