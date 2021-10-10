package com.akdogan.simpletimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import com.akdogan.simpletimer.Constants
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.ui.MainActivity

fun createNotificationChannel(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(
            Constants.NOT_CHANNEL_ID, Constants.NOT_CHANNEL_NAME, importance).apply {
            description = Constants.NOT_CHANNEL_DESC
        }
        context.getNotMan().createNotificationChannel(channel)
    }
}

fun Context.getNotMan(): NotificationManager{
    return getSystemService(this, NotificationManager::class.java) as NotificationManager
}

fun getServiceNotification(
    context: Context,
    title: String = "",
    body: String = "",
    showNextButton: Boolean = false
): Notification{

    val intent = Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val notification: Notification = Notification.Builder(context,
        Constants.NOT_CHANNEL_ID
    )
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.ic_baseline_timer_24)
        .setContentIntent(pendingIntent)
        .setTicker("This seems to be the ticker text")
        .also {
            if (showNextButton){
                it.addAction(getActionNext(context))
            }
        }
        .addAction(getActionStop(context))
        .build()
    return notification
}


fun getActionNext(context: Context): Notification.Action {
    val intent = Intent(context, TimerService::class.java)
        .setAction(TimerService.ACTION_NEXT_TIMER)
    val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
    return Notification.Action.Builder(null, "NEXT", pendingIntent ).build()
}

fun getActionStop(context: Context): Notification.Action{
    val intent = Intent(context, TimerService::class.java)
        .setAction(TimerService.ACTION_STOP_TIMER)
    val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
    return Notification.Action.Builder(null, "STOP", pendingIntent).build()
}