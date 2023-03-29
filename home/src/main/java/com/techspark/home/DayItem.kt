package com.techspark.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.common.TAG_LINK
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.home.model.HomeDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItem(
    day: HomeDay,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit = {}
) {

    Card(modifier = modifier.fillMaxWidth(),
        onClick = { onClick(day.id) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = com.techspark.core.R.dimen.margin_small)),
            modifier = modifier.padding(8.dp)
        ) {
            Text(
                text = day.dayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            var t1 = getSolutionText(type = day.largestActionType, true)
            var t2:AnnotatedString = getSolutionText(type = day.leastActionType, false)
            //if the largest and least action are the same it means this day has no data yet
            if (day.largestActionType == day.leastActionType) {
                t1 =
                    AnnotatedString(
                        stringResource(id = com.techspark.core.R.string.no_feeling_found_for_day)
                    )
                t2 = AnnotatedString("")
            }
            val lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 4 / 3
            Text(
                text = t1,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = lineHeight,
                modifier = Modifier.sizeIn(minHeight = with(LocalDensity.current) {
                    //2 lines
                    (lineHeight * 2).toDp()
                })
            )

                Text(
                    text = t2,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = lineHeight,
                    modifier = Modifier.sizeIn(minHeight = with(LocalDensity.current) {
                        //2 lines
                        (lineHeight * 2).toDp()
                    })
                )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.action_go_next),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = modifier.alignByBaseline()
                )
                Image(
                    imageVector = Icons.Default.ArrowForward, contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(16.dp, 16.dp)
                )
            }
        }
    }

}


@Composable
private fun getSolutionText(type: Action.Type, isMax: Boolean = true): AnnotatedString {
    return buildAnnotatedString {

        val adj = stringResource(id = type.adjective)
        val sentence = if (isMax) R.string.day_feeling_summary_short_max else
            R.string.day_feeling_summary_short_min
        val summary = stringResource(
            id = sentence,
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

@Preview
@Composable
private fun previewDayItem() {
    IFeelTheme {
        DayItem(day = HomeDay.fromDay(DataHelper.getDays(System.currentTimeMillis())[0]))
    }
}