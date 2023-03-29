package com.techspark.onboarding

import CrossSlide
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.techspark.core.common.openEmail
import com.techspark.core.common.openStorePage
import com.techspark.core.data.pref.IFeelPref
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_LARGE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.util.MARGIN_SMALL
import com.techspark.core.util.MARGIN_X_LARGE
import com.techspark.core.view.IFeelButton
import kotlinx.coroutines.launch


@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onAccepted: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .size(300.dp, 400.dp),
            shape = MaterialTheme.shapes.large
        ) {
            var screen by rememberSaveable {
                mutableStateOf(Pair(0, false))
            }

            Box {
                CrossSlide(targetState = screen.first, reverseAnimation = screen.second) { screen ->
                    OnBoardingPage(
                        modifier = Modifier.padding(dimensionResource(id = MARGIN_X_LARGE)),
                        pageIndex = screen,
                        onContactUsClicked = { openEmail(activity) },
                        onRateUsClicked = {
                            openStorePage(activity)
                            coroutineScope.launch {
                                IFeelPref.updateRateStatus(activity, true)
                            }
                        }
                    )
                }

                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "previous",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .padding(vertical = 12.dp)
                        .offset(x = -(10.dp))
                        .clickable {
                            var s = screen.first
                            s--
                            if (s < 0)
                                s = 5
                            screen = Pair(s, true)
                        })

                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "next",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp)
                        .padding(vertical = 12.dp)
                        .offset(x = 10.dp)
                        .clickable {
                            var s = screen.first
                            s++
                            s %= 6
                            screen = Pair(s, false)
                        })


            }
        }
    }
}


@Composable
@Preview
private fun PreviewDialog() {
    IFeelTheme {
        OnBoardingScreen()
    }
}