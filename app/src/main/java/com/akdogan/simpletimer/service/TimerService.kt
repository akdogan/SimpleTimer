package com.akdogan.simpletimer.service

import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akdogan.simpletimer.Constants.ONGOING_NOTIFICATION_ID
import com.akdogan.simpletimer.Constants.SERVICE_KEY_NUMBER_OF_SETS
import com.akdogan.simpletimer.Constants.SERVICE_KEY_TIMER_LIST
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.data.domain.TimerTransferObject
import com.akdogan.simpletimer.data.domain.getTimeAsString
import com.akdogan.simpletimer.data.domain.millisToSeconds
import com.akdogan.simpletimer.data.domain.toDomain
import com.akdogan.simpletimer.timercore.TimerManager
import kotlinx.coroutines.*

class TimerService : LifecycleService() {
    inner class ServiceBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private lateinit var timerManager: TimerManager
    private var initialized = false

    // Exposed observables
    val currentTime: LiveData<Long> by lazy { timerManager.currentTime }
    val countingUp: LiveData<Boolean> by lazy { timerManager.countingUp }
    val currentSet: LiveData<Int> by lazy { timerManager.currentSet }
    val currentRound: LiveData<Int> by lazy { timerManager.currentRound }
    val allTimersAreFinished: LiveData<Boolean> by lazy { timerManager.allTimersFinished }
    val userHasStopped: LiveData<Boolean>
        get() = _stoppedByUser

    private val _stoppedByUser = MutableLiveData(false)

    private var mPlayer: MediaPlayer? = null

    // Exposed API
    fun userPressedNext() {
        playSound()
        timerManager.userPressedNext()
    }

    fun stopService() = stopSelf()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return ServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        when (intent?.action) {
            ACTION_NEXT_TIMER -> userPressedNext()
            ACTION_STOP_TIMER -> userHasStopped()
        }
        Log.i(TAG, "onStartCommand Called, service ref: $this, intentAction: ${intent?.action}")


        // Setup Soundsystem
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(this.applicationContext, R.raw.end_alarm)
        }

        // Setup timerManager and observers
        if (!initialized) {

            val timerList = intent?.getParcelableArrayListExtra<TimerTransferObject>(
                SERVICE_KEY_TIMER_LIST
            )?.toDomain() ?: emptyList()
            val sets = intent?.getIntExtra(SERVICE_KEY_NUMBER_OF_SETS, 0) ?: 0

            Log.i(TAG, "intent data extracted sets $sets, timerlist $timerList")

            timerManager = TimerManager(sets, timerList)

            setupObservers()
            timerManager.startManager()
            initialized = true

        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupObservers() {

        this.lifecycle.addObserver(timerManager)

        timerManager.currentTime.observe(this) {
            updateNotification(it.millisToSeconds().getTimeAsString())
            if (it == 0L) {
                playSound()
            }
        }

        timerManager.allTimersFinished.observe(this) {
            if (it == true) {
                stopSelf()
            }
        }
    }

    private fun userHasStopped() {
        _stoppedByUser.postValue(true)
        stopSelf()
    }

    private fun getSetsLabel(): String {
        val set = currentSet.value
        val round = currentRound.value
        return this.resources.getString(R.string.set_round_label, set, round)
    }


    override fun onDestroy() {
        mPlayer?.release()
        mPlayer = null
        Log.i(TAG, "Service says goodbye")
        super.onDestroy()
    }

    private fun startForegroundService() {
        val notification = getServiceNotification(this)
        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun updateNotification(time: String) {
        val showNextButton = countingUp.value ?: false
        val notification = getServiceNotification(this, getSetsLabel(), time, showNextButton)
        this.getNotMan().notify(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun playSound() {
        CoroutineScope(Job() + Dispatchers.Default).launch {
            mPlayer?.start()
        }
    }

    companion object {
        private const val TAG = "SERVICE_PLAYGROUND"
        const val ACTION_NEXT_TIMER = "ACTION_NEXT_TIMER"
        const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"
    }

}




