package com.akdogan.simpletimer.data.domain


import android.os.Parcel
import android.os.Parcelable
import com.akdogan.simpletimer.Consts.TIMER_INTERVAL
import com.akdogan.simpletimer.Consts.TIMER_MAX_VALUE

sealed class MListItem

object AddButton : MListItem()

data class TimerTransferObject(
    val time : Long,
    val countDown: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeByte(if (countDown) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimerTransferObject> {
        override fun createFromParcel(parcel: Parcel): TimerTransferObject {
            return TimerTransferObject(parcel)
        }

        override fun newArray(size: Int): Array<TimerTransferObject?> {
            return arrayOfNulls(size)
        }
    }
}

fun TimerObject.toTransferObject(): TimerTransferObject =
    TimerTransferObject(this.time, this.timerType)

fun List<TimerObject>.toTransfer(): List<TimerTransferObject> =
    this.map{ it.toTransferObject()}

fun TimerTransferObject.toTimerObject(): TimerObject =
    TimerObject(this.time, this.countDown)

fun ArrayList<TimerTransferObject>.toDomain(): List<TimerObject> =
    this.map{ it.toTimerObject()}


class TimerObject(
    initialTime : Long = 60L,
    initialTimerType: Boolean = true
    ): MListItem() {

    var time : Long = initialTime
        set(value){
            if (value in 0..TIMER_MAX_VALUE){
                field = value
            }
        }

    var timerType = initialTimerType
        private set

    val label: String
        get() = time.getTimeAsString()

    /**
     * Toggles the type of timer between:
     * true - Countdown timer
     * false - manual timer
     * TODO should be replaced with enum
     * @return the new timer type after it was toggled
     */
    fun toggleTimerType(): Boolean {
        timerType = !timerType
        return timerType
    }

    fun incrementTime(){
        if (time < TIMER_MAX_VALUE) time += TIMER_INTERVAL
    }

    fun decrementTime(){
        if (time > TIMER_INTERVAL) time -= TIMER_INTERVAL
    }

}

fun Long.getTimeAsString(): String {
    fun Long.getStringPart(): String = when {
        this >= 10 -> this.toString()
        this >= 1 -> "0$this"
        else -> "00"
    }

    if (this > TIMER_MAX_VALUE || this < 0){
        throw IllegalArgumentException("Source is out of range. Must be between 0 and 5999 (99:59)")
    }
    val minutes = this / 60
    val seconds = this - (minutes * 60)
    return "${minutes.getStringPart()}:${seconds.getStringPart()}"
}

fun Long.millisToSeconds(): Long = this / 1000