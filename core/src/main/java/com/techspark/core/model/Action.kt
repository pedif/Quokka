package com.techspark.core.model

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.techspark.core.R
import com.techspark.core.common.*
import com.techspark.core.data.db.ActionTypeConverter
import com.techspark.core.theme.*
import com.techspark.core.util.ANGER_ACTIONS
import com.techspark.core.util.ANXIETY_ACTIONS
import com.techspark.core.util.DEPRESSION_ACTIONS
import com.techspark.core.util.SADNESS_ACTIONS
import java.util.concurrent.TimeUnit

@Entity(tableName = "action")
data class Action(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val title: String,
    var startDate: Long,
    var endDate: Long,
    @TypeConverters(ActionTypeConverter::class)
    var type: Type,
    var comment: String
) {

    constructor() : this(0, "",
        System.currentTimeMillis(), 0L,
        Type.HAPPINESS, "")


    /**
     * Duration for this action in minutes
     */
    @Ignore
    var duration = 0

    /**
     * The day this action belongs too
     */
    @Ignore
    var dayId= 0L

    val color: Color
        get() = type.color

    init {
        duration =
            if (endDate == 0L) 0 else TimeUnit.MILLISECONDS.toMinutes(endDate - startDate).toInt()
        dayId = DateUtil.getStartOfDay(startDate)
    }

    fun updateDuration(){
        duration =
            if (endDate == 0L) 0 else TimeUnit.MILLISECONDS.toMinutes(endDate - startDate).toInt()
    }

    enum class Type(
        val title: Int,
        val color: Color,
        val adjective: Int,
        val solution: Int,
        val link: String,
        val emoji: String
    ) {
        HAPPINESS(
            R.string.enjoyment_title,
            HappinessColor,
            R.string.happiness_adj,
            R.string.happiness_solution,
            "",
            "\uD83D\uDE0A"
        ),
        ANGER(
            R.string.anger_title,
            AngerColor,
            R.string.anger_adj,
            R.string.anger_solution,
            LINK_ANGER,
            "\uD83D\uDE21"
        ),
        ANXIETY(
            R.string.anxiety_title,
            FearColor,
            R.string.anxiety_adj,
            R.string.anxiety_solution,
            LINK_ANXIETY,
            "\uD83D\uDE31"
        ),
        SADNESS(
            R.string.sadness_title,
            SadnessColor,
            R.string.sadness_adj,
            R.string.sadness_solution,
            LINK_STRESS,
            "\uD83D\uDE14"
        ),
        DEPRESSED(
            R.string.depression_title,
            DepressionColor,
            R.string.depression_adj,
            R.string.depression_solution,
            LINK_DEPRESSION,
            "\uD83D\uDE1E"
        ),
        NO_INPUT(
            R.string.no_input_title,
            UnknownColor,
            R.string.no_input_title,
            R.string.no_input_title,
            "",
            ""
        )
    }

    fun getSolution(context: Context):Array<String>{
        return when (type) {
          Type.ANGER -> context.resources.getStringArray (ANGER_ACTIONS)
            Type.ANXIETY -> context.resources.getStringArray (ANXIETY_ACTIONS)
           Type.SADNESS -> context.resources.getStringArray ( SADNESS_ACTIONS)
           Type.DEPRESSED -> context.resources.getStringArray ( DEPRESSION_ACTIONS)
            else -> emptyArray()
        }
    }
    /**
     * Get duration label for this item
     * @see Type
     */
    fun getDurationLabel(context: Context): String {

        return DateUtil.getDurationLabel(context, duration)
    }


    fun setEndByDuration(duration: Int) {
        endDate = if (duration == 0)
            0;
        else{
            val amount = TimeUnit.MINUTES.toMillis(duration.toLong())
            if(endDate ==0L) startDate+amount else endDate+amount
        }
    }

    /**
     * Updates time based on the new starting date
     * The duration of the action should stay the same if we change the starting date
     * hence we need to update enddate eachtime startdate changes
     */
    fun updateTime(startDate: Long){
        this.startDate = startDate
        endDate = startDate + TimeUnit.MINUTES.toMillis(duration.toLong())
    }
}
