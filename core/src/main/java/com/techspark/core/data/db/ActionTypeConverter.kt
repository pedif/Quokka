package com.techspark.core.data.db

import androidx.room.TypeConverter
import com.techspark.core.model.Action

class ActionTypeConverter {
    @TypeConverter
    fun toHealth(value: String) = enumValueOf<Action.Type>(value)

    @TypeConverter
    fun fromHealth(value: Action.Type) = value.name

}