package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * Displays the phone number with formatting.
 */
@Composable
fun PhoneNumberHeader(
    phoneNumber: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatPhoneNumber(phoneNumber),
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Country flag emoji (placeholder - in a real app would use country code)
        Text(
            text = "🇰🇷",
            fontSize = 16.sp,
            modifier = Modifier.size(20.dp),
        )
    }
}

/**
 * Format phone number to a readable format.
 * For Korean numbers: 010-1234-5678
 */
private fun formatPhoneNumber(number: String): String {
    return when {
        // Already formatted
        number.contains("-") -> number
        // Korean mobile (11 digits)
        number.length == 11 && (number.startsWith("01")) -> {
            "${number.substring(0, 3)}-${number.substring(3, 7)}-${number.substring(7)}"
        }
        // Korean landline (10 digits)
        number.length == 10 -> {
            "${number.substring(0, 2)}-${number.substring(2, 6)}-${number.substring(6)}"
        }
        // International format with +
        number.startsWith("+") -> number
        // Default: return as-is
        else -> number
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneNumberHeaderPreview() {
    MyPhoneCheckTheme {
        PhoneNumberHeader(phoneNumber = "01012345678")
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneNumberHeaderFormattedPreview() {
    MyPhoneCheckTheme {
        PhoneNumberHeader(phoneNumber = "010-1234-5678")
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneNumberHeaderLandlinePreview() {
    MyPhoneCheckTheme {
        PhoneNumberHeader(phoneNumber = "0212345678")
    }
}
