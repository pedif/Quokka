package com.techspark.core.chart.line

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.chart.line.LineChartUtils.calculateDrawableArea
import com.techspark.core.chart.line.LineChartUtils.calculateFillPath
import com.techspark.core.chart.line.LineChartUtils.calculateLinePath
import com.techspark.core.chart.line.LineChartUtils.calculatePointLocation
import com.techspark.core.chart.line.LineChartUtils.calculateXAxisDrawableArea
import com.techspark.core.chart.line.LineChartUtils.calculateXAxisLabelsDrawableArea
import com.techspark.core.chart.line.LineChartUtils.calculateYAxisDrawableArea
import com.techspark.core.chart.line.LineChartUtils.withProgress
import com.techspark.core.chart.line.renderer.line.LineDrawer
import com.techspark.core.chart.line.renderer.line.LineShader
import com.techspark.core.chart.line.renderer.line.NoLineShader
import com.techspark.core.chart.line.renderer.line.SolidLineDrawer
import com.techspark.core.chart.line.renderer.point.FilledCircularPointDrawer
import com.techspark.core.chart.line.renderer.point.PointDrawer
import com.techspark.core.chart.line.renderer.xaxis.SimpleXAxisDrawer
import com.techspark.core.chart.line.renderer.xaxis.XAxisDrawer
import com.techspark.core.chart.line.renderer.yaxis.SimpleYAxisDrawer
import com.techspark.core.chart.line.renderer.yaxis.YAxisDrawer
import com.techspark.core.chart.util.animation.simpleChartAnimation
import com.techspark.core.theme.IFeelTheme

@Composable
fun LineChart(
  lineChartData: LineChartData,
  modifier: Modifier = Modifier,
  animation: AnimationSpec<Float> = simpleChartAnimation(),
  pointDrawer: PointDrawer = FilledCircularPointDrawer(),
  lineDrawer: LineDrawer = SolidLineDrawer(),
  lineShader: LineShader = NoLineShader,
  xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(),
  yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer(),
  horizontalOffset: Float = 5f
) {
  check(horizontalOffset in 0f..25f) {
    "Horizontal offset is the % offset from sides, " +
      "and should be between 0%-25%"
  }

  val transitionAnimation = remember(lineChartData.points) { Animatable(initialValue = 0f) }

  LaunchedEffect(lineChartData.points) {
    transitionAnimation.snapTo(0f)
    transitionAnimation.animateTo(1f, animationSpec = animation)
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    drawIntoCanvas { canvas ->
      val yAxisDrawableArea = calculateYAxisDrawableArea(
        xAxisLabelSize = xAxisDrawer.requiredHeight(this),
        size = size
      )
      val xAxisDrawableArea = calculateXAxisDrawableArea(
        yAxisWidth = yAxisDrawableArea.width,
        labelHeight = xAxisDrawer.requiredHeight(this),
        size = size
      )
      val xAxisLabelsDrawableArea = calculateXAxisLabelsDrawableArea(
        xAxisDrawableArea = xAxisDrawableArea,
        offset = horizontalOffset
      )
      val chartDrawableArea = calculateDrawableArea(
        xAxisDrawableArea = xAxisDrawableArea,
        yAxisDrawableArea = yAxisDrawableArea,
        size = size,
        offset = horizontalOffset
      )

      // Draw the chart line.
      lineDrawer.drawLine(
        drawScope = this,
        canvas = canvas,
        linePath = calculateLinePath(
          drawableArea = chartDrawableArea,
          lineChartData = lineChartData,
          transitionProgress = transitionAnimation.value
        )
      )

      lineShader.fillLine(
        drawScope = this,
        canvas = canvas,
        fillPath = calculateFillPath(
          drawableArea = chartDrawableArea,
          lineChartData = lineChartData,
          transitionProgress = transitionAnimation.value
        )
      )

      lineChartData.points.forEachIndexed { index, point ->
        withProgress(
          index = index,
          lineChartData = lineChartData,
          transitionProgress = transitionAnimation.value
        ) {
          pointDrawer.drawPoint(
            drawScope = this,
            canvas = canvas,
            center = calculatePointLocation(
              drawableArea = chartDrawableArea,
              lineChartData = lineChartData,
              point = point,
              index = index
            )
          )
        }
      }

      // Draw the X Axis line.
      xAxisDrawer.drawAxisLine(
        drawScope = this,
        drawableArea = xAxisDrawableArea,
        canvas = canvas
      )

      xAxisDrawer.drawAxisLabels(
        drawScope = this,
        canvas = canvas,
        drawableArea = xAxisLabelsDrawableArea,
        labels = lineChartData.points.map { it.label }
      )

      // Draw the Y Axis line.
      yAxisDrawer.drawAxisLine(
        drawScope = this,
        canvas = canvas,
        drawableArea = yAxisDrawableArea
      )

      yAxisDrawer.drawAxisLabels(
        drawScope = this,
        canvas = canvas,
        drawableArea = yAxisDrawableArea,
        minValue = lineChartData.minYValue,
        maxValue = lineChartData.maxYValue
      )
    }
  }
}

@Composable
@Preview
private fun PreviewChart(){
  IFeelTheme {
    val data = LineChartData(listOf(
      LineChartData.Point(5f,"1"),
      LineChartData.Point(10f,"2")
    ,LineChartData.Point(7.5f, "3")
    ,LineChartData.Point(15f,"4")
    ))
    LineChart(lineChartData = data, modifier = Modifier.height(250.dp))
  }
}