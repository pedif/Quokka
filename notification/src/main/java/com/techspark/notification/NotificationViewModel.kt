package com.techspark.notification

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techspark.core.common.DateUtil
import com.techspark.core.data.Repository
import com.techspark.core.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var _state by mutableStateOf(ActionState())
    val state: ActionState
        get() = _state

    init {
        getOngoingAction()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getOngoingAction() {
        viewModelScope.launch {
            val ongoing = repository.getOngoingAction(DateUtil.getStartOfToday()).data
            _state = _state.copy(
                action = ongoing ?: Action(), isOngoing = ongoing != null
            )
        }
    }

    fun updateAction(newAction: Action) {
        _state = _state.copy(action = newAction)
    }

    fun updateActionComment(comment: String) {
        val action = _state.action
        _state = _state.copy(action = action.copy(comment = comment))
    }

    fun updateActionType(type: Int) {
        val action = _state.action
        _state = _state.copy(action = action.copy(type = Action.Type.values()[type]))
    }

    fun saveAction() {
        viewModelScope.launch {
            val action: Action = state.action
            var isOngoing = false
            if (action.id != 0L) {
//                action.endDate = System.currentTimeMillis()
//                repository.updateAction(action)
                checkForOngoingAction(action)
            } else {
                action.startDate = System.currentTimeMillis()
                repository.addAction(action)
                isOngoing = true
            }
            _state = _state.copy(
                action = action,
                hasFinishedTask = true,
                isOngoing = isOngoing
            )
        }
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
    private fun checkForOngoingAction(ongoing: Action) {
        val startOfToday = DateUtil.getStartOfDay(System.currentTimeMillis())
        var curDate = (ongoing.dayId)

        //In case we are already at current day we just end the ongoing action
        if (curDate == startOfToday) {
            repository.updateAction(
                ongoing.copy(
                    endDate = System.currentTimeMillis()
                )
            )
            return
        }
        var hasUpdatedOngoing = false
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
                startDate = startOfToday,
                endDate = System.currentTimeMillis()
            )
        )
    }
}