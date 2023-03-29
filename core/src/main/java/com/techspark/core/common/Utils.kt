package com.techspark.core.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat.startActivity
import com.techspark.core.R
import timber.log.Timber


@Composable
fun Float.dp() = with(LocalDensity.current) { Dp(this@dp).toSp() }

@Composable
fun Int.dp() = with(LocalDensity.current) { Dp(this@dp.toFloat()).toSp() }


@Composable
fun Modifier.roundedBorder(
    color: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = dimensionResource(id = R.dimen.border_thickness),
    cornerRadius: Dp = dimensionResource(id = R.dimen.corner_size)
): Modifier =
    drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            style = Stroke(borderWidth.toPx())
        )
    }


fun openLink(activity: Activity, link: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(link)
    activity.startActivity(i)
}

fun openEmail(activity: Activity) {
    with(activity) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(
            Intent.EXTRA_EMAIL, arrayOf(
                "quokka.techspark@gmail.com"
            )
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Timber.e(ex.message)
        }
    }
}
    fun openStorePage(activity: Activity){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.techspark.quokka")
            setPackage("com.android.vending")
        }
        activity.startActivity(intent)
    }
