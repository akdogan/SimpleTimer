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
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            Constants.NOT_CHANNEL_ID, Constants.NOT_CHANNEL_NAME, importance).apply {
            description = Constants.NOT_CHANNEL_DESC
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

fun getServiceNotification(context: Context): Notification{

    val intent = Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)


    val notification: Notification = Notification.Builder(context,
        Constants.NOT_CHANNEL_ID
    )
        .setContentTitle("SERVICE TEST")
        .setContentText("the service is runnig boi!!")
        .setSmallIcon(R.drawable.ic_baseline_timer_24)
        .setContentIntent(pendingIntent)
        .setTicker("This seems to be the ticker text")
        .build()
    return notification
}