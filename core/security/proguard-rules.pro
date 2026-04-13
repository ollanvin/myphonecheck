# ================================================================
# core:security ProGuard Rules
# 올랑방 앱팩토리 보안 모듈 난독화 정책
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
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# ---- Security Crypto ----
-keep class androidx.security.crypto.** { *; }
