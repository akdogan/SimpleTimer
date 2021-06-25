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