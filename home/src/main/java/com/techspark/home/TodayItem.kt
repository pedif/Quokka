package com.techspark.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.common.DataHelper
import com.techspark.core.common.DateUtil
import com.techspark.core.model.Action
import com.techspark.core.model.Day
import com.techspark.core.theme.IFeelTheme
import com.techspark.core.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayItem(
    currentAction: Action?,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit = {}
) {

    Card(modifier = modifier.fillMaxWidth().semantics { contentDescription = "today" },
        onClick = { onClick(currentAction?.dayId?:
    DateUtil.getStartOfDay(System.currentTimeMillis())) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = com.techspark.core.R.dimen.margin_small)),
            modifier = modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.today_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if(currentAction !=null) {
                Text(
                    text = getSolutionText(type = currentAction.type),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                if (currentAction.type != Action.Type.HAPPINESS &&
                    currentAction.type != Action.Type.NO_INPUT
                ) {
                    Text(
                        text = stringResource(id = R.string.today_solution_label),
                        style = MaterialTheme.typography.bodySmall,
                    )
                   val actions = currentAction.getSolution(LocalContext.current)
                    actions.forEachIndexed { i, solution ->
                        Text(
                            text = "${i + 1}.$solution",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }else{
                Text(
                    text = stringResource(id = CURRENT_FEELING_NOTHING),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.action_go_next),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = modifier.alignByBaseline()
                )
                Image(
                    imageVector = Icons.Default.ArrowForward, contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(16.dp, 16.dp)
                )
            }
        }
    }

}


@Composable
private fun getSolutionText(type: Action.Type): AnnotatedString {
    return buildAnnotatedString {

        val adj = stringResource(id = type.adjective)
        val summary = stringResource(
            id = CURRENT_FEELING_TITLE,
            adj
        )
        append(summary)
        //we want the adjective to have a different style
        val adjStartedIndex = summary.indexOf(adj)
        addStyle(
            SpanStyle(type.color, fontWeight = FontWeight.Bold),
            adjStartedIndex,
            adjStartedIndex + adj.length
        )
    }
}

@Preview
@Composable
private fun previewTodayItem() {
    IFeelTheme {
        TodayItem(currentAction = (DataHelper.getDays(System.currentTimeMillis())[0]).actions[0])
    }
}