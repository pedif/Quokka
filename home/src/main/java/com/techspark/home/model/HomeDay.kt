package com.techspark.home.model

import com.techspark.core.model.Action
import com.techspark.core.model.Day
import java.text.SimpleDateFormat
import java.util.*


/**
 * Data class representing a day in Home screen based on
 * @see com.techspark.core.model.Day
 */
data class HomeDay(val id:Long, val dayName: String, val largestActionType: Action.Type, val leastActionType:Action.Type) {

    companion object {
        fun fromDay(day: Day): HomeDay {
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            val d = Date(day.startDate)
            val dayName: String = sdf.format(d)
            val largestActionType = day.getTheMostFeltFeelingType()
            val leastActionType = day.getLeastFeltFeelingType()
            return HomeDay(day.startDate, dayName, largestActionType, leastActionType)
        }
    }
}
