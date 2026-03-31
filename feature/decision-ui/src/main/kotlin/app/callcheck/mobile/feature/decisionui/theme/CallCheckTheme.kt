package app.callcheck.mobile.feature.decisionui.theme

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
object CallCheckColors {
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

data class CallCheckColorScheme(
    val darkBackground: Color = CallCheckColors.darkBackground,
    val darkOverlay: Color = CallCheckColors.darkOverlay,
    val cardBackground: Color = CallCheckColors.cardBackground,
    val riskSafe: Color = CallCheckColors.riskSafe,
    val riskLow: Color = CallCheckColors.riskLow,
    val riskMedium: Color = CallCheckColors.riskMedium,
    val riskHigh: Color = CallCheckColors.riskHigh,
    val riskCritical: Color = CallCheckColors.riskCritical,
    val textPrimary: Color = CallCheckColors.textPrimary,
    val textSecondary: Color = CallCheckColors.textSecondary,
    val textTertiary: Color = CallCheckColors.textTertiary,
    val primary: Color = CallCheckColors.primary,
    val primaryDark: Color = CallCheckColors.primaryDark,
    val accent: Color = CallCheckColors.accent,
    val error: Color = CallCheckColors.error,
    val success: Color = CallCheckColors.success,
    val warning: Color = CallCheckColors.warning,
    val buttonAnswer: Color = CallCheckColors.buttonAnswer,
    val buttonReject: Color = CallCheckColors.buttonReject,
    val buttonBlock: Color = CallCheckColors.buttonBlock,
    val buttonDisabled: Color = CallCheckColors.buttonDisabled,
    val divider: Color = CallCheckColors.divider,
    val border: Color = CallCheckColors.border,
)

object CallCheckTypography {
    val headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = CallCheckColors.textPrimary,
    )

    val headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = CallCheckColors.textPrimary,
    )

    val headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = CallCheckColors.textPrimary,
    )

    val bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = CallCheckColors.textPrimary,
    )

    val bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = CallCheckColors.textSecondary,
    )

    val bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = CallCheckColors.textTertiary,
    )

    val labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = CallCheckColors.textPrimary,
    )

    val labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = CallCheckColors.textSecondary,
    )

    val labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = CallCheckColors.textTertiary,
    )
}

val LocalCallCheckColorScheme = staticCompositionLocalOf { CallCheckColorScheme() }

@Composable
fun CallCheckTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = CallCheckColorScheme()

    val darkMaterialScheme = darkColorScheme(
        primary = CallCheckColors.primary,
        onPrimary = Color.White,
        secondary = CallCheckColors.accent,
        onSecondary = Color.White,
        error = CallCheckColors.error,
        onError = Color.White,
        background = CallCheckColors.darkBackground,
        onBackground = CallCheckColors.textPrimary,
        surface = CallCheckColors.cardBackground,
        onSurface = CallCheckColors.textPrimary,
    )

    CompositionLocalProvider(
        LocalCallCheckColorScheme provides colorScheme,
    ) {
        MaterialTheme(
            colorScheme = darkMaterialScheme,
            content = content,
        )
    }
}

object CallCheckTheme {
    val colors: CallCheckColorScheme
        @Composable
        get() = LocalCallCheckColorScheme.current

    val typography = CallCheckTypography
}
