package com.techspark.home

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techspark.core.common.ChartDataConverter
import com.techspark.core.common.DateUtil
import com.techspark.core.data.Repository
import com.techspark.core.data.Resource
import com.techspark.core.data.pref.IFeelPref
import com.techspark.core.model.Action
import com.techspark.core.model.Day
import com.techspark.home.model.HomeDay
import com.techspark.home.model.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _state by mutableStateOf(HomeState())
    val state: HomeState
        get() = _state

    init {
        getActions()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getActions() {
        viewModelScope.launch {
            _state = _state.copy(isLoading = true)
            /**
             * We want to get the actions for today up to 7 days ago so a total of
             * 8 days, the last valid time would be the end of today(before start of tomorrow)
             */
            val endOfToday = DateUtil.getEndOfDay(System.currentTimeMillis())
            val startOfToday = DateUtil.getStartOfDay(System.currentTimeMillis())
            val lastWeek = startOfToday - TimeUnit.DAYS.toMillis(7)
            repository.getAllDaysInInterval(lastWeek, endOfToday).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { items ->
//                            if (items.isEmpty()||(items.size==1 && items[0].actions.isEmpty())) {
//                                DataHelper.getDays(startOfToday).forEach { day ->
//                                    day.actions.forEach { action ->
//                                        repository.addAction(action.copy(id = 0))
//                                    }
//                                }
//                            } else {
                            convertDaysToHomeState(items, startOfToday)
//                            }
                        }
                    }
                    is Resource.Error -> {
                        _state = _state.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Convert Day model class to HomeDay which is used in the home screen
     * check for ongoing action and update the database accordingly in case we have
     * ongoing actions which have lasted for more than a day
     */
    private fun convertDaysToHomeState(items: List<Day>, lastDay: Long) {
        val today = items.flatMap { it.actions }
        val ongoing = today.firstOrNull { it.endDate == 0L }
        checkForOngoingAction(ongoing)
        val others = items.filter { it.startDate < lastDay }
        val lastWeek = others.flatMap { it.actions }
        val askForPermission = (today.size + lastWeek.size) > 0
        val feelings = lastWeek.groupBy { it.type }
            .map { (type, actions) -> Pair(type, actions.sumOf { it.duration }) }
            .maxByOrNull { (_, sum) -> sum }
        _state = _state.copy(
            ongoingAction = ongoing,
            chartData = ChartDataConverter.actionToPieData(lastWeek),
            yesterdayLargestAction = feelings?.first ?: Action.Type.NO_INPUT,
            dayList = others.map { day -> HomeDay.fromDay(day) },
            isLoading = false,
            askForNotificationPermission = askForPermission
        )
    }

    /**
     * There might be ongoing feelings which their duration took
     * more than a day or happend on day changes. this function
     * would traverse through the days since the start of the action and add
     * appropriate actions between the end time and start time of the action
     * meaning an action which has happened on Sunday and still ongoing on Wednesday
     * would be converted to: 1. finished action on sunday, 2. finished action on monday
     * 3. finished action on Tuesday, 4. ongoing action on Wedensday
     */
    private fun checkForOngoingAction(ongoing: Action?) {
        if (ongoing == null)
            return
        val startOfToday = DateUtil.getStartOfDay(System.currentTimeMillis())
        var curDate = (ongoing.dayId)

        if (curDate == startOfToday)
            return
        var hasUpdatedOngoing = false
        viewModelScope.launch {
            while (curDate < startOfToday) {
                val tomorrow = DateUtil.getTomorrowDate(curDate)
                //If we have not yet updated the original ongoing action, finish that
                //otherwise add a finished action for each respective day till today
                if (!hasUpdatedOngoing) {
                    repository.updateAction(
                        ongoing.copy(
                            endDate = tomorrow - 1
                        )
                    )
                    hasUpdatedOngoing = true
                } else {
                    repository.addAction(
                        ongoing.copy(
                            id = 0L,
                            startDate = curDate,
                            endDate = tomorrow - 1
                        )
                    )
                }
                curDate = tomorrow
            }
            //add an ongoing action for today
            repository.addAction(
                ongoing.copy(
                    id = 0L,
                    startDate = startOfToday
                )
            )
        }
    }

    fun checkForRateDialog(context: Context, onShouldShowDialog: () -> Unit) {
        viewModelScope.launch {
            val lastCount = IFeelPref.getLastRateDate(context)
            val total = repository.getTotalActionCount()
            val hasRated = IFeelPref.hasRated(context)
            //if the user has already rated the app
            if (hasRated)
                return@launch

            val diff = total - lastCount
            /**
             * If it is the first time the app has been launched as soon as the user
             * adds a feeling we show the rate dialog
             * otherwise every 5 feelings that the user adds
             */
            if (lastCount == 0) {
                if (total >= 1) {
                    onShouldShowDialog()
                    IFeelPref.updateLastRateDate(context, total)
                }

            } else if (diff != 0 && diff % 5 == 0) {
                onShouldShowDialog()
                IFeelPref.updateLastRateDate(context, total)
            }


//            if(now - lastDate > TimeUnit.DAYS.toDays(1) )
//                onShouldShowDialog()
        }
    }

    fun notificationDialogShowed() {
        _state = _state.copy(askForNotificationPermission = false)
    }
}