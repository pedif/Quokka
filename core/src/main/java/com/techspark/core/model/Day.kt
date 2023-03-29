package com.techspark.core.model

import android.content.Context
import com.techspark.core.R
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action.*
import java.util.concurrent.TimeUnit
import kotlin.math.min

data class Day(val startDate: Long, val actions: List<Action>) {

    val feelingDuration = hashMapOf(
        Type.HAPPINESS to 0,
        Type.ANGER to 0,
        Type.ANXIETY to 0,
        Type.SADNESS to 0,
        Type.DEPRESSED to 0,
        Type.NO_INPUT to 0
    )

    /**
     * In the init phase we calculate all the taken time by all the actions and categorize
     * them because this info will be required everywhere so there would be no need to calculate
     * it after initiation
     */
    init {
        var totalDuration = 0
        actions.forEach { action ->
            feelingDuration[action.type] = (feelingDuration[action.type] ?: 0) + action.duration
            totalDuration += action.duration
        }
        //The unknown time is the time the user has not put in a day which is
        //a day minus all the inputted time
        feelingDuration[Type.NO_INPUT] = TimeUnit.DAYS.toMinutes(1L).toInt() - (totalDuration)

    }

    /**
     * Get duration label for each item type
     * @see Type
     */
    fun getTypeDurationLabel(context: Context, type: Type): String {

        var hourLabel = Pair<String, String>("", "")
        var minuteLabel = Pair<String, String>("", "")
        var percentage = 0
        feelingDuration[type]?.let { duration ->
            if (duration < 60) {
                minuteLabel = Pair(
                    duration.toString(),
                    context.resources.getQuantityString(R.plurals.minute_label, duration)
                )
            } else {
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
            percentage = DateUtil.getPercentagePerDay(duration)
        }
        return String.format(
            context.getString(R.string.action_duration_label_full),
            hourLabel.first, hourLabel.second, minuteLabel.first, minuteLabel.second, percentage
        )
    }

    fun getTheMostFeltFeelingType(): Type {
        //The default type is no input
        var type = Type.NO_INPUT

        //sort the feeling duration so we can know which feeling has taken place the most
        var sortedList = feelingDuration.toList()
            .filter { (_, value) -> value > 0 }
            .sortedByDescending { (_, value) -> value }

        //if no feelings were included then nothing has been inserted yet
        return if (sortedList.size <= 1)
            type
        //if feelings are there but no input has the most duration we take the second largest feeling
        else if (sortedList[0].first == Type.NO_INPUT)
            sortedList[1].first
        else
            sortedList[0].first
    }

    fun getLeastFeltFeelingType(): Type {
        return if (feelingDuration[Type.NO_INPUT] == TimeUnit.DAYS.toMinutes(1L).toInt())
            Type.NO_INPUT
        else {
            var min = Integer.MAX_VALUE;
            var target = Type.NO_INPUT
            feelingDuration.forEach { (type, duration) ->
                if (type != Type.NO_INPUT) {
                    if (duration < min) {
                        target = type
                        min = min(min, duration)
                    }
                }
            }
            return target
        }
    }
}
