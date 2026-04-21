# ================================================================
# ProGuard Rules
# ================================================================

# ---- JDK 9+ StringConcatFactory (Kotlin data class toString) ----
# Android 런타임에 미존재. R8 missing_rules.txt 권고 반영.
-dontwarn java.lang.invoke.StringConcatFactory

