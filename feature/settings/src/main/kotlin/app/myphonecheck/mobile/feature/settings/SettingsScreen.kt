package app.myphonecheck.mobile.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel


/**
 * Settings screen for MyPhoneCheck app.
 *
 * Sections:
 * - Account (subscription status)
 * - General (language, country)
 * - Privacy & Permissions
 * - About
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState(initial = AppSettings())
    val permissions by viewModel.permissionStatus.collectAsState()

    val backgroundColor = Color(0xFF0F0F0F)
    val cardBackground = Color(0xFF1A1A1A)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)
    val textTertiary = Color(0xFF808080)
    val primary = Color(0xFF00BCD4)
    val dividerColor = Color(0xFF424242)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Account Section
        SettingsSection(
            title = stringResource(R.string.settings_section_account),
            backgroundColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            dividerColor = dividerColor,
        ) {
            SettingsItem(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.settings_subscription_status),
                subtitle = stringResource(R.string.settings_subscription_active),
                onClick = {},
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            SettingsItem(
                icon = Icons.Default.Settings,
                title = stringResource(R.string.settings_subscription_manage),
                subtitle = stringResource(R.string.settings_subscription_manage_desc),
                onClick = {},
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // General Section
        SettingsSection(
            title = stringResource(R.string.settings_section_general),
            backgroundColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            dividerColor = dividerColor,
        ) {
            LanguageSelector(
                currentLanguage = settings.language,
                onLanguageSelected = { language ->
                    viewModel.updateLanguage(language)
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
                dividerColor = dividerColor,
            )

            SettingsDivider(dividerColor)

            CountrySelector(
                currentCountry = settings.countryOverride,
                onCountrySelected = { country ->
                    viewModel.updateCountryOverride(country)
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
                dividerColor = dividerColor,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Privacy & Permissions Section
        SettingsSection(
            title = stringResource(R.string.settings_section_privacy_permissions),
            backgroundColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            dividerColor = dividerColor,
        ) {
            PermissionItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.settings_contacts_permission),
                subtitle = if (permissions.contactsPermission) stringResource(R.string.settings_perm_approved) else stringResource(R.string.settings_perm_not_approved),
                statusColor = if (permissions.contactsPermission) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = {
                    if (!permissions.contactsPermission) {
                        viewModel.openAppPermissionSettings()
                    }
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            PermissionItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.settings_call_log_permission),
                subtitle = if (permissions.callLogPermission) stringResource(R.string.settings_perm_approved) else stringResource(R.string.settings_perm_not_approved),
                statusColor = if (permissions.callLogPermission) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = {
                    if (!permissions.callLogPermission) {
                        viewModel.openAppPermissionSettings()
                    }
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            SettingsItem(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.settings_default_spam_filter),
                subtitle = stringResource(R.string.settings_open_system_settings),
                onClick = {
                    viewModel.setAsDefaultCallerIdApp()
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // About Section
        SettingsSection(
            title = stringResource(R.string.settings_section_about),
            backgroundColor = backgroundColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            dividerColor = dividerColor,
        ) {
            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_version),
                subtitle = "1.0.0",
                onClick = {},
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            SettingsItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.settings_privacy_policy),
                subtitle = "myphonecheck.app/privacy",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myphonecheck.app/privacy"))
                    context.startActivity(intent)
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_terms),
                subtitle = "myphonecheck.app/terms",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myphonecheck.app/terms"))
                    context.startActivity(intent)
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )

            SettingsDivider(dividerColor)

            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_feedback),
                subtitle = "contact@myphonecheck.app",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:contact@myphonecheck.app")
                    }
                    context.startActivity(intent)
                },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primary = primary,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    backgroundColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    dividerColor: Color,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textSecondary,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp, start = 4.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF1A1A1A),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                )
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    primary: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.height(24.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.height(20.dp),
        )
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusColor: Color,
    onClick: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    primary: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.height(24.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.height(20.dp),
        )
    }
}

@Composable
private fun LanguageSelector(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    primary: Color,
    dividerColor: Color,
) {
    var expanded by remember { mutableStateOf(false) }

    val languageMap = mapOf(
        "auto" to stringResource(R.string.settings_auto_detect),
        "ko" to "한국어",
        "en" to "English",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.height(24.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                )

                Text(
                    text = languageMap[currentLanguage] ?: stringResource(R.string.settings_auto_detect),
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.height(20.dp),
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(Color(0xFF2A2A2A)),
    ) {
        languageMap.forEach { (code, label) ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(label, color = textPrimary)
                        if (currentLanguage == code) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.height(16.dp),
                            )
                        }
                    }
                },
                onClick = {
                    onLanguageSelected(code)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun CountrySelector(
    currentCountry: String?,
    onCountrySelected: (String?) -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    primary: Color,
    dividerColor: Color,
) {
    var expanded by remember { mutableStateOf(false) }

    val countryMap = mapOf(
        null to stringResource(R.string.settings_auto_detect),
        "KR" to stringResource(R.string.settings_country_kr),
        "US" to stringResource(R.string.settings_country_us),
        "JP" to stringResource(R.string.settings_country_jp),
        "CN" to stringResource(R.string.settings_country_cn),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.height(24.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_country_region),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary,
                )

                Text(
                    text = countryMap[currentCountry] ?: stringResource(R.string.settings_auto_detect),
                    fontSize = 12.sp,
                    color = textSecondary,
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = textSecondary,
            modifier = Modifier.height(20.dp),
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(Color(0xFF2A2A2A)),
    ) {
        countryMap.forEach { (code, label) ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(label, color = textPrimary)
                        if (currentCountry == code) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.height(16.dp),
                            )
                        }
                    }
                },
                onClick = {
                    onCountrySelected(code)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun SettingsDivider(dividerColor: Color) {
    Divider(
        color = dividerColor,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}
