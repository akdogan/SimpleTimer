package com.akdogan.simpletimer.ui

import kotlinx.coroutines.*
import kotlin.math.absoluteValue

/**
 * Creates a Timer that can count up indefinitely.
 * This timer is using the Globalscope. Make sure to stop it when it is no longer required.
 * Can savely be paused and resumed with [startTimer] / [stopTimer]
 * The timer cannot be reset. Use a new instance instead.
 * @param countInterval the interval to count in milliseconds
 */

abstract class CountUpTimerCoroutine(protected val countInterval: Long){
    protected var millisCount = 0L
    private var timerJob: Job? = null

    /**
     * Called on every tick
     * @param millis: Milliseconds that have elapsed since the start of the timer
     */
    abstract fun onTick(millis: Long)

    /**
     * Called when the timer was finished through onStop()
     * @param millis: The final amount of milliseconds that have passed
     */
    abstract fun onFinish(millis: Long)

    /**
     * Starts / resumes the timer
     */
    open fun startTimer(): CountUpTimerCoroutine{


        /*val scope = CoroutineScope(Job() + Dispatchers.Default)
        timerJob = scope.launch(Dispatchers.Default) {
            run()
        }*/
        timerJob = GlobalScope.launch(Dispatchers.Default) {
            run()
        }
        return this
    }

    /**
     * Stops / pauses the timer
     */
    fun stopTimer(){
        timerJob?.cancel()
        onFinish(millisCount)
        }

    protected open suspend fun run(){
        delay(countInterval)
        while (true){
            onTick(millisCount)
            millisCount += countInterval
            delay(countInterval)
        }
    }
}

/**
 * Creates a Coroutine countdown timer. See [CountUpTimerCoroutine] for more details.
 * @param millisInFuture amount of time to count down in milliseconds
 * @param countInterval the interval to count in milliseconds
 */
abstract class CountDownTimerCoroutine(
    millisInFuture: Long,
    countInterval: Long
) : CountUpTimerCoroutine(countInterval){
    private val millisInFutureNormalized = millisInFuture.absoluteValue

    override suspend fun run(){
        while (true){
            if (millisCount > millisInFutureNormalized){
                super.stopTimer()
            } else {
                onTick(millisInFutureNormalized - millisCount)
            }
            millisCount += countInterval
            delay(countInterval)
        }
    }

    /**
     * Called on every tick
     * @param millis Milliseconds left for this timer
     */
    abstract override fun onTick(millis: Long)

    override fun startTimer(): CountDownTimerCoroutine {
        super.startTimer()
        return this
    }
}
