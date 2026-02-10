package org.igo.lidtrainer.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun ExitDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit
) {
    if (!showDialog) return

    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = strings.exitDialogTitle) },
        text = { Text(text = strings.exitDialogMessage) },
        confirmButton = {
            TextButton(onClick = onConfirmExit) {
                Text(
                    text = strings.exitDialogConfirm,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.exitDialogCancel)
            }
        }
    )
}
