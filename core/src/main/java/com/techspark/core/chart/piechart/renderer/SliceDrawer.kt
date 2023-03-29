package com.techspark.core.chart.piechart.renderer

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.techspark.core.chart.piechart.PieChartData.Slice

interface SliceDrawer {
  fun drawSlice(
    canvas: Canvas,
    area: Size,
    startAngle: Float,
    sweepAngle: Float,
    slice: Slice
  )
}