package com.techspark.day.addAction

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.*
import com.techspark.core.view.IFeelButton
import com.techspark.core.view.Spinner
import com.techspark.day.R
import com.techspark.day.day.DayViewModel
import com.techspark.day.day.OngoingDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


const val MINUTE = Calendar.MINUTE
const val HOUR = Calendar.HOUR_OF_DAY

@Composable
fun ActionSheet(
    viewModel: DayViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onTaskFinished: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    DisposableEffect(viewModel.actionId) {
        viewModel.getAction()
        onDispose {
            viewModel.actionId = 0
        }
    }
    val state = viewModel.actionState
    if (state.isLoading)
        return
    if (state.hasFinishedTask) {
        onTaskFinished()
        return
    }

    state.error?.let {
        scope.launch {
            snackbarHostState.showSnackbar(it)
        }
    }
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize()) {
        ActionSheet(action = state.action.copy(),
            onSheetDismiss = onTaskFinished,
            onSavePressed = { viewModel.saveAction(false) },
            onActionChanged = { action ->
                viewModel.updateAction(action, context)
            })
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { Snackbar(snackbarData = it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        if (state.showConfirmationDialog) {
            OngoingDialog(onCanceled = { viewModel.ongoingDialogCanceled() },
                onAccepted = { viewModel.saveAction(true) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSheet(
    action: Action,
    modifier: Modifier = Modifier,
    onSheetDismiss: () -> Unit = {},
    onSavePressed: () -> Unit = {},
    onActionChanged: (Action) -> Unit = {}
) {

    var closeSheet by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(closeSheet) {
        if (closeSheet) {
            delay(400)
            onSheetDismiss()
        }
    }

    val height = animateDpAsState(targetValue = if (closeSheet) 0.dp else 320.dp)


    Surface(
        shadowElevation = dimensionResource(MARGIN_MEDIUM),
        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()

        ) {
            val cornerWidth = MaterialTheme.shapes.extraLarge.topStart
            Column(
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(
                            cornerWidth,
                            cornerWidth,
                            CornerSize(0), CornerSize(0)
                        )
                    )
                    .height(height.value)
                    .padding(dimensionResource(id = MARGIN_MEDIUM))
                    .align(Alignment.BottomCenter)
                    .semantics { contentDescription = "add action screen" },
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "",
                    modifier = Modifier
                        .clickable { closeSheet = true }
                        .padding(
                            horizontal = dimensionResource(id = MARGIN_LARGE),
                            vertical = dimensionResource(
                                id =
                                MARGIN_SMALL
                            )
                        )
                )

                Box(
                    modifier = modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeComponent(
                            date = action.startDate
                        ) {
                            action.updateTime(it)
                            onActionChanged(action)
                        }

                        DurationComponent(
                            durationLabel = action.getDurationLabel(LocalContext.current)
                        ) { duration ->
                            action.setEndByDuration(duration)
                            onActionChanged(action)
                        }
                    }
                    Box(
                        modifier = modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        val labels = Action.Type.values().map { stringResource(id = it.title) }.dropLast(1)
                        Spinner(
                            items = labels,
                            itemColors = Action.Type.values().map { it.color }.toList(),
                            initialText = stringResource(id = action.type.title)
                        ) { id ->
                            action.type = Action.Type.values()[id]
                            onActionChanged(action)
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = MARGIN_MEDIUM))
                )

                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                    label = { Text(stringResource(id = COMMENT_LABEL)) },
                    placeholder = { Text(text = stringResource(id = COMMENT_PLACEHOLDER))},
                    value = action.comment,
                    onValueChange = { text ->
                        action.comment = text
                        onActionChanged(action)
                    })

                Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
                IFeelButton(
                    modifier = Modifier.padding(horizontal = dimensionResource(id = MARGIN_SMALL),
                    vertical = dimensionResource(id = MARGIN_X_SMALL)),
                    text = stringResource(id = R.string.save),
                    onClicked = onSavePressed
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeelingPreview() {
    IFeelTheme {
        val startDate = System.currentTimeMillis()
        val item = Action(
            1, "", startDate, startDate + 3600L, Action.Type.HAPPINESS, ""
        )
        ActionSheet(item)
    }
}