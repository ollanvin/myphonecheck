package app.myphonecheck.mobile.feature.billing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
    onSubscriptionRestored: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val subscriptionState by viewModel.subscriptionState.collectAsState()
    val trialCountdown by viewModel.trialCountdown.collectAsState()
    val valueAnchor by viewModel.valueAnchor.collectAsState()

    PaywallContent(
        subscriptionState = subscriptionState,
        trialCountdown = trialCountdown,
        valueAnchor = valueAnchor,
        onSubscribeClick = {
            if (activity != null) viewModel.purchaseSubscription(activity)
        },
        onRestoreClick = {
            viewModel.restorePurchases()
            onSubscriptionRestored()
        },
        onCancelSubscriptionClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(PLAY_SUBSCRIPTION_MANAGE_URI)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(intent) }
        },
        onPrivacyClick = {},
        onTermsClick = {},
    )
}

@Composable
fun PaywallContent(
    subscriptionState: SubscriptionState,
    trialCountdown: TrialCountdown,
    valueAnchor: SubscriptionValueAnchorState?,
    onSubscribeClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(0xFF0F0F0F)
    val cardBackground = Color(0xFF1A1A1A)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB3B3B3)
    val primary = Color(0xFF00BCD4)
    val danger = Color(0xFFFF3B30)
    val ctaText = if (trialCountdown is TrialCountdown.Remaining) {
        "지금 보호 계속하기"
    } else {
        "지금 보호 시작하기"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 112.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "구독",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubscriptionValueAnchorCard(
                state = valueAnchor,
                modifier = Modifier.fillMaxWidth(),
            )

            if (valueAnchor?.visible == true) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            TrialCountdown(
                state = trialCountdown,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .height(72.dp),
                tint = primary,
            )

            Text(
                text = "MyPhoneCheck",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "실측된 보호 기록을 바탕으로 구독 전에도 상태를 확인합니다",
                fontSize = 14.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground, shape = RoundedCornerShape(12.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ValuePropositionItem(
                    icon = Icons.Default.Phone,
                    title = "실시간 의심 전화 확인",
                    description = "저장된 판단 기록을 바탕으로 의심 전화를 확인합니다.",
                    iconTint = primary,
                )
                ValuePropositionItem(
                    icon = Icons.Default.Lock,
                    title = "위험 링크 메시지 확인",
                    description = "로컬에 저장된 위험 링크 메시지만 선별해 보여줍니다.",
                    iconTint = Color(0xFF34C759),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSubscribeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = Color.White,
                ),
                enabled = subscriptionState !is SubscriptionState.Loading &&
                    subscriptionState !is SubscriptionState.Active,
            ) {
                if (subscriptionState is SubscriptionState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = ctaText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onRestoreClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "이전 구독 복원",
                    fontSize = 14.sp,
                    color = primary,
                )
            }

            if (subscriptionState is SubscriptionState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = subscriptionState.message,
                    fontSize = 12.sp,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3A1F1F), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onPrivacyClick) {
                    Text(
                        text = "개인정보 처리방침",
                        fontSize = 12.sp,
                        color = textSecondary,
                    )
                }
                Text(
                    text = "·",
                    fontSize = 12.sp,
                    color = textSecondary,
                )
                TextButton(onClick = onTermsClick) {
                    Text(
                        text = "이용약관",
                        fontSize = 12.sp,
                        color = textSecondary,
                    )
                }
            }
        }

        Button(
            onClick = onCancelSubscriptionClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = danger,
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(
                text = "구독 취소",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private const val PLAY_SUBSCRIPTION_MANAGE_URI: String =
    "https://play.google.com/store/account/subscriptions?sku=myphonecheck_monthly&package=app.myphonecheck.mobile"

@Composable
private fun ValuePropositionItem(
    icon: ImageVector,
    title: String,
    description: String,
    iconTint: Color,
) {
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
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFFB3B3B3),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 920)
@Composable
private fun PaywallContentPreview_WithMeasuredState() {
    PaywallContent(
        subscriptionState = SubscriptionState.Active,
        trialCountdown = TrialCountdown.Remaining(days = 3),
        valueAnchor = SubscriptionValueAnchorState(
            selectedPeriodLabel = "최근 7일",
            suspiciousCallsCount = 2,
            riskyLinkMessagesCount = 1,
            visible = true,
        ),
        onSubscribeClick = {},
        onRestoreClick = {},
        onCancelSubscriptionClick = {},
        onPrivacyClick = {},
        onTermsClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 920)
@Composable
private fun PaywallContentPreview_EmptyState() {
    PaywallContent(
        subscriptionState = SubscriptionState.NotPurchased,
        trialCountdown = TrialCountdown.NotApplicable,
        valueAnchor = null,
        onSubscribeClick = {},
        onRestoreClick = {},
        onCancelSubscriptionClick = {},
        onPrivacyClick = {},
        onTermsClick = {},
    )
}
