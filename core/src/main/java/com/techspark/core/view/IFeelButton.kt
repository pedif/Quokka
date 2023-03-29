package com.techspark.core.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.techspark.core.util.BUTTON_HORIZONTAL_PADDING
import com.techspark.core.util.BUTTON_VERTICAL_PADDING

@Composable
fun IFeelButton(text:String, modifier: Modifier=Modifier,
onClicked:()->Unit = {}) {
    Button(
        onClick = onClicked,
        contentPadding = PaddingValues(
            horizontal = dimensionResource(id = BUTTON_HORIZONTAL_PADDING),
            vertical = dimensionResource(id = BUTTON_VERTICAL_PADDING)
        ),
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier.defaultMinSize(1.dp, 1.dp)
    ) {
        Text(text = text)
    }
}