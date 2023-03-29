package com.techspark.day.addAction

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DateUtil
import com.techspark.core.common.roundedBorder
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.day.R
import kotlinx.coroutines.delay
import java.util.*


@Composable
fun TimeComponent(
    date: Long, modifier: Modifier = Modifier, onDateChanged: (Long) -> Unit
) {
    val minuteListener: (Int) -> Unit = { value ->
        val newDate = DateUtil.setField(date, MINUTE, value)
        onDateChanged(newDate)
    }
    val hourListener: (Int) -> Unit = { value ->
        val newDate = DateUtil.setField(date, HOUR, value)
        onDateChanged(newDate)
    }
    val cal = Calendar.getInstance()
    cal.timeInMillis = date
    Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.start_date_label))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                TimeField(startValue = cal[HOUR], maxValue = 23, onSetValue = hourListener)
                Text(
                    text = ":",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                TimeField(startValue = cal[MINUTE], maxValue = 59, onSetValue = minuteListener)
            }

        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    startValue: Int,
    maxValue: Int,
    onSetValue: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxDelayMillis: Long = 1000,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = 0.15f
) {

    val onValueChanged: (Boolean) -> Unit = { increase ->
        val nextVal = if (increase) startValue + 1
        else startValue - 1
        if (nextVal in 0..maxValue) onSetValue(nextVal)
    }

    Column(
        modifier = modifier
            .width(dimensionResource(id = R.dimen.button_width))
            .height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TouchableArrow(
            increase = true,
            onClicked = { onValueChanged(true) },
            maxDelayMillis = maxDelayMillis,
            minDelayMillis = minDelayMillis,
            delayDecayFactor = delayDecayFactor
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
        Text(
            text = startValue.toString(),
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.button_width), dimensionResource(id = R.dimen.button_height))
                .roundedBorder(MaterialTheme.colorScheme.primary)
                ,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_MEDIUM)))
        TouchableArrow(
            increase = false,
            onClicked = { onValueChanged(false) },
            maxDelayMillis = maxDelayMillis,
            minDelayMillis = minDelayMillis,
            delayDecayFactor = delayDecayFactor
        )
    }


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TouchableArrow(
    increase: Boolean,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
    maxDelayMillis: Long = 500,
    minDelayMillis: Long = 50,
    delayDecayFactor: Float = 0.3f
) {
    val currentClickListener by rememberUpdatedState(onClicked)
    var valueChangeState by remember {
        mutableStateOf(false)
    }
    val backgroundColor by animateColorAsState(
        if (valueChangeState)
            MaterialTheme.colorScheme.primary
        else
            androidx.compose.ui.graphics.Color.Transparent
    )
    val text = if (increase) "+" else "-"

    Text(
        modifier = modifier
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown()
                        valueChangeState = true
                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach {
                                it.consume()
                            }
                        } while (event.changes.any { it.pressed })
                        valueChangeState = false
                    }
                }
            }
            .size(dimensionResource(id = R.dimen.button_width), dimensionResource(id = R.dimen.button_height))
            .roundedBorder(),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    LaunchedEffect(valueChangeState) {
        var currentDelayMillis = maxDelayMillis

        while (valueChangeState) {
            Log.e("timetime", "called")
            currentClickListener()
            delay(maxDelayMillis)
            currentDelayMillis =
                (currentDelayMillis - (currentDelayMillis * delayDecayFactor))
                    .toLong().coerceAtLeast(minDelayMillis)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TimeComponentPreview() {
    IFeelTheme {
        var startDate by remember {
            mutableStateOf(System.currentTimeMillis())
        }
        val item = Action(
            1, "", startDate, startDate + 3600L, Action.Type.HAPPINESS, ""
        )
        TimeComponent(date = startDate, onDateChanged = { date -> startDate = date })
    }
}