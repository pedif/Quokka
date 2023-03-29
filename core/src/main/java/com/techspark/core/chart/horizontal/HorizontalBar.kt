package com.techspark.core.chart.horizontal

/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techspark.core.R
import com.techspark.core.model.Action
import com.techspark.core.theme.IFeelTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SleepBar(
    typePair: Pair<Action.Type, Int>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        SleepRoundedBar(
            typePair
        )
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun SleepRoundedBar(
    typePair: Pair<Action.Type, Int>
) {
    val textMeasurer = rememberTextMeasurer()
    Spacer(
        modifier = Modifier
            .drawWithCache {
                val width = this.size.width
                val cornerRadiusStartPx = 2.dp.toPx()
                val collapsedCornerRadiusPx = 10.dp.toPx()
                val animatedCornerRadius = CornerRadius(
                    cornerRadiusStartPx
                )

                val lineThicknessPx = lineThickness.toPx()
                val roundedRectPath = Path()
                roundedRectPath.addRoundRect(
                    RoundRect(
                        rect = Rect(
                            Offset(x = 0f, y = -lineThicknessPx / 2f),
                            Size(
                                this.size.width + lineThicknessPx * 2,
                                this.size.height + lineThicknessPx
                            )
                        ),
                        cornerRadius = animatedCornerRadius
                    )
                )
//                val roundedCornerStroke = Stroke(
//                    lineThicknessPx,
//                    cap = StrokeCap.Round,
//                    join = StrokeJoin.Round,
//                    pathEffect = PathEffect.cornerPathEffect(
//                        cornerRadiusStartPx
//                    )
//                )
                val barHeightPx = barHeight.toPx()

//                val sleepGraphPath = generateSleepPath(
//                    this.size,
//                    0, width, barHeightPx, 0f,
//                    lineThickness.toPx() / 2f
//                )

                val baseColor = typePair.first.color
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(baseColor, baseColor)
                )
                val text =
                    if (typePair.first.emoji.isNotEmpty() &&
                        typePair.second >= 60
                    ) "${typePair.second} , ${typePair.first.emoji}"
                    else
                        "${typePair.second}"
                val textResult = textMeasurer.measure(
                    AnnotatedString(text)
                )
                val cornerRadius = CornerRadius(4.dp.toPx())
                onDrawBehind {
                    drawSleepBar(
                        roundedRectPath,
                        gradientBrush,
                        textResult,
                        cornerRadiusStartPx,
                        cornerRadius,
                        typePair.second != 0
                    )
                }
            }
            .height(dimensionResource(id = R.dimen.horizontal_bar_height))
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawSleepBar(
    roundedRectPath: Path,
    gradientBrush: Brush,
    textResult: TextLayoutResult,
    cornerRadiusStartPx: Float,
    cornerRadius: CornerRadius,
    shouldDrawText: Boolean = true
) {
//    clipPath(roundedRectPath) {
//        drawPath(sleepGraphPath,
//            brush = gradientBrush)
////        drawPath(
////            sleepGraphPath,
////            style = roundedCornerStroke,
////            brush = gradientBrush
////        )
//    }
    with(roundedRectPath.getBounds()) {
        drawRoundRect(
            gradientBrush,
            topLeft, size,
            cornerRadius = cornerRadius
        )
    }
    if (shouldDrawText) {
        translate(0f) {
            drawText(
                textResult,
                topLeft = Offset(textPadding.toPx(), cornerRadiusStartPx)
            )
        }
    }
}

/**
 * Generate the path for the different sleep periods.
 */
private fun generateSleepPath(
    canvasSize: Size,
    duration: Int,
    width: Float,
    barHeightPx: Float,
    heightAnimation: Float,
    lineThicknessPx: Float,
): Path {
    val path = Path()
    // step 2 - add the current sleep period as rectangle to path
    path.addRect(
        rect = Rect(
            offset = Offset(x = 0 * width + lineThicknessPx, y = 0f),
            size = canvasSize.copy(width = width, height = barHeightPx)
        )
    )
    return path
}


@Preview
@Composable
fun SleepBarPreview() {

    SleepBar(Pair(Action.Type.HAPPINESS, 2))
}

private val lineThickness = 2.dp
private val barHeight = 24.dp
private const val animationDuration = 500
private val textPadding = 4.dp
