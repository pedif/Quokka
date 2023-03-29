package com.techspark.statistics

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_SMALL
import java.text.SimpleDateFormat

@Composable
fun ListItem(
    item: Action,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Row(
        modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = dimensionResource(id = MARGIN_SMALL)),

        ) {

        Column(
            modifier = modifier
                .padding(
                    0.dp,
                    dimensionResource(id = MARGIN_SMALL),
                    0.dp,
                    dimensionResource(id = MARGIN_SMALL)
                )
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                text = getSolutionText(context = LocalContext.current, action = item)
            )

            if (item.comment.isNotEmpty())
                Text(
                    modifier = modifier
                        .padding(dimensionResource(id = MARGIN_SMALL), 0.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    text = item.comment
                )
        }
    }
}

@Composable
private fun getSolutionText(context: Context, action: Action): AnnotatedString {
    return buildAnnotatedString {

        val dateLabel = SimpleDateFormat("dd 'of' MMM ")
        val adj = stringResource(id = action.type.adjective)
        val summary = stringResource(
            id = R.string.action_item_summary,
            adj,
            action.getDurationLabel(context),
            dateLabel.format(action.startDate)
        )
        append(summary)
        //we want the adjective to have a different style
        val adjStartedIndex = summary.indexOf(adj)
        addStyle(
            SpanStyle(action.type.color, fontWeight = FontWeight.Bold),
            adjStartedIndex,
            adjStartedIndex + adj.length
        )
    }
}


@Preview
@Composable
fun PreviewItem() {
    IFeelTheme {
        ListItem(item = DataHelper.getFeelings(System.currentTimeMillis())[0])
    }
}