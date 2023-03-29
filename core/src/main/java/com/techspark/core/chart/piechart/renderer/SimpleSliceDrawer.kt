package com.techspark.core.chart.piechart.renderer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.chart.piechart.PieChartData.Slice
import com.techspark.core.theme.IFeelTheme

class SimpleSliceDrawer(private val sliceThickness: Float = 25f) : SliceDrawer {
  init {
    require(sliceThickness in 10f..100f) {
      "Thickness of $sliceThickness must be between 10-100"
    }
  }

  private val sectionPaint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
  }

  override fun drawSlice(
    canvas: Canvas,
    area: Size,
    startAngle: Float,
    sweepAngle: Float,
    slice: Slice
  ) {
    val sliceThickness = calculateSectorThickness(area = area)
    val drawableArea = calculateDrawableArea(area = area)

    canvas.drawArc(
      rect = drawableArea,
      paint = sectionPaint.apply {
        color = slice.color
        strokeWidth = sliceThickness
      },
      startAngle = startAngle,
      sweepAngle = sweepAngle,
      useCenter = true
    )
  }

  private fun calculateSectorThickness(area: Size): Float {
    val minSize = minOf(area.width, area.height)

    return minSize * (sliceThickness / 200f)
  }

  private fun calculateDrawableArea(area: Size): Rect {
    val sliceThicknessOffset =
      calculateSectorThickness(area = area) / 2f
    val offsetHorizontally = (area.width - area.height) / 2f

    return Rect(
      left = sliceThicknessOffset + offsetHorizontally,
      top = sliceThicknessOffset,
      right = area.width - sliceThicknessOffset - offsetHorizontally,
      bottom = area.height - sliceThicknessOffset
    )
  }
}

@Composable
@Preview(backgroundColor = 255)
fun SliceDrawerPreview(){
  IFeelTheme {
    Canvas(modifier = Modifier.
    size(100.dp,100.dp)) {
      drawIntoCanvas {
          val slice= Slice(40f, Color.Green)
          SimpleSliceDrawer().drawSlice(
            canvas = drawContext.canvas,
            area = size,
            startAngle = 0f,
            sweepAngle = 90f,
            slice = slice
          )
      }
    }
  }
}