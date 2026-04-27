package app.myphonecheck.mobile.feature.cardcheck

import app.myphonecheck.mobile.feature.cardcheck.parser.CardParseResult
import app.myphonecheck.mobile.feature.cardcheck.parser.Confidence
import app.myphonecheck.mobile.feature.cardcheck.parser.CurrencyAmount
import app.myphonecheck.mobile.feature.cardcheck.parser.TransactionSource
import app.myphonecheck.mobile.feature.cardcheck.parser.Validator
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidatorTest {

    private val validator = Validator()
    private val sampleAmount = CurrencyAmount("USD", 2_550L)

    @Test
    fun `HIGH confidence - all 4 fields extracted`() {
        val parsed = CardParseResult(
            sourceId = "VISA",
            amount = sampleAmount,
            timestamp = 1_700_000_000_000L,
            cardIdentifier = "1234",
            merchant = "Starbucks",
            source = TransactionSource.SMS,
        )
        assertEquals(Confidence.HIGH, validator.validate(parsed))
    }

    @Test
    fun `MEDIUM confidence - amount and 2 aux fields`() {
        val parsed = CardParseResult(
            sourceId = "VISA",
            amount = sampleAmount,
            timestamp = 1_700_000_000_000L,
            cardIdentifier = "1234",
            merchant = null,
            source = TransactionSource.SMS,
        )
        assertEquals(Confidence.MEDIUM, validator.validate(parsed))
    }

    @Test
    fun `MEDIUM confidence - amount and 1 aux field`() {
        val parsed = CardParseResult(
            sourceId = "VISA",
            amount = sampleAmount,
            timestamp = 1_700_000_000_000L,
            cardIdentifier = null,
            merchant = null,
            source = TransactionSource.SMS,
        )
        assertEquals(Confidence.MEDIUM, validator.validate(parsed))
    }

    @Test
    fun `LOW confidence - amount only without aux fields`() {
        val parsed = CardParseResult(
            sourceId = "VISA",
            amount = sampleAmount,
            timestamp = null,
            cardIdentifier = null,
            merchant = null,
            source = TransactionSource.SMS,
        )
        assertEquals(Confidence.LOW, validator.validate(parsed))
    }

    @Test
    fun `LOW confidence - no amount`() {
        val parsed = CardParseResult(
            sourceId = "VISA",
            amount = null,
            timestamp = 1_700_000_000_000L,
            cardIdentifier = "1234",
            merchant = "Starbucks",
            source = TransactionSource.SMS,
        )
        assertEquals(Confidence.LOW, validator.validate(parsed))
    }
}
