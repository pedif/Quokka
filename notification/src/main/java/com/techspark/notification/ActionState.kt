package com.techspark.notification

import com.techspark.core.model.Action
import com.techspark.core.model.Day

data class ActionState(
    val action: Action = Action(),
    val isOngoing:Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasFinishedTask:Boolean = false
)