package com.techspark.core.model

import java.util.concurrent.TimeUnit

object FakeDataHelper {

    /**
     * Get placeholder data to setup ui
     */
    fun getFeelings(startDate: Long):List<Action>{
        return listOf(
            Action(1,"action 1 ",startDate, startDate+TimeUnit.HOURS.toMillis(1),Action.Type.HAPPINESS, ""),
            Action(2,"action 2 ",startDate, startDate+TimeUnit.HOURS.toMillis(2),Action.Type.ANXIETY, ""),
            Action(3,"action 3 ",startDate, startDate+TimeUnit.HOURS.toMillis(3),Action.Type.ANGER, ""),
            Action(4,"action 4 ",startDate, startDate+TimeUnit.HOURS.toMillis(4),Action.Type.SADNESS, ""),
            Action(5,"action 5 ",startDate, startDate+TimeUnit.HOURS.toMillis(5),Action.Type.DEPRESSED, ""),
        )
    }
}