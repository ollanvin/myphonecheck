package app.myphonecheck.core.common

import app.myphonecheck.core.common.risk.RiskTier
import app.myphonecheck.core.common.risk.TierMapping
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TierMappingTest {

    @Test
    fun `score 0_6 returns Danger`() {
        TierMapping.from(0.6f, 1.0f) shouldBe RiskTier.Danger
    }

    @Test
    fun `score 0_5 returns Danger`() {
        TierMapping.from(0.5f, 1.0f) shouldBe RiskTier.Danger
    }

    @Test
    fun `score 0_3 returns Caution`() {
        TierMapping.from(0.3f, 1.0f) shouldBe RiskTier.Caution
    }

    @Test
    fun `score 0_2 returns Caution`() {
        TierMapping.from(0.2f, 1.0f) shouldBe RiskTier.Caution
    }

    @Test
    fun `score 0_1 with low confidence returns Unknown`() {
        TierMapping.from(0.1f, 0.3f) shouldBe RiskTier.Unknown
    }

    @Test
    fun `score 0_1 with high confidence returns Caution (uncertain ambient)`() {
        TierMapping.from(0.1f, 0.5f) shouldBe RiskTier.Caution
    }

    @Test
    fun `score zero with very low confidence returns Unknown`() {
        TierMapping.from(0.0f, 0.0f) shouldBe RiskTier.Unknown
    }

    @Test
    fun `score minus 0_3 returns Safe`() {
        TierMapping.from(-0.3f, 1.0f) shouldBe RiskTier.Safe
    }

    @Test
    fun `score minus 0_2 returns Safe`() {
        TierMapping.from(-0.2f, 1.0f) shouldBe RiskTier.Safe
    }
}
