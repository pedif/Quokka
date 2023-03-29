package com.techspark.statistics

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.*

@Composable
fun ActionList(
    items: List<Action>,
    modifier: Modifier = Modifier,
    onItemClicked: (Long) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(dimensionResource(MARGIN_LARGE)),
    ) {
        items(items,
            key = { item -> item.id }) { item ->
            ListItem(item, modifier){
                onItemClicked(item.id)
            }
            Divider(
                modifier = modifier
                    .padding(dimensionResource(MARGIN_MEDIUM),
                        dimensionResource(MARGIN_MEDIUM),
                        0.dp,
                        dimensionResource(MARGIN_MEDIUM)),
                thickness = dimensionResource(MARGIN_MEDIUM)
            )
        }
    }
}

@Preview(heightDp = 600, showBackground = true)
@Composable
fun PreviewList() {
    IFeelTheme {
        ActionList(items = DataHelper.getFeelings(System.currentTimeMillis()))
    }
}