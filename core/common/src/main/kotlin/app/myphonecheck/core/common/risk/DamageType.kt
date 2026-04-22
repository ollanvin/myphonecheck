package app.myphonecheck.core.common.risk

/**
 * Damage category shared across four surfaces. FREEZE: new enum values = MINOR.
 */
enum class DamageType {
    FINANCIAL_FRAUD,
    IDENTITY_THEFT,
    PRIVACY_LEAK,
    SOCIAL_ENGINEERING,
    MALWARE_DELIVERY,
    HARASSMENT,
    UNKNOWN,
}
