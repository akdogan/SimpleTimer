package com.akdogan.simpletimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import com.akdogan.simpletimer.Constants

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