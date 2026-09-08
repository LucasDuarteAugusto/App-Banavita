package br.com.banavita.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R

@Composable
fun FormLoadingState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FormScaffold(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isSaving: Boolean = false,
    showDelete: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSave: () -> Unit,
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                top = contentPadding.calculateTopPadding(),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = contentPadding.calculateBottomPadding(),
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
            onClick = onSave,
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(4.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(stringResource(R.string.form_save))
            }
        }

        if (showDelete) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                onClick = onDelete,
            ) {
                Text(
                    text = stringResource(R.string.form_delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = stringResource(R.string.form_delete_confirm),
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                enabled = !isDeleting,
                onClick = onConfirm,
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isDeleting,
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.form_cancel))
            }
        },
    )
}
