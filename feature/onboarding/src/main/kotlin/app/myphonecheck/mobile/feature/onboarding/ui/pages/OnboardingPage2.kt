package app.myphonecheck.mobile.feature.onboarding.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.onboarding.R

/** 온보딩 2장: 4곳 공격 포인트 */
@Composable
fun OnboardingPage2() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = context.getString(R.string.onboarding_page2_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE57373),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        data class ThreatInfo(
            val icon: ImageVector,
            val threat: String,
            val attack: String,
            val color: Color,
        )
        val threats = listOf(
            ThreatInfo(
                Icons.Filled.Phone,
                context.getString(R.string.threat_call_title),
                context.getString(R.string.threat_call_desc),
                Color(0xFF4FC3F7),
            ),
            ThreatInfo(
                Icons.Filled.Message,
                context.getString(R.string.threat_message_title),
                context.getString(R.string.threat_message_desc),
                Color(0xFF81C784),
            ),
            ThreatInfo(
                Icons.Filled.Security,
                context.getString(R.string.threat_privacy_title),
                context.getString(R.string.threat_privacy_desc),
                Color(0xFFE57373),
            ),
        )

        for (threat in threats) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A1A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(threat.icon, null, tint = threat.color, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = threat.threat,
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = threat.attack,
                            fontSize = 13.sp,
                            color = Color(0xFFEF9A9A),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = context.getString(R.string.onboarding_page2_footer),
            fontSize = 15.sp,
            color = Color(0xFF4FC3F7),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
