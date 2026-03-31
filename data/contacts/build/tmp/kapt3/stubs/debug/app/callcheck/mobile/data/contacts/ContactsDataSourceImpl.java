package app.callcheck.mobile.data.contacts;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\tH\u0002J\u0018\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\n\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0012\u0010\u0011\u001a\u00020\u00102\b\u0010\u0012\u001a\u0004\u0018\u00010\tH\u0002J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\f2\u0006\u0010\n\u001a\u00020\tH\u0002J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tH\u0002J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\tH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lapp/callcheck/mobile/data/contacts/ContactsDataSourceImpl;", "Lapp/callcheck/mobile/data/contacts/ContactsDataSource;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "contentResolver", "Landroid/content/ContentResolver;", "generateNumberVariants", "", "", "normalizedNumber", "getContactInfo", "Lapp/callcheck/mobile/data/contacts/ContactInfo;", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getContactName", "isContactSaved", "", "isFavoriteContact", "contactId", "queryContactInfo", "queryContactName", "queryPhoneLookup", "Landroid/database/Cursor;", "phoneNumber", "contacts_debug"})
public final class ContactsDataSourceImpl implements app.callcheck.mobile.data.contacts.ContactsDataSource {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.ContentResolver contentResolver = null;
    
    public ContactsDataSourceImpl(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isContactSaved(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getContactName(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getContactInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.contacts.ContactInfo> $completion) {
        return null;
    }
    
    private final java.lang.String queryContactName(java.lang.String normalizedNumber) {
        return null;
    }
    
    private final app.callcheck.mobile.data.contacts.ContactInfo queryContactInfo(java.lang.String normalizedNumber) {
        return null;
    }
    
    private final android.database.Cursor queryPhoneLookup(java.lang.String phoneNumber) {
        return null;
    }
    
    private final boolean isFavoriteContact(java.lang.String contactId) {
        return false;
    }
    
    private final java.util.List<java.lang.String> generateNumberVariants(java.lang.String normalizedNumber) {
        return null;
    }
}