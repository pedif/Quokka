package com.techspark.quokka.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.techspark.day.day.DayScreen
import com.techspark.day.day.DayViewModel
import com.techspark.home.HomeScreen
import com.techspark.home.HomeViewModel
import com.techspark.statistics.StatisticsScreen
import com.techspark.statistics.StatisticsViewModel

val navStartRoute = HomeDestination.route

@Composable
fun IFeelNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showActionSheet: Boolean = false,
    shouldShowToolBar:(Boolean)->Unit = {},
   shouldShowFab: (Boolean)->Unit = {},
    onSheetDismissed: () -> Unit = {},
    onShowRateDialog:() ->Unit = {},
    onActionItemSelected:()->Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = navStartRoute,
        modifier = modifier
    ) {

        composable(HomeDestination.route) {
            // Creates a ViewModel from the current BackStackEntry
            // Available in the androidx.hilt:hilt-navigation-compose artifact
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(viewModel = viewModel,
            onShowRateDialog = onShowRateDialog ) { dayId ->
                navController.navigateToDayScreen(dayId)
            }
            shouldShowFab(false)
            shouldShowToolBar(true)
        }

        composable(
            route = DayDestination.routeWithArgs,
            arguments = DayDestination.arguments
        ) {

            val viewModel = hiltViewModel<DayViewModel>()

            DayScreen(
                viewModel = viewModel,
                showActionSheet = showActionSheet,
                onSheetDismissed = onSheetDismissed,
                onActionItemSelected = onActionItemSelected
            )
            shouldShowFab(true)
            shouldShowToolBar(false)
        }

        composable(StatisticsDestination.route) {
            // Creates a ViewModel from the current BackStackEntry
            // Available in the androidx.hilt:hilt-navigation-compose artifact
            val viewModel = hiltViewModel<StatisticsViewModel>()
            StatisticsScreen(viewModel = viewModel)
            shouldShowFab(false)
            shouldShowToolBar(false)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String,
                                          shouldSaveState:Boolean = true) =
    this.navigate(route) {
        popUpTo(
            HomeDestination.route
        ) {
            saveState = shouldSaveState
        }
        launchSingleTop = true
        restoreState = true
    }


private fun NavHostController.navigateToDayScreen(dayId: Long) {
    this.navigateSingleTopTo("${DayDestination.route}/$dayId")
}
 fun NavHostController.navigateToActionScreen(actionId: Long) {
    this.navigate("${ActionDestination.route}/$actionId")
}
