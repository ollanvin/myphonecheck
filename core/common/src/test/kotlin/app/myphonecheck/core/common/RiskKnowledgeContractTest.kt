package app.myphonecheck.core.common

import app.myphonecheck.core.common.identifier.IdentifierType
import app.myphonecheck.core.common.risk.DamageEstimate
import app.myphonecheck.core.common.risk.DamageType
import app.myphonecheck.core.common.risk.RiskKnowledge
import app.myphonecheck.core.common.risk.RiskLevel
import app.myphonecheck.core.common.risk.SearchEvidence
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RiskKnowledgeContractTest {

    private data class FakeRisk(
        override val identifier: IdentifierType,
        override val riskScore: Float,
        override val expectedDamage: DamageEstimate,
        override val damageType: DamageType,
        override val reasoning: String,
        override val analyzedAt: Long,
    ) : RiskKnowledge

    @Test
    fun `riskLevel auto-maps from score`() {
        val risk = FakeRisk(
            identifier = IdentifierType.PhoneNumber("+821012345678"),
            riskScore = 0.9f,
            expectedDamage = DamageEstimate(500_000L, "KRW", 0.8f),
            damageType = DamageType.FINANCIAL_FRAUD,
            reasoning = "scam_pattern_detected",
            analyzedAt = System.currentTimeMillis(),
        )
        risk.riskLevel shouldBe RiskLevel.DANGER
    }

    @Test
    fun `RiskLevel fromScore covers boundaries`() {
        RiskLevel.fromScore(0.0f) shouldBe RiskLevel.SAFE
        RiskLevel.fromScore(0.09f) shouldBe RiskLevel.SAFE
        RiskLevel.fromScore(0.1f) shouldBe RiskLevel.SAFE_UNKNOWN
        RiskLevel.fromScore(0.3f) shouldBe RiskLevel.UNKNOWN
        RiskLevel.fromScore(0.5f) shouldBe RiskLevel.CAUTION
        RiskLevel.fromScore(0.85f) shouldBe RiskLevel.DANGER
        RiskLevel.fromScore(1.0f) shouldBe RiskLevel.DANGER
    }

    @Test
    fun `DamageEstimate UNKNOWN is safe default`() {
        DamageEstimate.UNKNOWN.amountLocal shouldBe 0L
        DamageEstimate.UNKNOWN.currencyCode shouldBe "XXX"
        DamageEstimate.UNKNOWN.confidence shouldBe 0.0f
    }

    @Test
    fun `DamageEstimate rejects negative amount`() {
        shouldThrow<IllegalArgumentException> {
            DamageEstimate(-1L, "KRW", 0.5f)
        }
    }

    @Test
    fun `SearchEvidence stores layer and summary`() {
        val e = SearchEvidence(
            SearchEvidence.Layer.L2_SEARCH,
            "summary_key",
            42L,
        )
        e.source shouldBe SearchEvidence.Layer.L2_SEARCH
        e.summary shouldBe "summary_key"
        e.timestamp shouldBe 42L
    }
}
