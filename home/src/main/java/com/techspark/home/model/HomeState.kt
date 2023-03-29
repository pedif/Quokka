package com.techspark.home.model

import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.model.Action

data class HomeState(
    val ongoingAction:Action? = null,
    val yesterdayLargestAction:Action.Type = Action.Type.NO_INPUT,
    val dayList:List<HomeDay> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val chartData:PieChartData = PieChartData(mutableListOf()),
    val askForNotificationPermission:Boolean = false
)
