package com.techspark.statistics

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techspark.core.common.ChartDataConverter
import com.techspark.core.common.DateUtil
import com.techspark.core.data.Repository
import com.techspark.core.data.Resource
import com.techspark.core.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private var _state by mutableStateOf(StatisticsState())
    val state: StatisticsState
        get() = _state



    var hasFetchedOnce = false
        private set

    init {
        _state = _state.copy(isLoading = true)
    }

    fun onActionTypeSet(context: Context, type: Int = Action.Type.NO_INPUT.ordinal) {
        val actionType = if (type == -1)
            Action.Type.NO_INPUT
        else
            Action.Type.values()[type]
        _state = _state.copy(selectedType = actionType)
        getData(context)
    }

    fun onIntervalSet(context: Context, interval: Int) {
        val intervalType:TimeInterval = when(interval){
            TimeInterval.LAST_YEAR.ordinal ->TimeInterval.LAST_YEAR
            TimeInterval.LAST_MONTH.ordinal->TimeInterval.LAST_MONTH
            else ->
                TimeInterval.LAST_WEEK
        }
        _state=_state.copy(selectedInterval = intervalType)
        getData(context)
    }

    private fun getData(
        context: Context
    ) {
        hasFetchedOnce = true
        viewModelScope.launch {

            val endDate = DateUtil.getStartOfDay(System.currentTimeMillis())
            val startDate = when (state.selectedInterval) {

                TimeInterval.LAST_WEEK ->
                    endDate - TimeUnit.DAYS.toMillis(7)

                TimeInterval.LAST_MONTH ->
                    endDate - TimeUnit.DAYS.toMillis(30)

                TimeInterval.LAST_YEAR ->
                    endDate - TimeUnit.DAYS.toMillis(365)

                else ->
                    endDate - TimeUnit.DAYS.toMillis(7)
            }

            /**
             * If no type is selected get the actions within the interval
             * otherwise get actions of the same type
             */
            if (state.selectedType == Action.Type.NO_INPUT)
                getAllActionsInInterval(context, startDate, endDate)
            else
                getAllActionsInIntervalWithType(startDate, endDate, state.selectedType)
        }

    }

    private suspend fun getAllActionsInInterval(context: Context, startDate: Long, endDate: Long) {
        repository.getAllActionsInInterval(startDate, endDate).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data!!.filter { it.endDate>0 }
                    val barData = ChartDataConverter.actionToBarData(context, data)
                    val pieData = ChartDataConverter.actionToPieData(data)
                    val largestType  = data.groupBy { it.type }
                        .map { (type, actions) -> Pair(type, actions.sumOf { it.duration }) }
                        .maxByOrNull { (_, sum) -> sum }?.first
                    _state = _state.copy(
                        actions = result.data!!,
                        selectedType = state.selectedType,
                        largestType = largestType?:Action.Type.NO_INPUT,
                        barChartData = barData,
                        pieChartData = pieData,
                        lineChartData = null,
                        isLoading = false
                    )
                }
                else -> Unit
            }
        }
    }

    private suspend fun getAllActionsInIntervalWithType(
        startDate: Long,
        endDate: Long,
        actionType: Action.Type
    ) {
        repository.getAllActionsInIntervalWithType(startDate, endDate, actionType)
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data!!
                        val lineData = if (state.selectedInterval == TimeInterval.LAST_YEAR) null else
                            ChartDataConverter.actionToLineData(data, actionType)
                        _state = _state.copy(
                            actions = data.flatMap { it.actions.filter { feeling->feeling.endDate>0 } },
                            selectedType = actionType,
                            lineChartData = lineData,
                            barChartData = null,
                            pieChartData = null,
                            isLoading = false
                        )
                    }
                    else -> Unit
                }
            }
    }

    enum class TimeInterval {
        LAST_WEEK,
        LAST_MONTH,
        LAST_YEAR
    }

}