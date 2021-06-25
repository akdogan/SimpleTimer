package com.akdogan.simpletimer.timercore

import android.util.Log
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.ui.CountDownTimerCoroutine
import com.akdogan.simpletimer.ui.CountUpTimerCoroutine

class TimerManager(
    private val tearDownCallback: () -> Unit
) {
    private var running = false
    private val callbacks: MutableList<TimerCallback> = mutableListOf()

    /**
     * Registers a callback. Keep a reference to unregister.
     * Existing Callbacks will not be registered twice.
     * @return true if the callback was added, false otherwise
     */
    fun registerCallback(callback: TimerCallback): Boolean{
        synchronized(callbacks){
            return if (callbacks.find { it == callback } == null){
                callbacks.add(callback)
                true
            } else {
                false
            }
        }
    }

    /**
     * Unregisters a callback.
     * @return true if the callback was removed, false otherwise
     */
    fun unRegisterCallback(callback: TimerCallback): Boolean{
        synchronized(callbacks){
            return callbacks.remove(callback)
        }
    }

    companion object{
        const val TAG = "TIMER_MANAGER_TEST"
    }

    // Timer Configuration relevant entities
    private val numberOfSets: Int = 2
    private val timerListTemplate: List<TimerObject> = listOf(
        TimerObject(5),
        TimerObject(10),
        TimerObject(15)
    )
    private var timerList: MutableList<TimerObject> = mutableListOf()
    private var timerInternal: CountUpTimerCoroutine? = null
    private var currentSet: Int = 0
    private var currentRound: Int = 0

    fun startManager(){
        if (!running){
            running = true
            startNextSet()
        }

    }

    private fun startNextSet() {
        if (currentSet < numberOfSets) {
            timerList = timerListTemplate.toMutableList()
            currentSet++
            startNextTimer()
        } else {
            allTimersAreFinished()
        }
    }

    private fun allTimersAreFinished() {
        Log.i(TAG, "All timers finished")
        executeOnDoneCallbacks()
        running = false
    }

    private fun startNextTimer() {
        timerInternal = null
        if (timerList.size > 0) {
            timerInternal = createTimer(timerList[0]).startTimer()
            timerList.removeAt(0)
            currentRound = timerListTemplate.size - timerList.size
        } else {
            startNextSet()
        }
    }

    private fun executeOnTickCallbacks(millis: Long){
        callbacks.forEach {
            it.onTick(millis)
        }
    }

    private fun executeOnFinishCallbacks(millis: Long){
        callbacks.forEach {
            it.onFinish(millis)
        }
    }

    private fun executeOnStartCallbacks(t: TimerObject){
        callbacks.forEach {
            it.onStart(t, currentSet)
        }
    }

    private fun executeOnDoneCallbacks(){
        callbacks.forEach {
            it.onDone()
        }
    }

    private fun createTimer(t: TimerObject): CountUpTimerCoroutine {
        executeOnStartCallbacks(t)
        val onTick = { millis: Long ->
            executeOnTickCallbacks(millis)
        }
        val onFinish = { millis: Long ->
            executeOnFinishCallbacks(millis)
            startNextTimer()
        }
        return if (t.timerTypeAutomatic) {
            object : CountDownTimerCoroutine(t.time * 1000, 1000) {
                override fun onTick(millis: Long) { onTick(millis) }
                override fun onFinish(millis: Long) { onFinish(millis) }
            }
        } else {
            object : CountUpTimerCoroutine(1000) {
                override fun onTick(millis: Long) { onTick(millis) }
                override fun onFinish(millis: Long) { onFinish(millis) }
            }
        }

    }

}