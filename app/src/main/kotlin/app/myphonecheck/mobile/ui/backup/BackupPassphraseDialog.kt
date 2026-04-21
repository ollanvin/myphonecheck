package app.myphonecheck.mobile.ui.backup

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.myphonecheck.mobile.R

private const val MIN_PASSPHRASE_LENGTH = 6
private const val MAX_PASSPHRASE_LENGTH = 6

@Composable
private fun BackupPassphraseInputColumn(
    passphrase: String,
    onPassphraseChange: (String) -> Unit,
    confirmPassphrase: String,
    onConfirmPassphraseChange: (String) -> Unit,
    @StringRes validationErrorResId: Int?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.backup_passphrase_warning),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB3B3B3),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.backup_passphrase_hint),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB3B3B3),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = passphrase,
            onValueChange = onPassphraseChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.backup_passphrase_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassphrase,
            onValueChange = onConfirmPassphraseChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.backup_passphrase_confirm_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        )
        validationErrorResId?.let { resId ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(resId),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEF5350),
            )
        }
    }
}

/**
 * 복구 비밀번호·확인 입력. 입력값은 저장하지 않으며 [onConfirm]으로만 일회 전달된다.
 */
@Composable
fun BackupPassphraseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    @StringRes confirmButtonLabelResId: Int = R.string.backup_confirm_default,
) {
    var passphrase by remember { mutableStateOf("") }
    var confirmPassphrase by remember { mutableStateOf("") }
    var validationErrorResId by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.backup_passphrase_title)) },
        text = {
            BackupPassphraseInputColumn(
                passphrase = passphrase,
                onPassphraseChange = {
                    passphrase = it.filter { ch -> ch.isDigit() }.take(MAX_PASSPHRASE_LENGTH)
                    validationErrorResId = null
                },
                confirmPassphrase = confirmPassphrase,
                onConfirmPassphraseChange = {
                    confirmPassphrase = it.filter { ch -> ch.isDigit() }.take(MAX_PASSPHRASE_LENGTH)
                    validationErrorResId = null
                },
                validationErrorResId = validationErrorResId,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val errResId = when {
                        passphrase.isBlank() -> R.string.backup_passphrase_error_empty
                        confirmPassphrase.isBlank() -> R.string.backup_passphrase_error_confirm_empty
                        passphrase.length < MIN_PASSPHRASE_LENGTH -> R.string.backup_passphrase_error_length
                        passphrase != confirmPassphrase -> R.string.backup_passphrase_error_mismatch
                        else -> null
                    }
                    validationErrorResId = errResId
                    if (errResId == null) {
                        val p = passphrase
                        passphrase = ""
                        confirmPassphrase = ""
                        onConfirm(p)
                    }
                },
            ) {
                Text(stringResource(confirmButtonLabelResId))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF808080)),
            ) {
                Text(stringResource(R.string.common_cancel))
            }
        },
    )
}
