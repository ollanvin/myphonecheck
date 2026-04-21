# ================================================================
# core:security Consumer ProGuard Rules
# app 모듈 R8 패스에 전달되는 keep 규칙
# ================================================================

# ---- TamperSignal 데이터 클래스 보호 ----
# 다른 모듈에서 참조하므로 클래스 구조 유지
-keep class app.myphonecheck.mobile.core.security.tamper.TamperSignal { *; }

# ---- DatabaseKeyProvider ----
# Hilt 주입 대상 — 생성자 보호
-keepclassmembers class app.myphonecheck.mobile.core.security.DatabaseKeyProvider {
    <init>(...);
}

# ---- Hilt 모듈 ----
-keep class app.myphonecheck.mobile.core.security.SecurityModule { *; }

# ---- SQLCipher ----
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.database.**

# ---- Security Crypto ----
-keep class androidx.security.crypto.** { *; }

# ---- JDK 9+ StringConcatFactory ----
-dontwarn java.lang.invoke.StringConcatFactory
