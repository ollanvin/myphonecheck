package app.callcheck.mobile.feature.callintercept;

/**
 * Stage 15 — 스토어 패키지 사양 확정.
 *
 * 자비스 기준:
 * "앱 설명 (글로벌 기준 단일 메시지), 스크린샷 5~8장,
 * 권한 설명 (Call intercept 관련), 개인정보/데이터 처리 문구 고정.
 *
 * 핵심 문장 반드시 포함:
 * '이 앱은 전화를 자동으로 차단하거나 수신하지 않습니다.
 *  사용자의 판단을 돕기 위한 정보만 제공합니다.'"
 *
 * 모든 문구는 상수로 고정. 런타임 변경 불가.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0004,-./B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010%\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\'J\u0010\u0010(\u001a\u00020\u00042\u0006\u0010)\u001a\u00020*H\u0002J\u0006\u0010+\u001a\u00020\'R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u000e\u0010\u001e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0013X\u0086T\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00040\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001aR\u000e\u0010\"\u001a\u00020\u0013X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0013X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec;", "", "()V", "APP_NAME", "", "CATEGORY", "CONTENT_RATING", "DEFAULT_LANGUAGE", "DISTRIBUTION_MODE", "FULL_DESCRIPTION", "MANDATORY_NOTICES", "", "getMANDATORY_NOTICES", "()Ljava/util/Map;", "MANDATORY_NOTICE_EN", "MANDATORY_NOTICE_JA", "MANDATORY_NOTICE_KO", "MANDATORY_NOTICE_ZH", "MIN_SDK", "", "PACKAGE_NAME", "PRICE", "REQUIRED_PERMISSIONS", "", "Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$PermissionSpec;", "getREQUIRED_PERMISSIONS", "()Ljava/util/List;", "SCREENSHOTS", "Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$ScreenshotSpec;", "getSCREENSHOTS", "SHORT_DESCRIPTION", "SUPPORTED_COUNTRY_COUNT", "SUPPORTED_LANGUAGES", "getSUPPORTED_LANGUAGES", "TARGET_SDK", "VERSION_CODE", "VERSION_NAME", "formatReport", "result", "Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$StoreReadinessResult;", "mark", "pass", "", "verifyStoreReadiness", "DataSafety", "PermissionSpec", "ScreenshotSpec", "StoreReadinessResult", "call-intercept_debug"})
public final class StorePackageSpec {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_NAME = "CallCheck";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PACKAGE_NAME = "app.callcheck.mobile";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VERSION_NAME = "1.0.0";
    public static final int VERSION_CODE = 1;
    public static final int MIN_SDK = 26;
    public static final int TARGET_SDK = 34;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CATEGORY = "Communication";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CONTENT_RATING = "Everyone";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PRICE = "$9.99/month";
    
    /**
     * 짧은 설명 (80자 이내) — Play Store 검색 결과에 표시
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SHORT_DESCRIPTION = "Real-time caller information to help you decide before you answer.";
    
    /**
     * 전체 설명 — Play Store 상세 페이지
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FULL_DESCRIPTION = "CallCheck provides real-time information about incoming calls to help you make informed decisions.\n\nWhen a call comes in, CallCheck instantly analyzes the phone number using on-device search technology and presents you with relevant information \u2014 all within 2 seconds.\n\nKEY FEATURES:\n\u2022 Real-time caller analysis in under 2 seconds\n\u2022 Works in 190+ countries with localized search\n\u2022 100% on-device processing \u2014 your data never leaves your phone\n\u2022 No automatic blocking \u2014 you always make the decision\n\u2022 Color-coded risk indicators (Green/Yellow/Red)\n\u2022 Country-specific search engines for maximum accuracy\n\u2022 Works offline with cached results\n\nIMPORTANT: This app does NOT automatically block or answer calls. It only provides information to help your judgment.\n\nPRIVACY FIRST:\n\u2022 Zero data collection \u2014 no server, no cloud\n\u2022 No personal information stored or transmitted\n\u2022 No tracking, no analytics, no ads\n\u2022 All processing happens on your device\n\nSUPPORTED COUNTRIES:\n190+ countries with Tier A coverage for KR, JP, CN, RU, CZ and Tier B/C/D coverage worldwide.\n\nCallCheck \u2014 Know before you answer.";
    
    /**
     * 자비스 필수 포함 문장 — 한국어
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MANDATORY_NOTICE_KO = "\uc774 \uc571\uc740 \uc804\ud654\ub97c \uc790\ub3d9\uc73c\ub85c \ucc28\ub2e8\ud558\uac70\ub098 \uc218\uc2e0\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \uc0ac\uc6a9\uc790\uc758 \ud310\ub2e8\uc744 \ub3d5\uae30 \uc704\ud55c \uc815\ubcf4\ub9cc \uc81c\uacf5\ud569\ub2c8\ub2e4.";
    
    /**
     * 자비스 필수 포함 문장 — 영어
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MANDATORY_NOTICE_EN = "This app does NOT automatically block or answer calls. It only provides information to help your judgment.";
    
    /**
     * 자비스 필수 포함 문장 — 일본어
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MANDATORY_NOTICE_JA = "\u3053\u306e\u30a2\u30d7\u30ea\u306f\u81ea\u52d5\u7684\u306b\u96fb\u8a71\u3092\u30d6\u30ed\u30c3\u30af\u3057\u305f\u308a\u5fdc\u7b54\u3057\u305f\u308a\u3057\u307e\u305b\u3093\u3002\u5224\u65ad\u3092\u52a9\u3051\u308b\u305f\u3081\u306e\u60c5\u5831\u306e\u307f\u3092\u63d0\u4f9b\u3057\u307e\u3059\u3002";
    
    /**
     * 자비스 필수 포함 문장 — 중국어
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String MANDATORY_NOTICE_ZH = "\u672c\u5e94\u7528\u4e0d\u4f1a\u81ea\u52a8\u62e6\u622a\u6216\u63a5\u542c\u7535\u8bdd\u3002\u4ec5\u63d0\u4f9b\u4fe1\u606f\u4ee5\u5e2e\u52a9\u60a8\u505a\u51fa\u5224\u65ad\u3002";
    
    /**
     * 모든 필수 고지 문구 (언어별)
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> MANDATORY_NOTICES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<app.callcheck.mobile.feature.callintercept.StorePackageSpec.PermissionSpec> REQUIRED_PERMISSIONS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<app.callcheck.mobile.feature.callintercept.StorePackageSpec.ScreenshotSpec> SCREENSHOTS = null;
    
    /**
     * 글로벌 배포: 191개국 동시 출시
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DISTRIBUTION_MODE = "GLOBAL_SIMULTANEOUS";
    public static final int SUPPORTED_COUNTRY_COUNT = 191;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_LANGUAGE = "en";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> SUPPORTED_LANGUAGES = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.StorePackageSpec INSTANCE = null;
    
    private StorePackageSpec() {
        super();
    }
    
    /**
     * 모든 필수 고지 문구 (언어별)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> getMANDATORY_NOTICES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.StorePackageSpec.PermissionSpec> getREQUIRED_PERMISSIONS() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.StorePackageSpec.ScreenshotSpec> getSCREENSHOTS() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSUPPORTED_LANGUAGES() {
        return null;
    }
    
    /**
     * 스토어 패키지 준비 상태 검증.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.StorePackageSpec.StoreReadinessResult verifyStoreReadiness() {
        return null;
    }
    
    /**
     * 스토어 준비 보고서 포맷팅.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.StorePackageSpec.StoreReadinessResult result) {
        return null;
    }
    
    private final java.lang.String mark(boolean pass) {
        return null;
    }
    
    /**
     * Data Safety Section — Google Play Console 대응
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$DataSafety;", "", "()V", "DATA_COLLECTED", "", "DATA_DELETION_AVAILABLE", "DATA_ENCRYPTED_IN_TRANSIT", "DATA_SHARED", "PRIVACY_POLICY_SUMMARY", "", "PRIVACY_POLICY_URL", "TERMS_OF_SERVICE_URL", "call-intercept_debug"})
    public static final class DataSafety {
        public static final boolean DATA_COLLECTED = false;
        public static final boolean DATA_SHARED = false;
        public static final boolean DATA_ENCRYPTED_IN_TRANSIT = true;
        public static final boolean DATA_DELETION_AVAILABLE = true;
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String PRIVACY_POLICY_SUMMARY = "CallCheck Privacy Policy Summary:\n\n1. DATA COLLECTION: None.\n   CallCheck does not collect, store, or transmit any personal data.\n\n2. ON-DEVICE PROCESSING: All call analysis is performed entirely on your device.\n   No data is sent to any server.\n\n3. SEARCH QUERIES: Phone number lookups are performed through public search engines\n   (Google, Naver, Baidu, etc.) directly from your device, the same as if you\n   searched manually in a browser.\n\n4. LOCAL STORAGE: Only cached search results and your personal settings are stored\n   locally on your device. This data is never transmitted.\n\n5. NO TRACKING: No analytics, no advertising IDs, no user tracking of any kind.\n\n6. DATA DELETION: Uninstalling the app removes all locally stored data.\n   There is no server-side data to delete because none was ever collected.\n\n7. THIRD PARTIES: CallCheck does not share any information with third parties.\n\nContact: privacy@callcheck.app";
        
        /**
         * 개인정보 처리 방침 URL
         */
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String PRIVACY_POLICY_URL = "https://callcheck.app/privacy";
        
        /**
         * 이용약관 URL
         */
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String TERMS_OF_SERVICE_URL = "https://callcheck.app/terms";
        @org.jetbrains.annotations.NotNull()
        public static final app.callcheck.mobile.feature.callintercept.StorePackageSpec.DataSafety INSTANCE = null;
        
        private DataSafety() {
            super();
        }
    }
    
    /**
     * 필요 권한 목록 + 사유
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0016"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$PermissionSpec;", "", "permission", "", "reason", "required", "", "(Ljava/lang/String;Ljava/lang/String;Z)V", "getPermission", "()Ljava/lang/String;", "getReason", "getRequired", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class PermissionSpec {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String permission = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String reason = null;
        private final boolean required = false;
        
        public PermissionSpec(@org.jetbrains.annotations.NotNull()
        java.lang.String permission, @org.jetbrains.annotations.NotNull()
        java.lang.String reason, boolean required) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPermission() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getReason() {
            return null;
        }
        
        public final boolean getRequired() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final boolean component3() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.StorePackageSpec.PermissionSpec copy(@org.jetbrains.annotations.NotNull()
        java.lang.String permission, @org.jetbrains.annotations.NotNull()
        java.lang.String reason, boolean required) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00c6\u0003J7\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$ScreenshotSpec;", "", "index", "", "filename", "", "description", "requiredElements", "", "(ILjava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getDescription", "()Ljava/lang/String;", "getFilename", "getIndex", "()I", "getRequiredElements", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class ScreenshotSpec {
        private final int index = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String filename = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String description = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> requiredElements = null;
        
        public ScreenshotSpec(int index, @org.jetbrains.annotations.NotNull()
        java.lang.String filename, @org.jetbrains.annotations.NotNull()
        java.lang.String description, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> requiredElements) {
            super();
        }
        
        public final int getIndex() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getFilename() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDescription() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getRequiredElements() {
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
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.StorePackageSpec.ScreenshotSpec copy(int index, @org.jetbrains.annotations.NotNull()
        java.lang.String filename, @org.jetbrains.annotations.NotNull()
        java.lang.String description, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> requiredElements) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001d\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003JY\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u00032\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006$"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/StorePackageSpec$StoreReadinessResult;", "", "mandatoryNoticePresent", "", "shortDescriptionOk", "fullDescriptionOk", "permissionsDocumented", "dataSafetyComplete", "screenshotSpecReady", "allLanguagesCovered", "passed", "(ZZZZZZZZ)V", "getAllLanguagesCovered", "()Z", "getDataSafetyComplete", "getFullDescriptionOk", "getMandatoryNoticePresent", "getPassed", "getPermissionsDocumented", "getScreenshotSpecReady", "getShortDescriptionOk", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class StoreReadinessResult {
        private final boolean mandatoryNoticePresent = false;
        private final boolean shortDescriptionOk = false;
        private final boolean fullDescriptionOk = false;
        private final boolean permissionsDocumented = false;
        private final boolean dataSafetyComplete = false;
        private final boolean screenshotSpecReady = false;
        private final boolean allLanguagesCovered = false;
        private final boolean passed = false;
        
        public StoreReadinessResult(boolean mandatoryNoticePresent, boolean shortDescriptionOk, boolean fullDescriptionOk, boolean permissionsDocumented, boolean dataSafetyComplete, boolean screenshotSpecReady, boolean allLanguagesCovered, boolean passed) {
            super();
        }
        
        public final boolean getMandatoryNoticePresent() {
            return false;
        }
        
        public final boolean getShortDescriptionOk() {
            return false;
        }
        
        public final boolean getFullDescriptionOk() {
            return false;
        }
        
        public final boolean getPermissionsDocumented() {
            return false;
        }
        
        public final boolean getDataSafetyComplete() {
            return false;
        }
        
        public final boolean getScreenshotSpecReady() {
            return false;
        }
        
        public final boolean getAllLanguagesCovered() {
            return false;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
        public final boolean component1() {
            return false;
        }
        
        public final boolean component2() {
            return false;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final boolean component4() {
            return false;
        }
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final boolean component8() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.StorePackageSpec.StoreReadinessResult copy(boolean mandatoryNoticePresent, boolean shortDescriptionOk, boolean fullDescriptionOk, boolean permissionsDocumented, boolean dataSafetyComplete, boolean screenshotSpecReady, boolean allLanguagesCovered, boolean passed) {
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
}