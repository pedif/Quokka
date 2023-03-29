package com.techspark.day.day

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.common.DataHelper
import com.techspark.core.common.openLink
import com.techspark.core.model.Day
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.day.addAction.ActionSheet

@Composable
fun DayScreen(
    viewModel: DayViewModel = viewModel(),
    modifier: Modifier = Modifier,
    showActionSheet: Boolean = false,
    onSheetDismissed: () -> Unit = {},
    onActionItemSelected: () -> Unit = {}
) {

    var linkAddress by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current as Activity
    LaunchedEffect(key1 = linkAddress){
        if(linkAddress.isNotEmpty()){
            openLink(context, linkAddress)
            linkAddress =""
        }
    }

    val state = viewModel.state
    Box {
        if (!state.isLoading)
            DayScreen(day = state.day, modifier = modifier.fillMaxSize(),
                onActionClicked = { id ->
                    viewModel.actionId = id
                    onActionItemSelected()
                },
                onActionDelete = { id ->
                    viewModel.actionId = id
                    viewModel.deleteAction(false)
                },
                onLinkSelected = {linkAddress = it}
            )
        if (showActionSheet)
            ActionSheet(onTaskFinished = onSheetDismissed)
//        LaunchedEffect(key1 = showActionSheet ){
//            if(!showActionSheet)
//                onSheetDismissed()
//        }
    }

    if(state.showDeleteDialog)
        DeleteDialog(onAccepted = {viewModel.deleteAction(true)},
        onCanceled = {viewModel.deleteDialogCanceled()})
}

@Composable
fun DayScreen(
    day: Day, modifier: Modifier = Modifier, onActionClicked: (Long) -> Unit = {},
    onActionDelete: (Long) -> Unit = {},
    onLinkSelected: (String) -> Unit={}
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = MARGIN_MEDIUM))
            .semantics { testTag = "day screen" }
    ) {

        ActionList(
            day = day,
            items = day.actions,
            onItemSelected = onActionClicked,
            onItemDelete = onActionDelete,
            onLinkSelected = onLinkSelected
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDayScreen() {
    val startDate = System.currentTimeMillis()
    val day = Day(startDate, DataHelper.getFeelings(startDate))
    IFeelTheme {
        DayScreen(day)
    }
}