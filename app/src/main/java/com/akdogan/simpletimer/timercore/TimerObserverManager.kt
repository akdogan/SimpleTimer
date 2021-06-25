package com.akdogan.simpletimer.timercore

import com.akdogan.simpletimer.data.domain.TimerObject
import java.util.*

abstract class TimerCallback {

    /**
     * Gets invoked every second.
     * @param millisCount the number of millis elapsed or left on the timer, depending on the
     * type of timer (manual or automatic)
     */
    open fun onTick(millisCount: Long){}

    /**
     * Gets invoked when the current timer is finished.
     * @param millisCount the final count of milliseconds

     */
    open fun onFinish(millisCount: Long){}

    /**
     * Called when a new timer starts
     * @param nextTimer the next Timer in the row, or null if this was the last timer overall
     * @param nextSet Number of the next set, or null if this was the last set
     */
    open fun onStart(currentTimer: TimerObject, currentSet: Int){}

    /**
     * Called when all timers are completely done
     */
    open fun onDone(){}
}

internal class TimerObserverManager() {
    private data class TimerObserver(
        val invokeFunction: () -> Unit?,
    ) {
        val id = UUID.randomUUID().toString()
    }

    private val onTickObservers: MutableList<TimerObserver> = mutableListOf()
    private val onFinishObservers: MutableList<TimerObserver> = mutableListOf()

    private fun onTickExecuted() = onTickObservers.forEach {
        it.invokeFunction.invoke()
    }

    private fun onFinishExecuted(
        millisCount: Long,
        nextTimer: TimerObject?,
        nextSet: Int?
    ) = onFinishObservers.forEach {
        it.invokeFunction.invoke()
    }

    /**
     * Register a function that is invoked on every tick
     * @param invokeFunction function to invole on every tick
     * @return unique ID for this listener; use it to remove the listener
     * with [unRegisterOnTickObserver]
     */
    fun registerOnTickObserver(invokeFunction: () -> Unit?): String{
        val observer = TimerObserver(invokeFunction)
        onTickObservers.add(observer)
        return observer.id
    }

    /**
     * Unregister a previously registered observer
     * @param uniqueId the id received when registering
     * @return true when an observer was succesfully removed, false otherwise
     */
    fun unRegisterOnTickObserver(uniqueId: String): Boolean{
        return onTickObservers.removeIf { it.id == uniqueId }
    }

    /**
     * Register a function that is invoked when the current timer is finished
     * @param invokeFunction function to invoke on finish
     * @return unique ID for this listener; use it to remove the listener
     * with [unRegisterOnFinishObserver]
     */
    fun registerOnFinishObserver(invokeFunction: () -> Unit?): String {
        val observer = TimerObserver(invokeFunction)
        onFinishObservers.add(observer)
        return observer.id
    }

    /**
     * Unregister a previously registered observer
     * @param uniqueId the id received when registering
     * @return true when an observer was successfully removed, false otherwise
     */
    fun unRegisterOnFinishObserver(uniqueId: String): Boolean{
        return onFinishObservers.removeIf { it.id == uniqueId }
    }

}