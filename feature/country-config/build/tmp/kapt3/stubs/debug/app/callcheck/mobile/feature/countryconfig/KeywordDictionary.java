package app.callcheck.mobile.feature.countryconfig;

/**
 * Keyword dictionary for categorizing incoming calls.
 *
 * Contains sets of keywords that indicate different call types.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bg\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u000bJ\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003Jy\u0010\u001b\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\u0004H\u00d6\u0001R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r\u00a8\u0006\""}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/KeywordDictionary;", "", "delivery", "", "", "hospital", "institution", "business", "financeSpam", "scam", "telemarketing", "(Ljava/util/Set;Ljava/util/Set;Ljava/util/Set;Ljava/util/Set;Ljava/util/Set;Ljava/util/Set;Ljava/util/Set;)V", "getBusiness", "()Ljava/util/Set;", "getDelivery", "getFinanceSpam", "getHospital", "getInstitution", "getScam", "getTelemarketing", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "country-config_debug"})
public final class KeywordDictionary {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> delivery = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> hospital = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> institution = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> business = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> financeSpam = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> scam = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> telemarketing = null;
    
    public KeywordDictionary(@org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> delivery, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> hospital, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> institution, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> business, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> financeSpam, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> scam, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> telemarketing) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getDelivery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getHospital() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getInstitution() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getBusiness() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getFinanceSpam() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getScam() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getTelemarketing() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.KeywordDictionary copy(@org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> delivery, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> hospital, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> institution, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> business, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> financeSpam, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> scam, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> telemarketing) {
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
}