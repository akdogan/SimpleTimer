package com.akdogan.simpletimer.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_DESC
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_ID
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_NAME
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        com.akdogan.simpletimer.service.createNotificationChannel(this)
    }

    @Deprecated("Use static createNotificationChannel(context: Context)")
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NOT_CHANNEL_ID, NOT_CHANNEL_NAME, importance).apply {
                description = NOT_CHANNEL_DESC
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}