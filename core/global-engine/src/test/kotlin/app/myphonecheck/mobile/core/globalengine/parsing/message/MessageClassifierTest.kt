package app.myphonecheck.mobile.core.globalengine.parsing.message

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageClassifierTest {

    private val classifier = MessageClassifier()

    private fun features(
        isShortSender: Boolean = false,
        hasUrl: Boolean = false,
        hasCurrencyPattern: Boolean = false,
        bodyLength: Int = 50,
        urlCount: Int = 0,
    ) = MessageFeatures(
        sender = "x",
        isShortSender = isShortSender,
        hasUrl = hasUrl,
        hasCurrencyPattern = hasCurrencyPattern,
        bodyLength = bodyLength,
        urlCount = urlCount,
        countryHint = "ZZ",
    )

    @Test
    fun `Short sender plus currency = PAYMENT_CANDIDATE`() {
        val f = features(isShortSender = true, hasCurrencyPattern = true)
        assertEquals(MessageCategory.PAYMENT_CANDIDATE, classifier.classify(f))
    }

    @Test
    fun `URL plus short body = SPAM_CANDIDATE`() {
        val f = features(hasUrl = true, urlCount = 1, bodyLength = 80)
        assertEquals(MessageCategory.SPAM_CANDIDATE, classifier.classify(f))
    }

    @Test
    fun `Two URLs = SPAM_CANDIDATE regardless of body length`() {
        val f = features(hasUrl = true, urlCount = 2, bodyLength = 500)
        assertEquals(MessageCategory.SPAM_CANDIDATE, classifier.classify(f))
    }

    @Test
    fun `Short sender alone = NOTIFICATION`() {
        val f = features(isShortSender = true)
        assertEquals(MessageCategory.NOTIFICATION, classifier.classify(f))
    }

    @Test
    fun `Plain text = NORMAL`() {
        val f = features()
        assertEquals(MessageCategory.NORMAL, classifier.classify(f))
    }

    @Test
    fun `Long body with single URL = NORMAL (not spam)`() {
        val f = features(hasUrl = true, urlCount = 1, bodyLength = 500)
        assertEquals(MessageCategory.NORMAL, classifier.classify(f))
    }
}
