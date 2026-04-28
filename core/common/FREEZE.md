# core/common Contract Freeze Declaration

Freeze Date: 2026-04-22 (Stage 0)
Freeze Version: v1.6.1-contract-frozen

## Frozen Contracts

1. IdentifierType sealed class (3 subclasses)
2. RiskKnowledge interface
3. Checker generic interface
4. DecisionEngineContract interface
5. RiskLevel, DamageEstimate, DamageType, SearchEvidence

## Verification

FreezeMarkerTest validates structure. Failure means contract change (MAJOR review).

## PR policy

Changes under core/common/src/main should include FREEZE-ACK in commit message or PR body.
