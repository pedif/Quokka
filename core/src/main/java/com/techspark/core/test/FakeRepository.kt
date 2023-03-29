package com.techspark.core.test

import androidx.annotation.VisibleForTesting
import com.techspark.core.common.DateUtil
import com.techspark.core.data.IFeelRepository
import com.techspark.core.data.Repository
import com.techspark.core.data.Resource
import com.techspark.core.model.Action
import com.techspark.core.model.Day
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class FakeRepository : Repository {
     val items = mutableListOf<Action>()
    override fun getAllDays(endDate: Long): Flow<Resource<List<Day>>> {
        return flow {
            emit(Resource.Success(IFeelRepository.sortActionsInDays(items, endDate)))
        }
    }

    override fun getAllActionsInInterval(
        startDate: Long,
        endDate: Long
    ): Flow<Resource<List<Action>>> {
        TODO("Not yet implemented")
    }

    override fun getAction(actionId: Long): Flow<Resource<Action>> {

        return flow {
            emit(Resource.Success(items.find { it.id == actionId }))
        }
    }


    override fun addAction(action: Action) {
        action.id = (items.size + 1).toLong()
        items.add(action)
    }

    override fun updateAction(action: Action) {
        val id = items.indexOf(items.find { it.id == action.id })
        items[id] = action
    }

    override fun deleteActionById(actionId: Long) {
        TODO("Not yet implemented")
    }

    override fun getDay(dayId: Long): Flow<Resource<Day>> {
        return flow {
            val day = Day(dayId, items.filter {
                it.startDate >= dayId &&
                        it.endDate < DateUtil.getTomorrowDate(dayId)
            })
            emit(Resource.Success(day));
        }
    }

    override fun getAllActionsInIntervalWithType(
        startDate: Long,
        endDate: Long,
        type: Action.Type
    ): Flow<Resource<List<Day>>> {
        TODO("Not yet implemented")
    }


    override fun getAllDaysInInterval(startDate: Long, endDate: Long): Flow<Resource<List<Day>>> {
        return flow {
            val data =
                IFeelRepository.sortActionsInDays(items.filter { it.startDate >= startDate && it.endDate < endDate }
                    .sortedByDescending { it.id }, endDate, startDate)
            emit(Resource.Success(data))
        }
    }

    override fun getOngoingAction(startDate: Long): Resource<Action> {
        return Resource.Success(items.firstOrNull { it.endDate == 0L })
    }

    override fun getTotalActionCount(): Int {
        return items.size
    }


    fun getAllDaysSync(currentTime: Long): List<Day> {

        return IFeelRepository.sortActionsInDays(items.sortedByDescending { it.id }, currentTime)
    }

    fun getAllActionsInIntervalSync(startDate: Long, endDate: Long): List<Action> {
        return items.filter { it.startDate >= startDate && it.endDate <= endDate }
    }

    fun getActionSync(actionId: Long): Action? {
        return items.find { it.id == actionId }
    }


}