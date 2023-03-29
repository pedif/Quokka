package com.techspark.quokka.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination{

    val route:String
}

object HomeDestination: Destination {
    override val route = "home"
}

object DayDestination: Destination {
    override val route: String  = "day"
    const val dayIdArg = "day_id"
    val routeWithArgs =  "$route/{$dayIdArg}"
    val    arguments = listOf(
        navArgument(dayIdArg){type = NavType.LongType}
    )
}

object ActionDestination: Destination {
    override val route: String  = "action"
    const val actionIdArg = "action_id"
    val routeWithArgs =  "$route/{$actionIdArg}"
    val    arguments = listOf(
        navArgument(actionIdArg){type = NavType.LongType}
    )
}


object StatisticsDestination: Destination {
    override val route = "statistics"
}