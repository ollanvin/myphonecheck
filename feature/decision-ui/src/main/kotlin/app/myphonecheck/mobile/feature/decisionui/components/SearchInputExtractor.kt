package app.myphonecheck.mobile.feature.decisionui.components

import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext

/**
 * 본문 파싱 helper — MessageCheck / PushCheck 등 다중 SearchInput 후보 추출.
 *
 * v2.5.0 §direct-search 정합: 본문에 URL / 폰 번호 발견 시 SearchInput 후보로 추가.
 * 실 PhoneNumberParser 통합은 Stage 4 영역 — 본 helper는 정규식 minimal 추출.
 */
object SearchInputExtractor {

    /**
     * 메시지 본문 → 다중 SearchInput 후보.
     * 1순위: 발신 번호 (있으면)
     * 2순위: 본문 URL
     * 3순위: 본문 폰 번호 (발신과 다른 경우)
     */
    fun fromMessage(
        senderPhoneNumber: String?,
        body: String,
        simContext: SimContext,
        surfaceContextLabel: String,
    ): List<SearchInput> {
        val candidates = mutableListOf<SearchInput>()

        senderPhoneNumber?.takeIf { it.isNotBlank() }?.let {
            candidates.add(SearchInput.PhoneNumber(it, simContext))
        }

        extractUrls(body).forEach { url ->
            candidates.add(SearchInput.Url(url, surfaceContextLabel))
        }

        extractPhoneNumbers(body)
            .filter { it != senderPhoneNumber }
            .forEach { num ->
                candidates.add(SearchInput.PhoneNumber(num, simContext))
            }

        // 최소 1개 보장 — body 만 있으면 MessageBody fallback
        if (candidates.isEmpty() && body.isNotBlank()) {
            candidates.add(SearchInput.MessageBody(body, emptyList(), emptyList()))
        }

        return candidates
    }

    /**
     * Push 알림 본문 → 다중 SearchInput 후보.
     * 1순위: 앱 패키지명
     * 2순위: 본문 URL
     * 3순위: 본문 폰 번호
     */
    fun fromPushNotification(
        packageName: String,
        body: String,
        simContext: SimContext,
    ): List<SearchInput> {
        val candidates = mutableListOf<SearchInput>(SearchInput.AppPackage(packageName))

        extractUrls(body).forEach { url ->
            candidates.add(SearchInput.Url(url, "PUSH"))
        }

        extractPhoneNumbers(body).forEach { num ->
            candidates.add(SearchInput.PhoneNumber(num, simContext))
        }

        return candidates
    }

    private val URL_REGEX = Regex("""https?://[\w.-]+(?:/\S*)?""")
    private val PHONE_REGEX = Regex("""\+?\d[\d\s\-]{6,15}\d""")

    fun extractUrls(text: String): List<String> =
        URL_REGEX.findAll(text).map { it.value }.toList()

    fun extractPhoneNumbers(text: String): List<String> =
        PHONE_REGEX.findAll(text).map { it.value.replace(Regex("[\\s\\-]"), "") }.distinct().toList()
}
