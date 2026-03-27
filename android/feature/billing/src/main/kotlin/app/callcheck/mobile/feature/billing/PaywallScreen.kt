package app.callcheck.mobile.feature.billing

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone

/**
 * Paywall screen for CallCheck subscription.
 *
 * Displays:
 * - App name and icon
 * - Value proposition (3-4 bullet points)
 * - Price display: "$1/month"
 * - Subscribe button
 * - Restore purchases link
 * - Terms/privacy links
 * - Dark theme consistent with app
 */
@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
    onSubscriptionRestored: () -> Unit = {},
) {
    val activity = LocalContext.current as? Activity
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    val backgroundColor = Color(0xFF0F0F0F)
    val cardBackground = Color(0xFF1A1A1A)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)
    val primary = Color(0xFF00BCD4)
    val success = Color(0xFF4CAF50)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // App Icon
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "CallCheck",
            modifier = Modifier
                .padding(24.dp)
                .height(80.dp),
            tint = primary,
        )

        // App Name
        Text(
            text = "CallCheck",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "스팸 전화로부터 보호받세요",
            fontSize = 16.sp,
            color = textSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Value Proposition
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ValuePropositionItem(
                icon = Icons.Default.Lock,
                title = "실시간 스팸 탐지",
                description = "AI 기반 분석으로 스팸 전화를 자동으로 감지합니다",
                iconTint = primary,
            )

            ValuePropositionItem(
                icon = Icons.Default.CheckCircle,
                title = "지능형 필터링",
                description = "발신 번호 정보와 검색 데이터로 정확한 판단을 제공합니다",
                iconTint = success,
            )

            ValuePropositionItem(
                icon = Icons.Default.Phone,
                title = "음성 통화 보호",
                description = "중요한 전화는 받고 스팸은 차단하세요",
                iconTint = primary,
            )

            ValuePropositionItem(
                icon = Icons.Default.CheckCircle,
                title = "개인정보 보호",
                description = "모든 데이터는 기기에 저장되며 외부로 전송되지 않습니다",
                iconTint = success,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Price Display
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "월간 구독",
                fontSize = 14.sp,
                color = textSecondary,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                )

                Text(
                    text = "1",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "/월",
                        fontSize = 14.sp,
                        color = textSecondary,
                    )
                    Text(
                        text = "최초 1개월 무료",
                        fontSize = 10.sp,
                        color = success,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Subscribe Button
        Button(
            onClick = {
                if (activity != null) {
                    viewModel.purchaseSubscription(activity)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = Color.White,
            ),
            enabled = subscriptionState !is SubscriptionState.Loading,
        ) {
            if (subscriptionState is SubscriptionState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(8.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "구독 시작하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Restore Purchases
        TextButton(
            onClick = {
                viewModel.restorePurchases()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "이전 구독 복원",
                fontSize = 14.sp,
                color = primary,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error Message
        if (subscriptionState is SubscriptionState.Error) {
            Text(
                text = (subscriptionState as SubscriptionState.Error).message,
                fontSize = 12.sp,
                color = Color(0xFFFF6B6B),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3A1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Success Message
        if (subscriptionState is SubscriptionState.Active) {
            Text(
                text = "구독이 활성화되었습니다!",
                fontSize = 12.sp,
                color = success,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F3A1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Terms and Privacy
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = {
                    // Navigate to privacy policy
                },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(
                    text = "개인정보 처리방침",
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }

            Text(
                text = " • ",
                fontSize = 12.sp,
                color = textSecondary,
            )

            TextButton(
                onClick = {
                    // Navigate to terms
                },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(
                    text = "이용약관",
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ValuePropositionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconTint: Color,
) {
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .height(20.dp),
            tint = iconTint,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
            )

            Text(
                text = description,
                fontSize = 12.sp,
                color = textSecondary,
            )
        }
    }
}
