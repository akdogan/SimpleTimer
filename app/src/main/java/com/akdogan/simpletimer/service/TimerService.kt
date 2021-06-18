package com.akdogan.simpletimer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.akdogan.simpletimer.Consts
import com.akdogan.simpletimer.Consts.ONGOING_NOTIFICATION_ID
import com.akdogan.simpletimer.ui.CountDownTimerCoroutine
import com.akdogan.simpletimer.ui.MainActivity

class TimerService : Service() {
    companion object {
        const val TAG = "SERVICE_PLAYGROUND"
    }
    var timer: CountDownTimerCoroutine? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startForegroundService(){
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = Notification.Builder(this,
            Consts.NOT_CHANNEL_ID
        )
            .setContentTitle("SERVICE TEST")
            .setContentText("the service is runnig boi!!")
            //.setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setTicker("This seems to be the ticket text")
            .build()

        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }

    private fun startTimer(){
        timer = object : CountDownTimerCoroutine(60000, 1000) {
            override fun onTick(millis: Long) {
                Log.i(TAG, "Service Timer tick: ${millis  / 1000}")
            }

            override fun onFinish(millis: Long) {
                Log.i(TAG, "Service Timer DONE: ${millis  / 1000}")
                stopForeground(true)
                stopSelf()
            }

        }
    }
}