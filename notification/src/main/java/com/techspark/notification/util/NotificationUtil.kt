package com.techspark.notification.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.techspark.core.model.Action
import com.techspark.core.util.CURRENT_FEELING_NOTHING
import com.techspark.core.util.CURRENT_FEELING_TITLE
import com.techspark.notification.NotificationActivity
import com.techspark.notification.R
import timber.log.Timber

object NotificationUtil {

    private const val CHANNEL_ID = "current_feeling"
    private const val NOTIFICATION_ID = 1010
    fun notify(context: Context, title: String, subtitle: String? = null) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, android.R.color.white))
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSubText(context.getString(R.string.notification_feeling_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, builder.build())
            }else{
                Toast.makeText(context,
                    context.resources.getString(R.string.notification_permission_not_granted),
                Toast.LENGTH_LONG).show()
            }
        }

    }

     fun removeNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_ID)
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOngoingNotification(context: Context, ongoing:Action?=null){

            Timber.e("called ${ongoing==null}")
            if (ongoing == null) {
                NotificationUtil.notify(
                    context,
                    context.resources.getString(CURRENT_FEELING_NOTHING)
                )
            } else {
                val solution = ongoing.getSolution(context)
                NotificationUtil.notify(
                    context,
                    String.format(
                        context.resources.getString(CURRENT_FEELING_TITLE),
                        context.resources.getString(ongoing.type.adjective)
                    ),
                    solution.getOrNull(Math.random().toInt() * solution.size)

                )
            }
        }

}
