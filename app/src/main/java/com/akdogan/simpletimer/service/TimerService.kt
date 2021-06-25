package com.akdogan.simpletimer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.akdogan.simpletimer.Constants.ONGOING_NOTIFICATION_ID
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.timercore.TimerCallback
import com.akdogan.simpletimer.timercore.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TimerService : Service() {
    inner class ServiceBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val TAG = "SERVICE_PLAYGROUND"
    }

    private var timerManager = TimerManager{
        stopSelf()
    }

    private var serviceCallback: TimerCallback? = null

    private var mPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        Log.i(TAG, "onStartCommand Called, service ref: $this")
        mPlayer = MediaPlayer.create(this.applicationContext, R.raw.end_alarm)
        registerCallBack()
        timerManager.startManager()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun registerCallBack(){
        if (serviceCallback == null){
            serviceCallback = object : TimerCallback() {
                override fun onTick(millisCount: Long) {
                    val timeInSeconds = millisCount / 1000
                    Log.i(TAG, "tick: $timeInSeconds")
                    if (timeInSeconds % 5 == 0L) {
                        Log.i(TAG, "Sound should play")
                        playSound()
                        mPlayer?.start()
                    }
                }

                override fun onStart(currentTimer: TimerObject, currentSet: Int) {
                    Log.i(TAG, "New Timer started: $currentTimer Set: $currentSet")
                }

                override fun onDone() {
                    stopSelf()
                }

            }
            serviceCallback?.let{
                timerManager.registerCallback(it)
            }
        }
    }

    override fun onDestroy() {
        mPlayer?.release()
        mPlayer = null
        super.onDestroy()
    }

    private fun startForegroundService(){
        val notification = getServiceNotification(this)
        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }

    private fun playSound(){
        CoroutineScope(Job() + Dispatchers.Default).launch {
            mPlayer?.start()
        }
    }

}