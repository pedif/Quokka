package com.techspark.home

import android.app.Activity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.chart.piechart.PieChart
import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.common.ANIMATION_SLOW
import com.techspark.core.common.TAG_LINK
import com.techspark.core.common.openLink
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.util.MARGIN_SMALL

@Composable
fun FeaturedItem(
    chartData: PieChartData,
    actionType: Action.Type,
    modifier: Modifier = Modifier
) {
    val feelingFound = actionType != Action.Type.NO_INPUT
    var linkAddress by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current as Activity
    LaunchedEffect(key1 = linkAddress) {
        if (linkAddress.isNotEmpty()) {
            openLink(context, linkAddress)
            linkAddress = ""
        }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()

    ) {
        Column(modifier = Modifier.padding(dimensionResource(MARGIN_MEDIUM))) {
            Text(
                text = stringResource(id = R.string.home_summary_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_SMALL)))
            Row {
                if (feelingFound)
                    Column(horizontalAlignment = Alignment.Start) {
                        PieChart(
                            pieChartData = chartData,
                            modifier = Modifier
                                .size(100.dp, 100.dp)
                                .semantics { contentDescription = "chart" },
                            animation = TweenSpec<Float>(
                                durationMillis = ANIMATION_SLOW,
                                delay = 100,
                                easing = LinearOutSlowInEasing
                            )
                        )
                        Action.Type.values().dropLast(1).forEach { type ->
                            Row(
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(
                                        horizontal = dimensionResource(id = MARGIN_MEDIUM),
                                        vertical = 0.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = type.title),
                                    color = type.color,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.width(dimensionResource(id = com.techspark.core.R.dimen.margin_medium)))

                val text = getSolutionText(type = actionType)
                ClickableText(text = text,
                    onClick = { _ ->
                        linkAddress = actionType.link
                    })
            }
        }
    }
}


@Composable
private fun getSolutionText(type: Action.Type): AnnotatedString {
    val label = stringResource(id = com.techspark.core.R.string.last_week_label)
    return buildAnnotatedString {
        if (type == Action.Type.NO_INPUT)
            return AnnotatedString(
                stringResource(
                    id = com.techspark.core.R.string.no_feeling_found,
                    label
                )
            )
        val adj = stringResource(id = type.adjective)
        val summary = stringResource(
            id = com.techspark.core.R.string.feeling_summary_first_part,
            label,
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
                append(stringResource(id = com.techspark.core.R.string.feeling_link_text))
            }
            pop()
        }
    }

}

@Preview(widthDp = 320)
@Composable
private fun PreviewFeaturedItem() {
    IFeelTheme() {
        val pieChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(25f, Color.Red),
                PieChartData.Slice(42f, Color.Blue),
                PieChartData.Slice(23f, Color.Green)
            )
        )
        FeaturedItem(chartData = pieChartData, actionType = Action.Type.HAPPINESS)
    }
}