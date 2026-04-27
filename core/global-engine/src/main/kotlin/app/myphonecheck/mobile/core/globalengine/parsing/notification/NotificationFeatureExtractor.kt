package app.myphonecheck.mobile.core.globalengine.parsing.notification

import android.app.Notification
import app.myphonecheck.mobile.core.globalengine.parsing.currency.CurrencyAmountParser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 알림 본문 특성 추출 — CardCheck 협업 hook (v2.0.0 §30).
 *
 * 코어 CurrencyAmountParser 의존 — 자체 정규식 0 (헌법 §8조).
 *
 * JVM 호환 raw overload:
 *  - extractFeatures(Notification): Android 인스트루먼트 환경.
 *  - extractFromText(title, text): JVM 가능.
 */
@Singleton
class NotificationFeatureExtractor @Inject constructor(
    private val currencyParser: CurrencyAmountParser,
) {

    fun extractFeatures(notification: Notification): NotificationFeatures {
        val title = notification.extras?.getString(Notification.EXTRA_TITLE).orEmpty()
        val text = notification.extras?.getString(Notification.EXTRA_TEXT).orEmpty()
        return extractFromText(title, text)
    }

    fun extractFromText(title: String, text: String): NotificationFeatures {
        val hasCurrency = text.isNotEmpty() && currencyParser.extractCurrencyAmount(text) != null
        return NotificationFeatures(
            title = title,
            text = text,
            hasCurrencyPattern = hasCurrency,
            bodyLength = text.length,
        )
    }
}
