package com.techspark.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.R
import com.techspark.core.chart.piechart.PieChartData
import com.techspark.core.model.Action
import com.techspark.home.model.HomeDay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onShowRateDialog:()->Unit ={},
    onDayClicked: (Long) -> Unit = {}
) {

    val context = LocalContext.current as Activity
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val state = viewModel.state
    if (!state.isLoading) {
        HomeScreen(
            currentAction = state.ongoingAction,
            chartData = state.chartData,
            featuredLargestAction = state.yesterdayLargestAction,
            days = state.dayList,
            modifier = modifier,
            onDayClicked = onDayClicked
        )

        LaunchedEffect(true){
            viewModel.checkForRateDialog(context, onShowRateDialog)
        }
    }

    if (state.askForNotificationPermission && !hasNotificationPermission) {

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
                viewModel.notificationDialogShowed()
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
                return
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun HomeScreen(
    currentAction: Action?,
    chartData: PieChartData,
    featuredLargestAction: Action.Type,
    days: List<HomeDay>,
    modifier: Modifier = Modifier,
    onDayClicked: (Long) -> Unit = {}
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(148.dp),
        contentPadding = PaddingValues(
            dimensionResource(id = R.dimen.margin_medium)
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TodayItem(currentAction) { id -> onDayClicked(id) }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
        }
        //In case no feelings were found we don't need to show a summary

        item(span = { GridItemSpan(maxLineSpan) }) {
            FeaturedItem(chartData = chartData, actionType = featuredLargestAction)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_medium)))
        }
        itemsIndexed(days,
            key = { i, day -> i }) { _, day ->
            DayItem(day = day) { id -> onDayClicked(id) }
//            if (index < days.lastIndex)
//                Divider(color = Color.Black, thickness = 1.dp)
        }
    }
}

