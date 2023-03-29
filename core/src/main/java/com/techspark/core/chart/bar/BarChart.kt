package com.techspark.core.chart.bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.techspark.core.chart.bar.BarChartUtils.axisAreas
import com.techspark.core.chart.bar.BarChartUtils.barDrawableArea
import com.techspark.core.chart.bar.BarChartUtils.forEachWithArea
import com.techspark.core.chart.bar.renderer.bar.BarDrawer
import com.techspark.core.chart.bar.renderer.bar.SimpleBarDrawer
import com.techspark.core.chart.bar.renderer.label.LabelDrawer
import com.techspark.core.chart.bar.renderer.label.SimpleValueDrawer
import com.techspark.core.chart.bar.renderer.xaxis.SimpleXAxisDrawer
import com.techspark.core.chart.bar.renderer.xaxis.XAxisDrawer
import com.techspark.core.chart.bar.renderer.yaxis.SimpleYAxisDrawer
import com.techspark.core.chart.bar.renderer.yaxis.YAxisDrawer
import com.techspark.core.chart.util.animation.simpleChartAnimation
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.*

@Composable
fun BarChart(
    barChartData: BarChartData,
    modifier: Modifier = Modifier,
    animation: AnimationSpec<Float> = simpleChartAnimation(),
    barDrawer: BarDrawer = SimpleBarDrawer(),
    xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(),
    yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer(),
    labelDrawer: LabelDrawer = SimpleValueDrawer()
) {
    val transitionAnimation = remember(barChartData.bars) { Animatable(initialValue = 0f) }

    LaunchedEffect(barChartData.bars) {
        transitionAnimation.animateTo(1f, animationSpec = animation)
    }

    val progress = transitionAnimation.value
    val cornerSize = dimensionResource(id = MARGIN_MEDIUM).value
    Canvas(modifier = modifier
        .fillMaxSize()
        .drawBehind {
            drawIntoCanvas { canvas ->
                val (xAxisArea, yAxisArea) = axisAreas(
                    drawScope = this,
                    totalSize = size,
                    xAxisDrawer = xAxisDrawer,
                    labelDrawer = labelDrawer
                )
                val barDrawableArea = barDrawableArea(xAxisArea)

                // Draw yAxis line.
                yAxisDrawer.drawAxisLine(
                    drawScope = this,
                    canvas = canvas,
                    drawableArea = yAxisArea
                )

                // Draw xAxis line.
                xAxisDrawer.drawAxisLine(
                    drawScope = this,
                    canvas = canvas,
                    drawableArea = xAxisArea
                )
                // Draw each bar.
                barChartData.forEachWithArea(
                    this,
                    barDrawableArea,
                    progress,
                    labelDrawer,
                ) { barArea, bar ->
                    barDrawer.drawBar(
                        drawScope = this,
                        canvas = canvas,
                        barArea = barArea,
                        bar = bar,
                        cornerSize = cornerSize
                    )
                }
            }
        }
    ) {
        /**
         *  Typically we could draw everything here, but because of the lack of canvas.drawText
         *  APIs we have to use Android's `nativeCanvas` which seems to be drawn behind
         *  Compose's canvas.
         */
        drawIntoCanvas { canvas ->
            val (xAxisArea, yAxisArea) = axisAreas(
                drawScope = this,
                totalSize = size,
                xAxisDrawer = xAxisDrawer,
                labelDrawer = labelDrawer
            )
            val barDrawableArea = barDrawableArea(xAxisArea)

            barChartData.forEachWithArea(
                this,
                barDrawableArea,
                progress,
                labelDrawer
            ) { barArea, bar ->
                labelDrawer.drawLabel(
                    drawScope = this,
                    canvas = canvas,
                    label = bar.label,
                    barArea = barArea,
                    xAxisArea = xAxisArea
                )
            }

            yAxisDrawer.drawAxisLabels(
                drawScope = this,
                canvas = canvas,
                minValue = barChartData.minYValue,
                maxValue = barChartData.maxYValue,
                drawableArea = yAxisArea
            )
        }
    }
}

@Preview(heightDp = 300)
@Composable
fun PreviewBarChart() {
    IFeelTheme {
        val data = BarChartData(
            mutableListOf(
                BarChartData.Bar(10f, Color.Gray, "bar 1"),
                BarChartData.Bar(20f, Color.Green, "bar 2"),
                BarChartData.Bar(30f, Color.Blue, "bar 3")
            )
        )
        BarChart(barChartData = data)
    }
}