package com.techspark.onboarding

import android.graphics.Paint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.core.util.MARGIN_SMALL
import com.techspark.core.util.MARGIN_X_SMALL
import com.techspark.core.view.IFeelButton


@Composable
fun OnBoardingPage(
    pageIndex: Int,
    modifier: Modifier = Modifier,
    onContactUsClicked: () -> Unit = {},
    onRateUsClicked: () -> Unit = {}
) {
    when (pageIndex) {
        4 ->
            FeelingDefinitionOnBoarding(
                text = getOnBoardingText(pageIndex = pageIndex),
                modifier
            )
        5 ->
            ContactUsOnBoarding(
                text = getOnBoardingText(pageIndex = pageIndex),
                modifier,
                onContactUsClicked,
                onRateUsClicked
            )
        else -> GenericOnBoarding(
            text = getOnBoardingText(pageIndex = pageIndex),
            image = painterResource(id = getOnBoardingImage(pageIndex)),
            modifier = modifier
        )
    }

}

@Composable
private fun GenericOnBoarding(
    text: String,
    image: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = image,
            contentDescription = ""
        )
    }
}

@Composable
private fun ContactUsOnBoarding(
    text: String,
    modifier: Modifier = Modifier,
    onContactUsClicked: () -> Unit = {},
    onRateUsClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        IFeelButton(
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = MARGIN_MEDIUM),
                vertical = dimensionResource(id = MARGIN_SMALL)
            ),
            text = stringResource(id = R.string.contact_us),
            onClicked = onContactUsClicked
        )
        IFeelButton(
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = MARGIN_MEDIUM),
                vertical = dimensionResource(id = MARGIN_SMALL)
            ),
            text = stringResource(id = R.string.rate_us),
            onClicked = onRateUsClicked
        )
    }
}


@Composable
private fun FeelingDefinitionOnBoarding(
    text: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedType by remember {
        mutableStateOf(Action.Type.NO_INPUT)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .scrollable(scrollState, Orientation.Vertical),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        Action.Type.values().dropLast(1).forEach { type ->
            FeelingInfo(type = type, selected = type == selectedType,
                onClick = {
                    selectedType = if (selectedType == type)
                        Action.Type.NO_INPUT
                    else
                        type
                })
        }

    }
}

@Composable
private fun FeelingInfo(
    type: Action.Type, selected: Boolean, modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Column(modifier = modifier.fillMaxWidth()
        .padding( dimensionResource(id = MARGIN_SMALL))
        .animateContentSize()
        .clickable { onClick() }) {
        if (selected)
            Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_SMALL)))
        Text(
            text = stringResource(id = type.title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (selected) {
            Text(
                text = getTypeDefinitionText(type = type),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = dimensionResource(id = MARGIN_X_SMALL),
                    start = dimensionResource(id = MARGIN_SMALL)
                )
            )
            Text(
                text = getTypeExpressionText(type = type),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = dimensionResource(id = MARGIN_X_SMALL),
                    start = dimensionResource(id = MARGIN_SMALL)
                )
            )
        }
    }
}

@Composable
private fun getOnBoardingText(pageIndex: Int): String {

    return when (pageIndex) {
        0 -> stringResource(id = R.string.on_boarding_1)
        1 -> stringResource(id = R.string.on_boarding_2)
        2 -> stringResource(id = R.string.on_boarding_3)
        3 -> stringResource(id = R.string.on_boarding_4)
        4 -> stringResource(id = R.string.on_boarding_5)
        5 -> stringResource(id = R.string.on_boarding_6)
        else ->
            ""
    }
}

private fun getOnBoardingImage(pageIndex: Int): Int {

    return when (pageIndex) {
        0 -> R.drawable.on_boarding_1
        1 -> R.drawable.on_boarding_2
        2 -> R.drawable.on_boarding_3
        3 -> R.drawable.on_boarding_4
        else -> 0
    }
}

@Composable
private fun getTypeDefinitionText(type: Action.Type): String {

    return when (type) {
        Action.Type.HAPPINESS -> stringResource(id = R.string.happiness_description)
        Action.Type.ANGER -> stringResource(id = R.string.anger_description)
        Action.Type.ANXIETY -> stringResource(id = R.string.anxiety_description)
        Action.Type.SADNESS -> stringResource(id = R.string.sadness_description)
        Action.Type.DEPRESSED -> stringResource(id = R.string.depression_description)
        else ->
            ""
    }
}

@Composable
private fun getTypeExpressionText(type: Action.Type): String {

    return when (type) {
        Action.Type.HAPPINESS -> stringResource(id = R.string.happiness_expression)
        Action.Type.ANGER -> stringResource(id = R.string.anger_expression)
        Action.Type.ANXIETY -> stringResource(id = R.string.anxiety_expression)
        Action.Type.SADNESS -> stringResource(id = R.string.sadness_expression)
        Action.Type.DEPRESSED -> stringResource(id = R.string.depression_expression)
        else ->
            ""
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeelingItem(){
    IFeelTheme {
        FeelingInfo(type = Action.Type.HAPPINESS, selected = true)
    }
}