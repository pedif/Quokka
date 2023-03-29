package com.techspark.day.day

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_LARGE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.day.R

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onAccepted: () -> Unit = {},
    onCanceled: () -> Unit = {}
) {

    Dialog(onDismissRequest = onCanceled) {
        Surface(
            modifier = modifier
                .width(300.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(dimensionResource(id = MARGIN_LARGE))
            ) {
                Text(
                    text = stringResource(id = R.string.delete_label),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.delete_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCanceled) {
                        Text(
                            text = stringResource(id = R.string.dialog_btn_no)
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(id = MARGIN_MEDIUM)))
                    TextButton(onClick = onAccepted) {
                        Text(
                            text = stringResource(id = R.string.dialog_delete_btn_yes),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewDialog() {
    IFeelTheme {
        DeleteDialog()
    }
}