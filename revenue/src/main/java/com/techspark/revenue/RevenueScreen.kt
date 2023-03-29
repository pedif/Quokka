package com.techspark.revenue

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_LARGE
import kotlinx.coroutines.launch

@Composable
fun RevenueScreen(
    viewModel: RevenueViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    var isIdle by rememberSaveable {
        mutableStateOf(true)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        RevenueScreen(modifier,
            onDismiss = onDismiss,
            isSubscribed = state.value.isSubscribed,
            isIdle = isIdle,
            onSubscribedClicked = {
                isIdle = false
                viewModel.purchaseSub(context as Activity)
            },
            onWatchAddClicked = {
                isIdle = false
                viewModel.showRewardedVideo(
                    context.getActivity()!!
                )
            })
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { Snackbar(snackbarData = it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    LaunchedEffect(state.value.code) {
        isIdle = true
        val code = state.value.code
        if (code == ErrorCode.NONE)
            viewModel.initBillingClient(context)
        else if (viewModel.userStartedAction) {
            viewModel.onMessageShown()
            snackbarHostState.showSnackbar(
                getErrorMessage(context = context, code = code)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getSubscriptionStatus(context)
        viewModel.checkConsentStatus(context as Activity)
    }

}

@Composable
private fun RevenueScreen(
    modifier: Modifier = Modifier,
    isSubscribed: Boolean = false,
    isIdle: Boolean = true,
    onDismiss: () -> Unit = {},
    onSubscribedClicked: () -> Unit,
    onWatchAddClicked: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(300.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = modifier
                    .padding(dimensionResource(id = MARGIN_LARGE)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Text(
                        text = "Buy me a coffee!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This app is 100% free, however, if you want to support us, Please use either of the below options to your heart's content :)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                if (isIdle) {
                    var numberLabel = ""
                    if (!isSubscribed) {
                        TextButton(onClick = onSubscribedClicked) {
                            Text(
                                text = "1. Coffee? I will buy you a warm meal instead! (Purchase a Subscription)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        numberLabel = "2. "
                    }
                    TextButton(onClick = onWatchAddClicked) {
                        Text(text = "$numberLabel Sure, no problemo amigo! (Watch an ad)")
                    }
                } else {
                    CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.height(1.dp))

            }
        }
    }
}

private fun getErrorMessage(context: Context, code: ErrorCode): String {
    return when (code) {
        ErrorCode.PLAY_SERVICES ->
            context.getString(R.string.error_play_services)
        ErrorCode.NO_PRODUCT ->
            context.getString(R.string.error_no_product)
        ErrorCode.UNKNOWN ->
            context.getString(R.string.error_unknown)
        ErrorCode.USER_CANCELED ->
            context.getString(R.string.error_canceled)
        ErrorCode.Successful ->
            context.getString(R.string.sucesss)

        ErrorCode.AD_NOT_LOADED ->
            context.getString(R.string.ad_not_loaded)
        else ->
            ""
    }
}

@Preview
@Composable
private fun PreviewRevenueScreen() {
    IFeelTheme {
        RevenueScreen(onSubscribedClicked = {}) {}
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}