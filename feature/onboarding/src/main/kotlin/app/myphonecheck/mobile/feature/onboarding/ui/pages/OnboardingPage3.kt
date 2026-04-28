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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.onboarding.R

/** 온보딩 3장: 접근 필요 이유 + 신뢰 확정 */
@Composable
fun OnboardingPage3() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = context.getString(R.string.onboarding_page3_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        data class AccessInfo(
            val target: String,
            val why: String,
            val icon: androidx.compose.ui.graphics.vector.ImageVector,
        )
        val accesses = listOf(
            AccessInfo(
                context.getString(R.string.access_phone_target),
                context.getString(R.string.access_phone_why),
                Icons.Filled.Phone,
            ),
            AccessInfo(
                context.getString(R.string.access_sms_target),
                context.getString(R.string.access_sms_why),
                Icons.Filled.Message,
            ),
            AccessInfo(
                context.getString(R.string.access_usage_target),
                context.getString(R.string.access_usage_why),
                Icons.Filled.Security,
            ),
        )

        for (access in accesses) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(access.icon, null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = access.why,
                            fontSize = 14.sp,
                            color = Color(0xFFB0BEC5),
                        )
                        Text(
                            text = access.target,
                            fontSize = 13.sp,
                            color = Color(0xFF607D8B),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = context.getString(R.string.onboarding_privacy_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = context.getString(R.string.onboarding_privacy_desc),
                    fontSize = 13.sp,
                    color = Color(0xFF81C784).copy(alpha = 0.7f),
                )
            }
        }
    }
}
