package app.myphonecheck.core.common

import app.myphonecheck.core.common.checker.Checker
import app.myphonecheck.core.common.engine.DecisionEngineContract
import app.myphonecheck.core.common.identifier.IdentifierType
import app.myphonecheck.core.common.risk.RiskKnowledge
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * Contract freeze structural tests. Failure implies MAJOR version / explicit review.
 */
class FreezeMarkerTest {

    @Test
    fun `FREEZE IdentifierType subclass count is 3`() {
        val subclasses = IdentifierType::class.sealedSubclasses.size
        subclasses shouldBe 3
    }

    @Test
    fun `FREEZE RiskKnowledge has 4 required properties`() {
        val props = RiskKnowledge::class.members
            .filter {
                it.name in setOf(
                    "identifier",
                    "riskScore",
                    "expectedDamage",
                    "damageType",
                    "reasoning",
                )
            }
        props.size shouldBe 5
    }

    @Test
    fun `FREEZE Checker has exactly one check method`() {
        val checkMethods = Checker::class.members
            .filter { it.name == "check" }
        checkMethods.size shouldBe 1
    }

    @Test
    fun `FREEZE DecisionEngineContract has 3 methods`() {
        val methods = DecisionEngineContract::class.members
            .filter {
                it.name in setOf(
                    "sourceEvidence",
                    "search",
                    "synthesize",
                )
            }
        methods.size shouldBe 3
    }
}
