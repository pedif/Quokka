package com.techspark.core.chart.bar.renderer.bar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.techspark.core.chart.bar.BarChartData

class SimpleBarDrawer : BarDrawer {
  private val barPaint = Paint().apply {
    this.isAntiAlias = true
  }

  override fun drawBar(
    drawScope: DrawScope,
    canvas: Canvas,
    barArea: Rect,
    bar: BarChartData.Bar,
    cornerSize: Float
  ) {
    val path = Path()
    val corner = CornerRadius(cornerSize, cornerSize)
    val rec = RoundRect(barArea, topLeft = corner, topRight = corner)
    path.addRoundRect(rec)
    canvas.drawPath(path, barPaint.apply {
      color = bar.color
    })
  }
}