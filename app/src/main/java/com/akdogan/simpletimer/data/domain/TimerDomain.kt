package com.akdogan.simpletimer.data.domain

import com.akdogan.simpletimer.Constants
import com.akdogan.simpletimer.ui.getTimeAsString

sealed interface MListItem

object AddButton : MListItem

data class TimerDomain(
    val dataBaseId: Int,
    val sort: Int,
    val time: Long,
    val manual: Boolean
): MListItem {
    val label: String
        get() = time.getTimeAsString()
}

fun TimerDomain.toggleTimerType(): TimerDomain {
    return this.copy(manual = !this.manual)
}

fun TimerDomain.incrementTime(): TimerDomain {
    if (this.time >= Constants.TIMER_MAX_VALUE) return this
    return this.copy(time = this.time + Constants.TIMER_INTERVAL)
}

fun TimerDomain.decrementTime(): TimerDomain {
    if (this.time <= Constants.TIMER_INTERVAL) return this
    return this.copy(time = this.time - Constants.TIMER_INTERVAL)
}
