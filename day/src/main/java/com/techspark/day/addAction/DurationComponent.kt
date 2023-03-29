package com.techspark.day.addAction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.roundedBorder
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.day.R

@Composable
fun DurationComponent(
    modifier: Modifier = Modifier,
    durationLabel: String = "1h",
    onDurationSelected: (Int) -> Unit = {}
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.duration_label))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DurationItem(text = stringResource(id = R.string.duration_continue)) {
                onDurationSelected(0)
            }
            Text(
                text = " ", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))
            DurationItem(text = stringResource(id = R.string.duration_10)) {
                onDurationSelected(10)
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DurationItem(text = stringResource(id = R.string.duration_30)) {
                onDurationSelected(30)
            }
            Text(
                text = " ", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            DurationItem(text = stringResource(id = R.string.duration_60)) {
                onDurationSelected(60)
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
        Text(
            text = durationLabel,
            modifier = Modifier
                .height(
                    dimensionResource(id = R.dimen.button_height)
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DurationItem(
    text: String,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {}
) {


    Text(

        textAlign = TextAlign.Center,

        modifier = modifier
            .clickable { onItemClicked() }
            .size(
                dimensionResource(id = R.dimen.button_width),
                dimensionResource(id = R.dimen.button_height)
            )
            .roundedBorder()
            .wrapContentHeight(),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold
    )


}

@Composable
@Preview(showBackground = true)
private fun PreviewDurationComponent() {
    IFeelTheme {
        DurationComponent()
    }
}