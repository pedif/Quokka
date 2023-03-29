package com.techspark.day.addAction

import com.techspark.core.model.Action

data class ActionState(
    val action:Action = Action(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasFinishedTask:Boolean = false,
    val showConfirmationDialog:Boolean = false
)