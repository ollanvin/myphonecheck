package app.myphonecheck.mobile.core.globalengine.search.publicfeed.download

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * 공개 피드 다운로드 워커 (Architecture v2.1.0 §30-4).
 *
 * 옵트인된 출처만 다운로드 → 디바이스 캐시 갱신.
 * placeholder URL("<...>") 출처는 자동 skip — 라이선스·robots.txt 검토 후 별도 PR에서 활성화.
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - optedInIds 미포함 출처는 fetch 0.
 *  - placeholder URL 출처는 fetch 0.
 *  - 옵트인 + 실 URL 출처에 한해 다운로드 후 디바이스 캐싱.
 *
 * WorkManager schedule 등록(주기별)은 후속 PR (Application onCreate 통합 시점).
 */
@HiltWorker
class FeedDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val registry: FeedRegistry,
    private val optInProvider: FeedOptInProvider,
    private val downloader: FeedDownloader,
    private val parser: FeedParser,
    private val cache: PublicFeedCache,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val optedIn = optInProvider.optedInIds()
        if (optedIn.isEmpty()) return Result.success()

        var anySuccess = false
        for (id in optedIn) {
            val source = registry.byId(id) ?: continue
            if (registry.isPlaceholder(source)) continue  // 라이선스 미검토 출처 skip

            try {
                val raw = downloader.fetch(source.downloadUrl)
                val entries = parser.parse(raw, source.format, source.dataType)
                entries.forEach { entry ->
                    cache.put(source.id, entry.sourceId, listOf(entry))
                }
                anySuccess = true
            } catch (e: Exception) {
                // 실패 기록만 — 다음 주기에서 재시도. 단일 출처 실패가 워커 전체를 중단시키지 않음.
            }
        }
        return if (anySuccess) Result.success() else Result.success()
    }

    companion object {
        const val WORK_NAME = "feed_download"
    }
}
