package com.techspark.core.common

import com.techspark.core.model.Day
import com.techspark.core.model.Action
import java.util.concurrent.TimeUnit

object DataHelper {

    /**
     * Get placeholder data to setup ui
     */
    fun getFeelings(startDate: Long): List<Action> {
        return listOf(
            Action(1,"action 1 ",startDate, startDate+TimeUnit.HOURS.toMillis(1),Action.Type.HAPPINESS, ""),
            Action(2,"action 2 ",startDate, startDate+TimeUnit.HOURS.toMillis(2),Action.Type.ANXIETY, ""),
            Action(3,"action 3 ",startDate, startDate+TimeUnit.HOURS.toMillis(3),Action.Type.ANGER, ""),
            Action(4,"action 4 ",startDate, startDate+TimeUnit.HOURS.toMillis(4),Action.Type.SADNESS, ""),
            Action(5,"action 5 ",startDate, startDate+TimeUnit.HOURS.toMillis(5),Action.Type.DEPRESSED, ""),
        )
    }


    fun getDays(lastDate: Long  = System.currentTimeMillis()): List<Day> {
        val days = mutableListOf<Day>()
        var currentDate = lastDate
//        days.add(Day(currentDate, listOf()))
        currentDate -= TimeUnit.DAYS.toMillis(1)
        for (i in 1..7) {
            days.add(Day(currentDate, getFeelings(currentDate)))
            currentDate -= TimeUnit.DAYS.toMillis(1)
        }
        return days
    }
}