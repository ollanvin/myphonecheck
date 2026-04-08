package app.callcheck.mobile.ui.backup

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

private const val MIN_PASSPHRASE_LENGTH = 6
private const val MAX_PASSPHRASE_LENGTH = 6

@Composable
private fun BackupPassphraseInputColumn(
    passphrase: String,
    onPassphraseChange: (String) -> Unit,
    confirmPassphrase: String,
    onConfirmPassphraseChange: (String) -> Unit,
    validationError: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "비밀번호를 잊으면 복원할 수 없습니다. 반드시 다른곳에 안전하게 보관하세요",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB3B3B3),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "숫자 6자리를 입력해 주세요",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFB3B3B3),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = passphrase,
            onValueChange = onPassphraseChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("복구 비밀번호") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassphrase,
            onValueChange = onConfirmPassphraseChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("복구 비밀번호 확인") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        )
        validationError?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = err, style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF5350))
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
    confirmButtonLabel: String = "백업",
) {
    var passphrase by remember { mutableStateOf("") }
    var confirmPassphrase by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("복구 비밀번호") },
        text = {
            BackupPassphraseInputColumn(
                passphrase = passphrase,
                onPassphraseChange = {
                    passphrase = it.filter { ch -> ch.isDigit() }.take(MAX_PASSPHRASE_LENGTH)
                    validationError = null
                },
                confirmPassphrase = confirmPassphrase,
                onConfirmPassphraseChange = {
                    confirmPassphrase = it.filter { ch -> ch.isDigit() }.take(MAX_PASSPHRASE_LENGTH)
                    validationError = null
                },
                validationError = validationError,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val err = when {
                        passphrase.isBlank() -> "복구 비밀번호를 입력하세요"
                        confirmPassphrase.isBlank() -> "복구 비밀번호 확인을 입력하세요"
                        passphrase.length < MIN_PASSPHRASE_LENGTH ->
                            "복구 비밀번호는 6자리 숫자여야 합니다"
                        passphrase != confirmPassphrase -> "복구 비밀번호가 일치하지 않습니다"
                        else -> null
                    }
                    validationError = err
                    if (err == null) {
                        val p = passphrase
                        passphrase = ""
                        confirmPassphrase = ""
                        onConfirm(p)
                    }
                },
            ) {
                Text(confirmButtonLabel)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF808080)),
            ) {
                Text("취소")
            }
        },
    )
}
