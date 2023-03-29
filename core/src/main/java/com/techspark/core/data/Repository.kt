package com.techspark.core.data

import com.techspark.core.model.Day
import com.techspark.core.model.Action
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllDays(endDate: Long): Flow<Resource<List<Day>>>
    fun getAllActionsInInterval(startDate: Long, endDate: Long): Flow<Resource<List<Action>>>
    fun getAction(actionId: Long): Flow<Resource<Action>>
    fun addAction(action: Action)
    fun updateAction(action: Action)
    fun deleteActionById(actionId: Long)
    fun getDay(dayId: Long): Flow<Resource<Day>>
    fun getAllActionsInIntervalWithType(startDate: Long, endDate: Long, type:Action.Type ):Flow<Resource<List<Day>>>
    fun getAllDaysInInterval(startDate: Long, endDate: Long): Flow<Resource<List<Day>>>
    fun getOngoingAction(startDate: Long):Resource<Action>
    fun getTotalActionCount(): Int

}