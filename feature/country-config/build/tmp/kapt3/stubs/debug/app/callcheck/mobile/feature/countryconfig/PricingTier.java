package app.callcheck.mobile.feature.countryconfig;

/**
 * CallCheck 프리미엄 가격 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 3-Tier 가격 구조 (월간 기준, USD 환산)                        │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Tier 1: $9.99/월  — 고소득 선진국                            │
 * │ Tier 2: $6.99/월  — 중소득 국가                              │
 * │ Tier 3: $3.99/월  — 저소득 국가                              │
 * │                                                              │
 * │ 연간 구독: 월간 × 10 (2개월 무료)                            │
 * │ Tier 1: $99.99/년, Tier 2: $69.99/년, Tier 3: $39.99/년    │
 * │                                                              │
 * │ 무료 체험:                                                    │
 * │ Tier 1: 7일, Tier 2: 5일, Tier 3: 3일                      │
 * │                                                              │
 * │ Google Play Console:                                          │
 * │ - base-plan ID: monthly / yearly                             │
 * │ - offer ID: free-trial-t1 / free-trial-t2 / free-trial-t3  │
 * │ - 국가별 가격은 Play Console에서 "국가별 가격 설정" 사용      │
 * └──────────────────────────────────────────────────────────────┘
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u0000 \"2\u00020\u0001:\u0001\"B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003JO\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020\u0003H\u00d6\u0001J\t\u0010!\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000f\u00a8\u0006#"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/PricingTier;", "", "tierId", "", "monthlyPriceUsd", "", "yearlyPriceUsd", "freeTrialDays", "playBasePlanMonthly", "playBasePlanYearly", "playOfferIdTrial", "(ILjava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getFreeTrialDays", "()I", "getMonthlyPriceUsd", "()Ljava/lang/String;", "getPlayBasePlanMonthly", "getPlayBasePlanYearly", "getPlayOfferIdTrial", "getTierId", "getYearlyPriceUsd", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "Companion", "country-config_debug"})
public final class PricingTier {
    private final int tierId = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String monthlyPriceUsd = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String yearlyPriceUsd = null;
    private final int freeTrialDays = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String playBasePlanMonthly = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String playBasePlanYearly = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String playOfferIdTrial = null;
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.feature.countryconfig.PricingTier TIER_1 = null;
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.feature.countryconfig.PricingTier TIER_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.feature.countryconfig.PricingTier TIER_3 = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.countryconfig.PricingTier.Companion Companion = null;
    
    public PricingTier(int tierId, @org.jetbrains.annotations.NotNull()
    java.lang.String monthlyPriceUsd, @org.jetbrains.annotations.NotNull()
    java.lang.String yearlyPriceUsd, int freeTrialDays, @org.jetbrains.annotations.NotNull()
    java.lang.String playBasePlanMonthly, @org.jetbrains.annotations.NotNull()
    java.lang.String playBasePlanYearly, @org.jetbrains.annotations.NotNull()
    java.lang.String playOfferIdTrial) {
        super();
    }
    
    public final int getTierId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMonthlyPriceUsd() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getYearlyPriceUsd() {
        return null;
    }
    
    public final int getFreeTrialDays() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPlayBasePlanMonthly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPlayBasePlanYearly() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPlayOfferIdTrial() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.PricingTier copy(int tierId, @org.jetbrains.annotations.NotNull()
    java.lang.String monthlyPriceUsd, @org.jetbrains.annotations.NotNull()
    java.lang.String yearlyPriceUsd, int freeTrialDays, @org.jetbrains.annotations.NotNull()
    java.lang.String playBasePlanMonthly, @org.jetbrains.annotations.NotNull()
    java.lang.String playBasePlanYearly, @org.jetbrains.annotations.NotNull()
    java.lang.String playOfferIdTrial) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/PricingTier$Companion;", "", "()V", "TIER_1", "Lapp/callcheck/mobile/feature/countryconfig/PricingTier;", "getTIER_1", "()Lapp/callcheck/mobile/feature/countryconfig/PricingTier;", "TIER_2", "getTIER_2", "TIER_3", "getTIER_3", "country-config_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.countryconfig.PricingTier getTIER_1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.countryconfig.PricingTier getTIER_2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.countryconfig.PricingTier getTIER_3() {
            return null;
        }
    }
}