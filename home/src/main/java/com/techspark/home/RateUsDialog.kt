package com.techspark.home

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techspark.core.common.openStorePage
import com.techspark.core.data.pref.IFeelPref
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_LARGE
import kotlinx.coroutines.launch

@Composable
fun RateUsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    RateDialog(modifier, onDismiss,
    onRateClicked = {
        openStorePage(activity)
        coroutineScope.launch {
            IFeelPref.updateRateStatus(activity, true)
            onDismiss()
        }
    })
}

@Composable
private fun RateDialog(modifier: Modifier = Modifier,
onDismiss: () -> Unit={},
onRateClicked: ()->Unit = {}){
    val padding = dimensionResource(id = MARGIN_LARGE)
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(300.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = modifier
                    .padding(top = padding,
                        start = padding,
                        end = padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(painter = painterResource(id = R.drawable.img_rate), contentDescription = "")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.rate_us_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick =onRateClicked ) {
                        Text(
                            text = stringResource(id = android.R.string.ok)
                        )
                    }

                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDialog(){
    IFeelTheme {
        RateDialog()
    }
}