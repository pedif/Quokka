package com.techspark.core.test

import java.util.concurrent.TimeUnit

object DateHelper {

    fun getDateByDay(startDate:Long, dayAmount: Int):Long{

        return startDate + (dayAmount * TimeUnit.DAYS.toMillis(1L))
    }

    fun getDateByHour(startDate:Long, hourAmount: Int):Long{

        return startDate + (hourAmount * TimeUnit.HOURS.toMillis(1L))
    }
}