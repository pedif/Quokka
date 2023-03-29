package com.techspark.day.day

import com.techspark.core.model.Day

data class DayState(
    val day: Day = Day(0, emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteDialog:Boolean = false
)