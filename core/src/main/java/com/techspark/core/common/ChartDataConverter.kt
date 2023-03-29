package com.techspark.core.common

import android.content.Context
import com.techspark.core.chart.bar.BarChartData
import com.techspark.core.chart.line.LineChart
import com.techspark.core.chart.line.LineChartData
import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.model.Action
import com.techspark.core.model.Day
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Converts model item data to it's appropriate
 * value needed by each chart type
 */
object ChartDataConverter {

    fun actionToBarData(context: Context, actions: List<Action>): BarChartData {
        val barData = actions.groupBy { it.type }
            .map { (type, actions) ->
                BarChartData.Bar(
                    actions.sumOf { it.duration }.toFloat(),
                    type.color,
                    context.getString(type.title)
                )
            }
        return BarChartData(barData)
    }

    fun actionToPieData(actions: List<Action>): PieChartData {
        val barData = actions.groupBy { it.type }
            .map { (type, actions) ->
                PieChartData.Slice(
                    actions.sumOf { it.duration }.toFloat(),
                    type.color
                )
            }
        return PieChartData(barData)
    }

    fun actionToLineData(days: List<Day>, type:Action.Type): LineChartData {
        val barData = days.map { day ->
            val typeDuration = day.feelingDuration[type]?:1f
            val duration = if(typeDuration==0) 1f else typeDuration.toFloat()
            LineChartData.Point(
                duration,
                SimpleDateFormat("EEE").format(day.startDate))
        }
//        val data = LineChartData(listOf(
//            LineChartData.Point(5f,"1"),
//            LineChartData.Point(10f,"2")
//            ,LineChartData.Point(7.5f, "3")
//            ,LineChartData.Point(15f,"4")
//        ))

//        return data;
        return LineChartData(barData)
    }
}