package com.techspark.core.common

import android.content.Context
import com.techspark.core.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DateUtil {

    /**
     * Get the duration percentage in a day
     */
    fun getPercentagePerDay(duration: Int): Int {
        return ((duration * 100) / TimeUnit.DAYS.toMinutes(1)).toInt()
    }

    fun minuteToHour(minutes: Int) = TimeUnit.MINUTES.toHours(minutes.toLong()).toInt() + 1

    fun getFieldValue(date: Long, field: Int): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return cal[field]
    }

    fun setField(date: Long, field: Int, value: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        cal[field] = value
        return cal.timeInMillis
    }

    fun getYesterdayDate(date: Long) = date - TimeUnit.DAYS.toMillis(1L)
    fun getTomorrowDate(date: Long) = date + TimeUnit.DAYS.toMillis(1L)
    fun getStartOfDay(date: Long): Long {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = date
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    fun getStartOfToday():Long{
        return getStartOfDay(System.currentTimeMillis())
    }

    /**
     * Get the end of today which would be start of nextday minues 1 milliseconds
     */
    fun getEndOfDay(date:Long):Long{
        val startOfTomorrow = getStartOfDay(date+TimeUnit.DAYS.toMillis(1))
        return startOfTomorrow-1
    }

    fun getDurationLabel(context: Context, duration: Int):String{
        if(duration == 0)
            return context.resources.getString(R.string.feeling_ongoing)
        var hourLabel = Pair<String, String>("0", "")
        var minuteLabel = Pair<String, String>("0", "")
        if (duration < 60) {
            minuteLabel = Pair(
                duration.toString(),
                context.resources.getQuantityString(R.plurals.minute_label, duration)
            )
        } else if(duration %60 == 0) {
            val hour = duration / 60
            hourLabel = Pair(
                hour.toString(),
                context.resources.getQuantityString(R.plurals.hour_label, hour)
            )
        }else{
            val min = duration % 60
            val hour = duration / 60
            minuteLabel = Pair(
                min.toString(),
                context.resources.getQuantityString(R.plurals.minute_label, min)
            )
            hourLabel = Pair(
                hour.toString(),
                context.resources.getQuantityString(R.plurals.hour_label, hour)
            )
        }

        return if(hourLabel.first=="0")
            "${minuteLabel.first} ${minuteLabel.second}"
        else
            "${hourLabel.first} ${hourLabel.second} ${minuteLabel.first} ${minuteLabel.second}"
    }


    /**
     * Set the target date time, to current time
     */
    fun getTodayTimeForTargetDate(target:Long):Long{
        val cal = Calendar.getInstance()
        val now = Calendar.getInstance()
        cal.timeInMillis = target
        cal[Calendar.HOUR_OF_DAY] =now[Calendar.HOUR_OF_DAY]
        cal[Calendar.MINUTE] = now[Calendar.MINUTE]
        return cal.timeInMillis
    }
}

