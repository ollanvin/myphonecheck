package app.callcheck.mobile.data.sms;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 \u00142\u00020\u0001:\u0002\u0014\u0015B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0096@\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nH\u0002J\u001a\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lapp/callcheck/mobile/data/sms/SmsMetadataDataSourceImpl;", "Lapp/callcheck/mobile/data/sms/SmsMetadataDataSource;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "contentResolver", "Landroid/content/ContentResolver;", "getSmsMetadata", "Lapp/callcheck/mobile/data/sms/SmsMetadata;", "normalizedNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "matchPhoneNumbers", "", "storedNumber", "querySmsFromBox", "Lapp/callcheck/mobile/data/sms/SmsMetadataDataSourceImpl$SmsBoxResult;", "boxType", "", "querySmsMetadata", "Companion", "SmsBoxResult", "sms_debug"})
public final class SmsMetadataDataSourceImpl implements app.callcheck.mobile.data.sms.SmsMetadataDataSource {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.ContentResolver contentResolver = null;
    private static final int SMS_TYPE_INBOX = 1;
    private static final int SMS_TYPE_SENT = 2;
    private static final int SMS_TYPE_DRAFT = 3;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.data.sms.SmsMetadataDataSourceImpl.Companion Companion = null;
    
    public SmsMetadataDataSourceImpl(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getSmsMetadata(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.sms.SmsMetadata> $completion) {
        return null;
    }
    
    private final app.callcheck.mobile.data.sms.SmsMetadata querySmsMetadata(java.lang.String normalizedNumber) {
        return null;
    }
    
    private final app.callcheck.mobile.data.sms.SmsMetadataDataSourceImpl.SmsBoxResult querySmsFromBox(java.lang.String normalizedNumber, int boxType) {
        return null;
    }
    
    private final boolean matchPhoneNumbers(java.lang.String normalizedNumber, java.lang.String storedNumber) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lapp/callcheck/mobile/data/sms/SmsMetadataDataSourceImpl$Companion;", "", "()V", "SMS_TYPE_DRAFT", "", "SMS_TYPE_INBOX", "SMS_TYPE_SENT", "sms_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0014\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u0014\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J8\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lapp/callcheck/mobile/data/sms/SmsMetadataDataSourceImpl$SmsBoxResult;", "", "count", "", "lastTime", "", "hasInc", "", "hasOut", "(ILjava/lang/Long;ZZ)V", "getCount", "()I", "getHasInc", "()Z", "getHasOut", "getLastTime", "()Ljava/lang/Long;", "Ljava/lang/Long;", "component1", "component2", "component3", "component4", "copy", "(ILjava/lang/Long;ZZ)Lapp/callcheck/mobile/data/sms/SmsMetadataDataSourceImpl$SmsBoxResult;", "equals", "other", "hashCode", "toString", "", "sms_debug"})
    static final class SmsBoxResult {
        private final int count = 0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Long lastTime = null;
        private final boolean hasInc = false;
        private final boolean hasOut = false;
        
        public SmsBoxResult(int count, @org.jetbrains.annotations.Nullable()
        java.lang.Long lastTime, boolean hasInc, boolean hasOut) {
            super();
        }
        
        public final int getCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long getLastTime() {
            return null;
        }
        
        public final boolean getHasInc() {
            return false;
        }
        
        public final boolean getHasOut() {
            return false;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long component2() {
            return null;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.data.sms.SmsMetadataDataSourceImpl.SmsBoxResult copy(int count, @org.jetbrains.annotations.Nullable()
        java.lang.Long lastTime, boolean hasInc, boolean hasOut) {
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