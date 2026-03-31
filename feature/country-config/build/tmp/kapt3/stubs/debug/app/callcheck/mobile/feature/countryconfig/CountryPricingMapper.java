package app.callcheck.mobile.feature.countryconfig;

/**
 * 국가별 Pricing Tier 매핑.
 *
 * 분류 기준: World Bank income group + 통신 시장 성숙도.
 * Google Play Console 국가 코드(ISO 3166-1 alpha-2) 기준.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\u0005R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/CountryPricingMapper;", "", "()V", "TIER_1_COUNTRIES", "", "", "TIER_2_COUNTRIES", "getTier", "Lapp/callcheck/mobile/feature/countryconfig/PricingTier;", "countryCode", "country-config_debug"})
public final class CountryPricingMapper {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TIER_1_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TIER_2_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.countryconfig.CountryPricingMapper INSTANCE = null;
    
    private CountryPricingMapper() {
        super();
    }
    
    /**
     * 국가 코드 → PricingTier 반환.
     * 미매핑 국가는 Tier 3 (가장 저렴) 적용.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.PricingTier getTier(@org.jetbrains.annotations.Nullable()
    java.lang.String countryCode) {
        return null;
    }
}