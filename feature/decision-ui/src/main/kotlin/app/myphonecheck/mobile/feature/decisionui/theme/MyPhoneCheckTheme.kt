package app.myphonecheck.mobile.feature.decisionui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color definitions
object MyPhoneCheckColors {
    // Dark theme background colors
    val darkBackground = Color(0xFF0F0F0F)
    val darkOverlay = Color(0x99000000) // Semi-transparent dark overlay
    val cardBackground = Color(0xFF1A1A1A)

    // Risk level colors
    val riskSafe = Color(0xFF4CAF50) // Green
    val riskLow = Color(0xFF8BC34A) // Light Green
    val riskMedium = Color(0xFFFFC107) // Amber
    val riskHigh = Color(0xFFFF9800) // Orange
    val riskCritical = Color(0xFFF44336) // Red

    // Text colors
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)
    val textTertiary = Color(0xFF808080)

    // UI element colors
    val primary = Color(0xFF00BCD4) // Cyan
    val primaryDark = Color(0xFF0097A7)
    val accent = Color(0xFF82B1FF)
    val error = Color(0xFFFF6B6B)
    val success = Color(0xFF4CAF50)
    val warning = Color(0xFFFFC107)

    // Button colors
    val buttonAnswer = Color(0xFF4CAF50) // Green
    val buttonReject = Color(0xFFFF9800) // Orange
    val buttonBlock = Color(0xFFF44336) // Red
    val buttonDisabled = Color(0xFF424242)

    // Divider and border
    val divider = Color(0xFF424242)
    val border = Color(0xFF333333)
}

data class MyPhoneCheckColorScheme(
    val darkBackground: Color = MyPhoneCheckColors.darkBackground,
    val darkOverlay: Color = MyPhoneCheckColors.darkOverlay,
    val cardBackground: Color = MyPhoneCheckColors.cardBackground,
    val riskSafe: Color = MyPhoneCheckColors.riskSafe,
    val riskLow: Color = MyPhoneCheckColors.riskLow,
    val riskMedium: Color = MyPhoneCheckColors.riskMedium,
    val riskHigh: Color = MyPhoneCheckColors.riskHigh,
    val riskCritical: Color = MyPhoneCheckColors.riskCritical,
    val textPrimary: Color = MyPhoneCheckColors.textPrimary,
    val textSecondary: Color = MyPhoneCheckColors.textSecondary,
    val textTertiary: Color = MyPhoneCheckColors.textTertiary,
    val primary: Color = MyPhoneCheckColors.primary,
    val primaryDark: Color = MyPhoneCheckColors.primaryDark,
    val accent: Color = MyPhoneCheckColors.accent,
    val error: Color = MyPhoneCheckColors.error,
    val success: Color = MyPhoneCheckColors.success,
    val warning: Color = MyPhoneCheckColors.warning,
    val buttonAnswer: Color = MyPhoneCheckColors.buttonAnswer,
    val buttonReject: Color = MyPhoneCheckColors.buttonReject,
    val buttonBlock: Color = MyPhoneCheckColors.buttonBlock,
    val buttonDisabled: Color = MyPhoneCheckColors.buttonDisabled,
    val divider: Color = MyPhoneCheckColors.divider,
    val border: Color = MyPhoneCheckColors.border,
)

object MyPhoneCheckTypography {
    val headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MyPhoneCheckColors.textPrimary,
    )

    val headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MyPhoneCheckColors.textPrimary,
    )

    val headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = MyPhoneCheckColors.textPrimary,
    )

    val bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = MyPhoneCheckColors.textPrimary,
    )

    val bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = MyPhoneCheckColors.textSecondary,
    )

    val bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = MyPhoneCheckColors.textTertiary,
    )

    val labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MyPhoneCheckColors.textPrimary,
    )

    val labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MyPhoneCheckColors.textSecondary,
    )

    val labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = MyPhoneCheckColors.textTertiary,
    )
}

val LocalMyPhoneCheckColorScheme = staticCompositionLocalOf { MyPhoneCheckColorScheme() }

@Composable
fun MyPhoneCheckTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = MyPhoneCheckColorScheme()

    val darkMaterialScheme = darkColorScheme(
        primary = MyPhoneCheckColors.primary,
        onPrimary = Color.White,
        secondary = MyPhoneCheckColors.accent,
        onSecondary = Color.White,
        error = MyPhoneCheckColors.error,
        onError = Color.White,
        background = MyPhoneCheckColors.darkBackground,
        onBackground = MyPhoneCheckColors.textPrimary,
        surface = MyPhoneCheckColors.cardBackground,
        onSurface = MyPhoneCheckColors.textPrimary,
    )

    CompositionLocalProvider(
        LocalMyPhoneCheckColorScheme provides colorScheme,
    ) {
        MaterialTheme(
            colorScheme = darkMaterialScheme,
            content = content,
        )
    }
}

object MyPhoneCheckTheme {
    val colors: MyPhoneCheckColorScheme
        @Composable
        get() = LocalMyPhoneCheckColorScheme.current

    val typography = MyPhoneCheckTypography
}
