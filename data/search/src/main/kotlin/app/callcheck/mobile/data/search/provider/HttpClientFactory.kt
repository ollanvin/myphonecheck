package app.callcheck.mobile.data.search.provider

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 온디바이스 HTTP 클라이언트 팩토리.
 *
 * 모든 검색 요청은 디바이스에서 직접 수행한다.
 * 중앙 서버, 프록시, 외부 API 없음.
 */
fun createOkHttpClient(timeoutSeconds: Long = 3): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
}
