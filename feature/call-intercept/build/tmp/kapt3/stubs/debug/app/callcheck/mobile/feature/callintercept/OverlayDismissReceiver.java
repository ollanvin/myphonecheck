package app.callcheck.mobile.feature.callintercept;

/**
 * 전화 상태 변경 시 오버레이를 자동 제거한다.
 *
 * - IDLE (전화 종료) → 오버레이 dismiss
 * - OFFHOOK (통화 중 = 사용자가 받음) → 오버레이 dismiss
 *
 * AndroidManifest에 등록:
 * <receiver android:name=".feature.callintercept.OverlayDismissReceiver"
 *          android:exported="false">
 *    <intent-filter>
 *        <action android:name="android.intent.action.PHONE_STATE" />
 *    </intent-filter>
 * </receiver>
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OverlayDismissReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "DismissReceiverEntryPoint", "call-intercept_debug"})
public final class OverlayDismissReceiver extends android.content.BroadcastReceiver {
    
    public OverlayDismissReceiver() {
        super();
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&\u00a8\u0006\u0004"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OverlayDismissReceiver$DismissReceiverEntryPoint;", "", "callerIdOverlayManager", "Lapp/callcheck/mobile/feature/callintercept/CallerIdOverlayManager;", "call-intercept_debug"})
    @dagger.hilt.EntryPoint()
    @dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
    public static abstract interface DismissReceiverEntryPoint {
        
        @org.jetbrains.annotations.NotNull()
        public abstract app.callcheck.mobile.feature.callintercept.CallerIdOverlayManager callerIdOverlayManager();
    }
}