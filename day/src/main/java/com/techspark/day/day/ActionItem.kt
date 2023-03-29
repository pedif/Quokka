package com.techspark.day.day

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import com.techspark.core.util.ACTION_DURATION_DESCRIPTION
import com.techspark.core.util.CURRENT_FEELING_TITLE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.util.MARGIN_SMALL

@Composable
fun ListItem(
    item: Action,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    Row(
        modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(MARGIN_SMALL))
    ) {
        Box(
            modifier = modifier
                .width(dimensionResource(MARGIN_MEDIUM))
                .fillMaxHeight()
                .background(item.color)
        )
        Column(
            modifier = modifier
                .padding(
                    0.dp,
                    dimensionResource(id = MARGIN_SMALL),
                    0.dp,
                    dimensionResource(id = MARGIN_SMALL)
                )
                .weight(1f)
        ) {

            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                text = getSolutionText(action = item)
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
        Image(imageVector = Icons.Default.Delete,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error),
            contentDescription = "Delete",
            modifier = Modifier
                .clickable { onDelete() }
                .padding(dimensionResource(id = MARGIN_SMALL)))
    }
}

@Composable
private fun getCurrentFeelingSolutionText(type: Action.Type): AnnotatedString {
    return buildAnnotatedString {

        val adj = stringResource(id = type.adjective)
        val summary = stringResource(
            id = CURRENT_FEELING_TITLE,
            adj
        )
        append(summary)
        //we want the adjective to have a different style
        val adjStartedIndex = summary.indexOf(adj)
        addStyle(
            SpanStyle(type.color, fontWeight = FontWeight.Bold),
            adjStartedIndex,
            adjStartedIndex + adj.length
        )
    }
}

@Composable
private fun getSolutionText(action: Action): AnnotatedString {
    if (action.endDate == 0L)
        return getCurrentFeelingSolutionText(type = action.type)
    return buildAnnotatedString {

        val adj = stringResource(id = action.type.adjective)
        val duration = action.getDurationLabel(LocalContext.current)
        val summary = stringResource(
            id = ACTION_DURATION_DESCRIPTION,
            adj,
            duration
        )
        append(summary)

        //we want the adjective to have a different style
        val adjStartedIndex = summary.indexOf(adj)
        addStyle(
            SpanStyle(action.type.color, fontWeight = FontWeight.Bold),
            adjStartedIndex,
            adjStartedIndex + adj.length
        )
        //we want the duration to have a different style
        val durationIndex = summary.indexOf(duration)
        addStyle(
            SpanStyle(fontWeight = FontWeight.Bold),
            durationIndex,
            durationIndex + duration.length
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