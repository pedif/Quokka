package com.techspark.statistics

import com.techspark.core.chart.bar.BarChartData
import com.techspark.core.chart.line.LineChartData
import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.model.Action

data class StatisticsState(
    val actions: List<Action> = mutableListOf(),
    val selectedType: Action.Type = Action.Type.NO_INPUT,
    val selectedInterval:StatisticsViewModel.TimeInterval = StatisticsViewModel.TimeInterval.LAST_WEEK,
    val largestType:Action.Type = Action.Type.NO_INPUT,
    val barChartData: BarChartData?=null,
    val pieChartData: PieChartData?=null,
    val lineChartData: LineChartData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
