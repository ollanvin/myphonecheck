# ================================================================
# MyPhoneCheck ProGuard Rules — Security Hardened
# 올랑방 앱팩토리 공통 보안 난독화 정책
# ================================================================

# ---- 난독화 강화 ----
# 패키지 계층 평탄화 — 역공학 시 구조 파악 방해
-repackageclasses 'o'
-allowaccessmodification
-overloadaggressively

# ---- Release 빌드 로그 제거 ----
# 민감 정보 노출 방지 — Log.d/v/i 완전 제거
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
}

# ---- 보안 모듈 보호 ----
# 탬퍼 탐지 로직은 난독화하되 클래스 구조 유지
-keep class app.myphonecheck.mobile.core.security.tamper.TamperSignal { *; }

# ---- 모델 클래스 보호 ----
-keep class app.myphonecheck.mobile.core.model.** { *; }

# ---- Hilt DI ----
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# ---- Room Database ----
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }
-keep class app.myphonecheck.mobile.data.localcache.entity.** { *; }
-keep class app.myphonecheck.mobile.data.localcache.dao.** { *; }

# ---- SQLCipher (sqlcipher-android 4.6.1 — 공식 현행 라이브러리) ----
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.database.**

# ---- Navigation ----
-keepclasseswithmembers class * {
    @androidx.navigation.** <fields>;
}

# ---- Compose ----
-keep class androidx.compose.** { *; }

# ---- Phone number library ----
-keep class com.google.i18n.phonenumbers.** { *; }
-dontwarn com.google.i18n.phonenumbers.**

# ---- Google Play Services + Billing ----
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.android.billingclient.** { *; }

# ---- Coroutines ----
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ---- Gson serialization ----
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---- 최소 디버그 정보 ----
# SourceFile 제거, LineNumberTable만 유지 (crash report용)
-keepattributes LineNumberTable
-renamesourcefileattribute ""
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
