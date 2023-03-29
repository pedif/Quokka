package com.techspark.notification

// for a 'val' variable

// for a `var` variable also add

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.ACTION_DURATION_DESCRIPTION
import com.techspark.core.util.COMMENT_LABEL
import com.techspark.core.util.COMMENT_PLACEHOLDER
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.view.IFeelButton
import com.techspark.core.view.Spinner
import com.techspark.notification.util.NotificationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotificationActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

//                window.setBackgroundDrawable(ColorDrawable(getColor(android.R.color.transparent)))
//                window.setDimAmount(0.3f)
            IFeelTheme {
                NotificationScreen {
                    finish()
                }
            }
        }

    }

}

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onTaskFinished: () -> Unit = {}
) {

    val state = viewModel.state
    val context = LocalContext.current
    LaunchedEffect(key1 = state.hasFinishedTask) {
        if (state.hasFinishedTask) {
            val ongoing = if (state.isOngoing) state.action else null
            NotificationUtil.showOngoingNotification(context, ongoing)
            onTaskFinished()
        }
    }

    val title = if (state.isOngoing)
        getSolutionText(action = state.action)
    else
        AnnotatedString(stringResource(id = R.string.screen_title))

    NotificationScreen(modifier,
        isOngoing = state.isOngoing,
        titleLabel = title,
        actionComment = { state.action.comment },
        onTypeChanged = { viewModel.updateActionType(it) },
        onCommentChanged = { viewModel.updateActionComment(it) },
        onSavePressed = { viewModel.saveAction() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier, actionComment: () -> String = { "" },
    isOngoing: Boolean,
    titleLabel: AnnotatedString,
    onTypeChanged: (Int) -> Unit = {},
    onCommentChanged: (String) -> Unit = {},
    onSavePressed: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(dimensionResource(id = com.techspark.core.R.dimen.margin_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = titleLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
        val actions = Action.Type.values()
        val labels = Action.Type.values().map { stringResource(id = it.title) }.dropLast(1)
        if (!isOngoing)
            Spinner(items = labels,
                itemColors = actions.map { it.color }.toList(),
                initialText = labels[0],
                onItemSelected = { onTypeChanged(it) })
        Spacer(modifier = Modifier.height(dimensionResource(id = com.techspark.core.R.dimen.margin_small)))
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
            label = { Text(stringResource(id = COMMENT_LABEL)) },
            placeholder = { Text(stringResource(id = COMMENT_PLACEHOLDER))},
            value = actionComment(),
            onValueChange = { text ->
                onCommentChanged(text)
            })
//        OutlinedTextField(modifier = Modifier
//            .fillMaxWidth()
//            .height(64.dp)
//            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
//            value = actionComment(),
//            onValueChange = { text ->
//                onCommentChanged(text)
//            })

        Spacer(modifier = Modifier.height(dimensionResource(id = com.techspark.core.R.dimen.margin_large)))
        val btnTitle = if (isOngoing)
            stringResource(id = R.string.button_finish)
        else
            stringResource(id = R.string.button_save)
        IFeelButton(
            text = btnTitle,
            onClicked = onSavePressed
        )
    }
}


@Composable
private fun getSolutionText(action: Action): AnnotatedString {
    val labelAction = Action(
        0, "", startDate = action.startDate,
        endDate = action.endDate, type = action.type, ""
    )
    if (labelAction.endDate == 0L) {
        labelAction.endDate = System.currentTimeMillis()
        labelAction.updateDuration()
    }
    return buildAnnotatedString {

        val adj = stringResource(id = labelAction.type.adjective)
        val duration = labelAction.getDurationLabel(LocalContext.current)
        val summary = stringResource(
            id = ACTION_DURATION_DESCRIPTION,
            adj,
            duration
        )
        append(summary)

        //we want the adjective to have a different style
        val adjStartedIndex = summary.indexOf(adj)
        addStyle(
            SpanStyle(labelAction.type.color, fontWeight = FontWeight.Bold),
            adjStartedIndex,
            adjStartedIndex + adj.length
        )
        //we want the duration to have a different style
        val durationIndex = summary.indexOf(duration)
        addStyle(
            SpanStyle(fontWeight = FontWeight.Bold),
            durationIndex,
            durationIndex + duration.length
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    IFeelTheme {
        NotificationScreen(isOngoing = true,
            titleLabel = AnnotatedString("what are you feeling"),
            actionComment = { "comment" })
    }
}

