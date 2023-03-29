package com.techspark.quokka.model

import com.techspark.quokka.DIALOG

data class MainState(
     val dialogState: DIALOG = DIALOG.NONE,
     val isSubscribed:Boolean = false
)
