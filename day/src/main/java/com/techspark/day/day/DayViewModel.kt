package com.techspark.day.day

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techspark.core.common.DateUtil
import com.techspark.core.data.Repository
import com.techspark.core.data.Resource
import com.techspark.core.model.Action
import com.techspark.day.R
import com.techspark.day.addAction.ActionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    private val repository: Repository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _state by mutableStateOf(DayState())
    val state: DayState
        get() = _state
    var dayId = 0L

    val dayIdKey = "day_id"


    init {
        dayId = savedStateHandle.get<Long>(dayIdKey) ?: 0L
        getDay()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getDay() {
        viewModelScope.launch {
            _state = _state.copy(
                isLoading = true
            )
            repository.getDay(dayId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state = _state.copy(
                            day = result.data!!, isLoading = false
                        )
                        ongoingAction =
                            result.data?.actions?.firstOrNull { it.endDate == 0L }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private var _actionState by mutableStateOf(ActionState())
    val actionState: ActionState
        get() = _actionState
    var ongoingAction: Action? = null
        private set

    var actionId = 0L
        set(value) {
            field = value
            _actionState = _actionState.copy(
                action = Action(),
                error = null,
                hasFinishedTask = false,
                showConfirmationDialog = false,
                isLoading = true
            )
        }

    fun getAction() {
        viewModelScope.launch {
            _actionState = _actionState.copy(
                isLoading = true
            )
            repository.getAction(actionId)
                .catch {
                    _actionState = _actionState.copy(
                        error = "exeptin", isLoading = false
                    )
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val startDate = DateUtil.getTodayTimeForTargetDate(dayId)
                            val endDate = if (dayId == DateUtil.getStartOfToday())
                                0L
                            else
                                startDate + TimeUnit.MINUTES.toMillis(30L)
                            val action = result.data ?: Action(
                                0L,
                                "",
                                startDate,
                                endDate,
                                Action.Type.HAPPINESS,
                                ""
                            )
                            _actionState = _actionState.copy(
                                action = action, isLoading = false, hasFinishedTask = false,
                                error = null

                            )
                        }
                        else -> {

                        }
                    }
                }

        }
    }

    fun updateAction(newAction: Action, context: Context? = null) {
        /**
         * In case there is an ongoing action the user
         * cannot add an action which has happened before the ongoing action
         */
        var error: String? = null
        ongoingAction?.let { ongoing ->
            if (newAction.startDate < ongoing.startDate) {
                newAction.startDate = ongoing.startDate
                error = context?.resources?.getString(R.string.error_start_date_before_ongoing)
            }
        }
        _actionState = _actionState.copy(action = newAction, error = error)
    }

    fun deleteAction(shouldDelete: Boolean = false) {
        if (!shouldDelete) {
            _state = _state.copy(
                showDeleteDialog = true
            )
        } else {
            viewModelScope.launch {
                repository.deleteActionById(actionId)
                _state = _state.copy(
                    showDeleteDialog = false
                )
            }
        }

    }

    fun saveAction(shouldEndOngoingFeeling: Boolean) {
        viewModelScope.launch {
            val action = actionState.action

            /**
             * If the ongoing feeling needs to be finished
             * we need to update the ongoing feeling according to the
             * action duration,
             * 1. if the action happens at the same time as the ongoign action we neeed to
             * delete the ongoing action
             * 2. if the action happens after the ongoing action start time we then just end the
             * ongoing action right before the start of this action
             */
            if (action.id != ongoingAction?.id) {
                if (shouldEndOngoingFeeling) {
                    ongoingAction?.let { ongoing ->
                        if (action.startDate == ongoing.startDate) {
                            repository.deleteActionById(ongoing.id)
                        } else {
                            ongoing.endDate = action.startDate - 1
                            repository.updateAction(ongoing)
                        }
                    }
                } else {
                    if (ongoingAction != null) {
                        _actionState = _actionState.copy(
                            showConfirmationDialog = true
                        )
                        return@launch
                    }
                }
            }

            /**
             * We need to check two things before the operation
             * first, an ongoing action cannot happen in the past
             * second, an action should end at most at 1 second before the start of next day
             */
            val startOfDay = DateUtil.getStartOfDay(dayId)
            val endOfDay = DateUtil.getTomorrowDate(startOfDay) - 1
            action.apply {
                endDate =
                    when {
                        endDate == 0L && startOfDay < DateUtil.getStartOfToday() ->
                            endOfDay
                        endDate < endOfDay ->
                            endDate
                        else ->
                            endOfDay
                    }
                updateDuration()
            }

            /**
             * If there is no free time we dont add the new item
             * if there is less available time than the item duration we update the duration
             * to the available time
             */
            val availableTime = state.day.feelingDuration[Action.Type.NO_INPUT]
            if (availableTime == 0) {
                _actionState = _actionState.copy(
                    hasFinishedTask = true
                )
                return@launch
            }

            action.apply {
                if (availableTime == null)
                    return@apply
                if (availableTime < duration)
                    endDate = startDate + TimeUnit.MINUTES.toMillis(availableTime.toLong())
            }



            if (action.id == 0L)
                repository.addAction(action = action)
            else
                repository.updateAction(action)
            _actionState = _actionState.copy(
                hasFinishedTask = true
            )
//            _actionState = _actionState.copy(
//                error = "Erro happebed!!"
//            )
        }
    }

    fun ongoingDialogCanceled() {
        _actionState = _actionState.copy(
            showConfirmationDialog = false
        )
    }

    fun deleteDialogCanceled() {
        _actionState = _actionState.copy(
            showConfirmationDialog = false
        )
    }
}