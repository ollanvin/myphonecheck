package app.myphonecheck.mobile.feature.onboarding.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.myphonecheck.mobile.feature.countryconfig.LanguageContextProvider
import app.myphonecheck.mobile.feature.onboarding.OnboardingViewModel
import app.myphonecheck.mobile.feature.onboarding.R
import app.myphonecheck.mobile.feature.onboarding.ui.pages.OnboardingPage1
import app.myphonecheck.mobile.feature.onboarding.ui.pages.OnboardingPage2
import app.myphonecheck.mobile.feature.onboarding.ui.pages.OnboardingPage3
import app.myphonecheck.mobile.feature.onboarding.ui.pages.OnboardingPage4
import app.myphonecheck.mobile.feature.onboarding.ui.pages.OnboardingPage5

private const val ONBOARDING_PAGE_COUNT = 5

@Composable
fun OnboardingScreen(
    languageProvider: LanguageContextProvider,
    onContinue: () -> Unit,
) {
    languageProvider.resolveLanguage()
    var currentPage by remember { mutableIntStateOf(0) }
    val viewModel: OnboardingViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (currentPage) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
                3 -> OnboardingPage4()
                4 -> OnboardingPage5(viewModel)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(ONBOARDING_PAGE_COUNT) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == currentPage) 10.dp else 8.dp)
                        .background(
                            color = if (index == currentPage) Color(0xFF4FC3F7) else Color(0xFF455A64),
                            shape = RoundedCornerShape(50),
                        ),
                )
            }
        }

        if (currentPage < 4) {
            Button(
                onClick = { currentPage++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_btn_next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4FC3F7)),
                ) {
                    Text(
                        stringResource(R.string.onboarding_permissions_later),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        stringResource(R.string.onboarding_permissions_continue_home),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1B2A),
                    )
                }
            }
        }
    }
}
