package com.techspark.core.data

import android.util.Log
import com.techspark.core.common.DateUtil
import com.techspark.core.data.db.ActionDao
import com.techspark.core.data.db.AppDatabase
import com.techspark.core.model.Day
import com.techspark.core.model.Action
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class IFeelRepository @Inject constructor(private val database: AppDatabase) : Repository {

    private val db: ActionDao
        get() = database.actionDao()


//    override fun getDays(startDate: Long):Flow<Resource<List<Day>>>{
//        return flow{
//            emit(Resource.Loading(true))
//            val data = db.getAll()
//            val yesterday = startDate - TimeUnit.DAYS.toMillis(1L)
//            with(data.iterator()){
//                while(hasNext()){
//                    val actiom= next()
//                }
//            }
////            emit(Resource.Success(data))
//        }
//    }
//
//    override fun getDay(dayId: Long): StateFlow<Day> {
//
//        return MutableStateFlow<Day>(_data.value.find { it.startDate == dayId }!!)
//    }
//
//    override fun getAction(actionId: Long): StateFlow<Action> {
//
//        return MutableStateFlow<Action>(_data.value.flatMap { it.actions }
//            .find { it.id == actionId }!!)
//    }

    companion object {
        /**
         * Sort Actions according to the days they happened on.
         * @param actions actions to be sorted
         * @param endDate the last date we want to consider
         * @param startDate the first date we want to consider.
         */
        fun sortActionsInDays(actions: List<Action>, endDate: Long, startDate:Long = 0 ): List<Day> {
            //If no actions are available then we have an empty day with the same date
            if (actions.isEmpty() && startDate==0L)
                return listOf(Day( DateUtil.getStartOfDay(endDate), listOf()))

            /**
             * There might be days where no actions were added, so to decide, how many days
             * we have,
             * first we find the first day and last day of the intervals
             * second, we add an empty list for each respective day
             * and later on by iterating through the list we just add actions belonging to each day
             *
             */
            var lastDay = DateUtil.getStartOfDay(endDate)
            //if a start date is not set choose the earliest starting date of the actions
            val start = if(startDate>0) startDate else actions.minBy { it.startDate }.startDate
            val firstDay = DateUtil.getStartOfDay(start)

            val dayToActionMap = HashMap<Long, MutableList<Action>>()
            while (lastDay>=firstDay){
                dayToActionMap[lastDay] = emptyList<Action>().toMutableList()
                lastDay = DateUtil.getYesterdayDate(lastDay)
            }
            with(actions.iterator()) {
                while (hasNext()) {
                    val action = next()
                    val actionDayId = DateUtil.getStartOfDay(action.startDate)
                    if(dayToActionMap.containsKey(actionDayId))
                        dayToActionMap[actionDayId] = dayToActionMap[actionDayId]!!.apply { add(action) }
                }
            }

            return dayToActionMap.map { (id, items)-> Day(id, items) }.sortedByDescending { it.startDate }
        }
    }

    override fun getAllDays(endDate: Long): Flow<Resource<List<Day>>> {
        return flow {
            db.getAll().collect { list ->
                emit(Resource.Loading(true))
                val data = list
                val days = sortActionsInDays( data,endDate)
                emit(Resource.Success(days))
                emit(Resource.Loading(false))

            }
        }
    }

    override fun getAllActionsInInterval(
        startDate: Long,
        endDate: Long
    ): Flow<Resource<List<Action>>> {
        return flow {
            emit(Resource.Loading(true))
            val data = db.getAllInInterval(startDate, endDate).first()
            emit(Resource.Success(data))
            emit(Resource.Loading(false))
        }
    }

    override fun getAllDaysInInterval(startDate: Long, endDate: Long): Flow<Resource<List<Day>>> {
        return flow {
            db.getAllInInterval(startDate, endDate).collect { list ->
                emit(Resource.Loading(true))
                val days = sortActionsInDays(list, endDate, startDate)
                emit(Resource.Success(days))
                emit(Resource.Loading(false))
            }
        }
    }

    override fun getAllActionsInIntervalWithType(startDate: Long, endDate: Long, type: Action.Type):
            Flow<Resource<List<Day>>> {
        return flow {
            emit(Resource.Loading(true))
            val data = db.getAllInIntervalWithType(startDate, endDate, type).first()
            val days = sortActionsInDays(data, endDate, startDate)
            emit(Resource.Success(days))
            emit(Resource.Loading(false))
        }
    }

    override fun getAction(actionId: Long): Flow<Resource<Action>> {
        return flow {
            emit(Resource.Loading(true))
            val data = db.getActionById(actionId)
            emit(Resource.Success(data))
            emit(Resource.Loading(false))
        }
    }

    override fun addAction(action: Action) {
        runBlocking {
            val id = db.add(action)
            Log.e("repo", "$id")
        }
    }

    override fun updateAction(action: Action) {
        runBlocking {
            db.update(action)
        }
    }

    override fun getOngoingAction(startDate: Long): Resource<Action> {
        val a =runBlocking {
             Resource.Success(db.getOngoingAction(startDate))
        }
        return a
    }

    override fun deleteActionById(actionId: Long) {
        runBlocking {
            db.delete(actionId)
        }
    }

    override fun getDay(dayId: Long): Flow<Resource<Day>> {
        return flow {
            db.getAllInInterval(dayId, DateUtil.getTomorrowDate(dayId)).collect { list ->
                emit(Resource.Loading(true))
                val days = sortActionsInDays(list, dayId).first()
                emit(Resource.Success(days))
                emit(Resource.Loading(false))
            }
        }
    }

    override fun getTotalActionCount(): Int {
        return runBlocking {
            db.getTotalCount()
        }
    }
}