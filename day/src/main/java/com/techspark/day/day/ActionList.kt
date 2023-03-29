package com.techspark.day.day

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.model.Action
import com.techspark.core.model.Day
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.MARGIN_LARGE
import com.techspark.core.util.MARGIN_MEDIUM
import com.techspark.day.R

@Composable
fun ActionList(
    day: Day,
    items: List<Action>,
    modifier: Modifier = Modifier,
    onItemSelected: (Long) -> Unit = {},
    onItemDelete: (Long) -> Unit = {},
    onLinkSelected: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            DayCard(
                day,
                onLinkSelected = onLinkSelected
            )
        }
        item {
            Spacer(modifier = Modifier.height(dimensionResource(id = MARGIN_LARGE)))
        }
        items(items) { item ->
            ListItem(item,
                modifier = Modifier.padding(horizontal = dimensionResource(id = MARGIN_LARGE))
                    .semantics { testTag = "tag"},
                onClick = { onItemSelected(item.id) },
                onDelete = { onItemDelete(item.id) })
            Divider(
                modifier = modifier
                    .padding(
                        dimensionResource(id = MARGIN_MEDIUM),
                        dimensionResource(id = MARGIN_MEDIUM),
                        0.dp,
                        dimensionResource(id = MARGIN_MEDIUM)
                    ),
                thickness = dimensionResource(id = R.dimen.divider_thickness)
            )
        }
    }
}

@Preview(heightDp = 600, showBackground = true)
@Composable
fun PreviewList() {
    IFeelTheme {
        ActionList(
            Day(System.currentTimeMillis(), DataHelper.getFeelings(System.currentTimeMillis())),
            items = DataHelper.getFeelings(System.currentTimeMillis())
        )
    }
}