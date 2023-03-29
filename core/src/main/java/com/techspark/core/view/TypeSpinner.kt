package com.techspark.core.view

import android.graphics.ColorSpace
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.R
import com.techspark.core.common.roundedBorder
import com.techspark.core.theme.IFeelTheme

@Composable
fun DropDownList(
    requestToOpen: Boolean = false,
    items: List<String>,
    itemColors: List<Color>,
    request: (Boolean) -> Unit,
    onItemSelected: (Int) -> Unit
) {
    DropdownMenu(
        modifier = Modifier.wrapContentWidth(),
        expanded = requestToOpen,
        onDismissRequest = { request(false) },
    ) {
        items.forEachIndexed() { index, text ->
            DropdownMenuItem(
                onClick = {
                    request(false)
                    onItemSelected(index)
                },
                text = {
                    Text(
                        text, modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start),
                        color = itemColors.getOrElse(index) { Color.Black }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    items: List<String>,
    itemColors: List<Color> = listOf(Color.Black),
    initialText: String = "",
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit = {}
) {

    var text by remember { mutableStateOf(initialText) } // initial value
    var isOpen by remember { mutableStateOf(false) } // initial value
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen = it
    }
    val userSelectedString: (Int) -> Unit = { id ->
        text = items[id]
        onItemSelected(id)
    }
    Box {
        //fetching color from composable
        val color = MaterialTheme.colorScheme.primary
        Column(modifier = modifier
            .padding(10.dp)
          ) {
            Text(
                modifier = Modifier
                    .roundedBorder(color)
                    .padding(10.dp)
                    .clickable(
                        onClick = { isOpen = true }
                    ),
                text = text,
            )
            DropDownList(
                requestToOpen = isOpen,
                items = items,
                itemColors = itemColors,
                openCloseOfDropDownList,
                userSelectedString
            )
        }
    }
}

@Preview(widthDp = 320)
@Composable
fun PreviewSpinner() {
    IFeelTheme {

        Spinner(items = listOf("A", "B", "C"), initialText = "Pick type")
    }
}