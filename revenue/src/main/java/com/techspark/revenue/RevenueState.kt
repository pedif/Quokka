package com.techspark.revenue

data class RevenueState(
    val isSubscribed:Boolean = false,
    val code:ErrorCode  = ErrorCode.NONE
)

enum class ErrorCode {
    NONE,
    PLAY_SERVICES,
    NO_PRODUCT,
    USER_CANCELED,
    Successful,
    AD_NOT_LOADED,
    UNKNOWN
}
