package com.akdogan.simpletimer.ui.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akdogan.simpletimer.service.TimerService

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, TimerService::class.java)
            .setAction("")
    }
}