# Proguard rules for MyPhoneCheck

# Keep all classes in model package
-keep class app.myphonecheck.mobile.core.model.** { *; }

# Keep Hilt annotations
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }

# Keep navigation arguments
-keepclasseswithmembers class * {
    @androidx.navigation.** <fields>;
}

# Preserve Compose runtime
-keep class androidx.compose.** { *; }

# Preserve phone number library
-keep class com.google.i18n.phonenumbers.** { *; }
-dontwarn com.google.i18n.phonenumbers.**

# Keep Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# General rules
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes MethodParameters
