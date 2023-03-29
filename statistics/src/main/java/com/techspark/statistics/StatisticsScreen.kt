package com.techspark.statistics

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.techspark.core.chart.bar.BarChart
import com.techspark.core.chart.bar.BarChartData
import com.techspark.core.chart.piechart.PieChart
import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.common.DataHelper
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.chart.bar.renderer.label.LabelDrawer
import com.techspark.core.chart.bar.renderer.label.SimpleValueDrawer
import com.techspark.core.chart.line.LineChart
import com.techspark.core.chart.line.LineChartData
import com.techspark.core.chart.line.renderer.line.GradientLineShader
import com.techspark.core.chart.line.renderer.line.SolidLineDrawer
import com.techspark.core.chart.line.renderer.point.FilledCircularPointDrawer
import com.techspark.core.common.TAG_LINK
import com.techspark.core.common.openLink
import com.techspark.core.util.MARGIN_LARGE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.view.Spinner

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var linkAddress by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current as Activity
    LaunchedEffect(key1 = linkAddress){
        if(linkAddress.isNotEmpty()){
            openLink(context, linkAddress)
            linkAddress =""
        }
    }

    val state = viewModel.state
    if (!viewModel.hasFetchedOnce)
        viewModel.onActionTypeSet(LocalContext.current)
    if (!state.isLoading)
        StatisticsScreen(
            actions = state.actions,
            selectedType = state.selectedType,
            selectedInterval = state.selectedInterval.ordinal,
            largestActionType = state.largestType,
            barChartData = state.barChartData,
            pieChartData = state.pieChartData,
            lineChartData = state.lineChartData,
            onActionTypeSelected = { viewModel.onActionTypeSet(context, it) },
            onTimeIntervalSelected = { viewModel.onIntervalSet(context, it) },
            onLinkedClicked = {
                link->linkAddress = link},
            modifier = modifier
        )
}

@Composable
private fun StatisticsScreen(
    actions: List<Action>,
    selectedType: Action.Type,
    selectedInterval:Int,
    largestActionType: Action.Type,
    barChartData: BarChartData?,
    pieChartData: PieChartData?,
    lineChartData: LineChartData?,
    modifier: Modifier = Modifier,
    onActionTypeSelected: (Int) -> Unit = {},
    onTimeIntervalSelected: (Int) -> Unit = {},
    onLinkedClicked:(String)->Unit = {}
) {

    Column(
        modifier = modifier.padding(dimensionResource(id = MARGIN_MEDIUM)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val intervals = stringArrayResource(id = R.array.time_intervals).toList();
        Row {
            val types = mutableListOf("ALL")
            types.addAll(Action.Type.values().map { stringResource(id = it.title) }.dropLast(1))
            Spinner(
                items = intervals,
                initialText = intervals[0]
            ) { id ->
                onTimeIntervalSelected(id)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Spinner(
                items = types,
                initialText = "All"
            ) { id ->
                onActionTypeSelected(id - 1)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (selectedType == Action.Type.NO_INPUT) {
            barChartData?.let {
                BarChart(
                    barChartData = barChartData,
                    modifier = Modifier
                        .height(150.dp)
                        .padding(horizontal = dimensionResource(id = MARGIN_MEDIUM)),
                    labelDrawer = SimpleValueDrawer(drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
                )
            }
            pieChartData?.let {
                Spacer(modifier = Modifier.height(20.dp))
                PieChart(
                    pieChartData = pieChartData,
                    modifier = Modifier.height(150.dp)
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_LARGE)))
            val text = getSolutionText(type =largestActionType, timeInterval = selectedInterval)
            ClickableText(text = text,
                modifier= Modifier.fillMaxWidth(),
                onClick = {
                    onLinkedClicked(largestActionType.link)
                })
        } else {
            lineChartData?.let {
                val dataColor = selectedType.color
                LineChart(
                    lineChartData = lineChartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(dimensionResource(id = MARGIN_MEDIUM)),
                    lineShader = GradientLineShader(
                        listOf(
                            dataColor,
                            Color.Transparent
                        )
                    ),
                    pointDrawer = FilledCircularPointDrawer(color = dataColor),
                    lineDrawer = SolidLineDrawer(color = dataColor.copy(alpha = 0.8f))
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
            LazyColumn() {
                items(actions) { action ->
                    ListItem(item = action)
                }
            }
        }
    }
}


@Composable
private fun getSolutionText(
    type: Action.Type,
    timeInterval: Int
): AnnotatedString {
    val timeLabel = stringArrayResource(id = R.array.time_intervals)[timeInterval]
    return buildAnnotatedString {
        if (type == Action.Type.NO_INPUT)
            return AnnotatedString(
                stringResource(
                    id = com.techspark.core.R.string.no_feeling_found,
                    timeLabel
                )
            )
        val adj = stringResource(id = type.adjective)
        val summary = stringResource(
            id = com.techspark.core.R.string.feeling_summary_first_part,
            timeLabel,
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

@Preview
@Composable
private fun PreviewStatisticsScreen() {
    IFeelTheme {
        val data = DataHelper.getFeelings(System.currentTimeMillis())

        val barChartData = BarChartData(
            mutableListOf(
                BarChartData.Bar(10f, Color.Gray, "bar 1"),
                BarChartData.Bar(20f, Color.Green, "bar 2"),
                BarChartData.Bar(30f, Color.Blue, "bar 3")
            )
        )
        val pieChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(25f, Color.Red),
                PieChartData.Slice(42f, Color.Blue),
                PieChartData.Slice(23f, Color.Green)
            )
        )
        val lineChartData = LineChartData(
            points = listOf(
                LineChartData.Point(10f, "1"),
                LineChartData.Point(7.5f, "2"), LineChartData.Point(15f, "3")
            )
        )
        StatisticsScreen(
            actions = data,
            selectedType = Action.Type.NO_INPUT,
            selectedInterval = 0,
            largestActionType = Action.Type.NO_INPUT,
            barChartData = barChartData,
            pieChartData = pieChartData,
            lineChartData = lineChartData
        )
    }
}