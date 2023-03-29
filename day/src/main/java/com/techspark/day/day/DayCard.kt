package com.techspark.day.day

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.techspark.core.common.DataHelper
import com.techspark.core.model.Day
import com.techspark.core.theme.IFeelTheme
import com.techspark.day.R
import com.techspark.core.chart.horizontal.HorizontalChart
import com.techspark.core.common.TAG_LINK
import com.techspark.core.model.Action
import com.techspark.core.util.MARGIN_LARGE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.util.MARGIN_SMALL
import java.text.DateFormat
import java.util.*

@Composable
fun DayCard(
    day: Day,
    modifier: Modifier = Modifier,
    onLinkSelected: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = MARGIN_MEDIUM))
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = MARGIN_LARGE)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = MARGIN_MEDIUM))
        ) {
            Text(
                text = DateFormat.getDateInstance().format(Date(day.startDate)),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_SMALL)))
            //Show the chart for the emotions
            HorizontalChart(day.feelingDuration.toList().sortedBy { pair -> pair.first.ordinal })
            //Show a Short summary of the day and a help link

            val text = getSolutionText(type = day.getTheMostFeltFeelingType())
//            ClickableText(text = text,
//                style = MaterialTheme.typography.bodyMedium){}
            ClickableText(text = text, onClick = { _ ->
                onLinkSelected(day.getTheMostFeltFeelingType().link)
            })
        }
    }
}


@Preview
@Composable
fun PreviewDayCard() {

    val startDate = System.currentTimeMillis()
    val day = Day(startDate, DataHelper.getFeelings(startDate))
    IFeelTheme {
        DayCard(day)
    }
}

@Composable
private fun getSolutionText(type: Action.Type): AnnotatedString {
    return buildAnnotatedString {

        if (type == Action.Type.NO_INPUT)
            return AnnotatedString(
                stringResource(
                    id = com.techspark.core.R.string.no_feeling_found_for_day
                )
            )

        val adj = stringResource(id = type.adjective)
        val summary = stringResource(
            id = R.string.day_feeling_summary_first_part,
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
        append(stringResource(id = type.solution))

        // We attach this *URL* annotation to the following content
        // until `pop()` is called
        if (type.link.isNotEmpty()) {
            pushStringAnnotation(tag = TAG_LINK, annotation = type.link)
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.feeling_link_text))
            }
            pop()
        }
    }

}