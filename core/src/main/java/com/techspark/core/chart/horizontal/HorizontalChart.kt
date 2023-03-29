package com.techspark.core.chart.horizontal

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@Composable
 fun HorizontalChart(items:List<Pair<Action.Type, Int>>) {
    val scrollState = rememberScrollState()

//    val hours = (0..items.maxOf { item -> DateUtil.minuteToHour(item.second) }).toList()
    val hours = (0..23).toList()
    TimeGraph(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.surface),
        dayItemsCount = items.size,
        hoursHeader = {
            HoursHeader(hours,MaterialTheme.colorScheme.primary)
        },
        dayLabel = { index ->
            val data = items[index].first
            DayLabel(stringResource(id = data.title))
        },
        bar = { index ->
            // We have access to Modifier.timeGraphBar() as we are now in TimeGraphScope
            val data = items[index]
            SleepBar(
                typePair = data,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .timeGraphBar(
                        start = data.second.toLong(),
                        end = data.second.toLong() + TimeUnit.MINUTES.toMillis(data.second.toLong()),
                        hours = hours,
                    )
            )
        }
    )
}

@Composable
private fun DayLabel(label: String) {
    Text(
        label,
        Modifier
            .height(24.dp)
            .padding(start = 8.dp, end = 24.dp),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HoursHeader(hours: List<Int>, baseColor:Color) {
    Row(
        Modifier
            .padding(bottom = 16.dp)
            .drawBehind {
                val brush = Brush.linearGradient(listOf(baseColor.copy(alpha = 0.6f), baseColor))
                drawRoundRect(
                    brush,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            }
    ) {
        hours.forEach {
            Text(
                text = "$it",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(50.dp)
                    .padding(vertical = 4.dp),
                style =  MaterialTheme.typography.labelMedium
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeGraph(
    hoursHeader: @Composable () -> Unit,
    dayItemsCount: Int,
    dayLabel: @Composable (index: Int) -> Unit,
    bar: @Composable TimeGraphScope.(index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayLabels = @Composable { repeat(dayItemsCount) { dayLabel(it) } }
    val bars = @Composable { repeat(dayItemsCount) { TimeGraphScope.bar(it) } }
    Layout(
        contents = listOf(hoursHeader, dayLabels, bars),
        modifier = modifier.padding( 16.dp)
    ) {
            (hoursHeaderMeasurables, dayLabelMeasurables, barMeasureables),
            constraints,
        ->
        require(hoursHeaderMeasurables.size == 1) {
            "hoursHeader should only emit one composable"
        }
        val hoursHeaderPlaceable = hoursHeaderMeasurables.first().measure(constraints)

        val dayLabelPlaceables = dayLabelMeasurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }

        var totalHeight = hoursHeaderPlaceable.height

        val barPlaceables = barMeasureables.map { measurable ->
            val barParentData = measurable.parentData as TimeGraphParentData
            val barWidth = (barParentData.duration * hoursHeaderPlaceable.width).roundToInt()

            val barPlaceable = measurable.measure(
                constraints.copy(
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
            totalHeight += barPlaceable.height
            barPlaceable
        }

        val totalWidth = dayLabelPlaceables.first().width + hoursHeaderPlaceable.width

        layout(totalWidth, totalHeight) {
            val xPosition = dayLabelPlaceables.maxByOrNull { p -> p.width }!!.width
            var yPosition = hoursHeaderPlaceable.height

            hoursHeaderPlaceable.place(xPosition, 0)

            barPlaceables.forEachIndexed { index, barPlaceable ->
                val barParentData = barPlaceable.parentData as TimeGraphParentData
                val barOffset = (barParentData.offset * hoursHeaderPlaceable.width).roundToInt()

                barPlaceable.place(xPosition + barOffset, yPosition)
                // the label depend on the size of the bar content - so should use the same y
                val dayLabelPlaceable = dayLabelPlaceables[index]
                dayLabelPlaceable.place(x = 0, y = yPosition)

                yPosition += barPlaceable.height
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
object TimeGraphScope {
    @Stable
    fun Modifier.timeGraphBar(
        start: Long,
        end: Long,
        hours: List<Int>,
    ): Modifier {

//        val durationInHours = TimeUnit.MILLISECONDS.toHours(end-start).toFloat()
        val durationInHours = TimeUnit.MILLISECONDS.toMinutes(end-start)/60f
//durationInHours / hours.size
        return then(
            TimeGraphParentData(
                duration = durationInHours/hours.size,
                offset = 0f / hours.size
            )
        )
    }
}

class TimeGraphParentData(
    val duration: Float,
    val offset: Float,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@TimeGraphParentData
}


@Composable
@Preview
private fun ChartPreview(){
    IFeelTheme {

        HorizontalChart(items =DataHelper.getDays()[0].feelingDuration.toList().sortedBy { pair-> pair.first.ordinal } )
    }
}