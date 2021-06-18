package com.akdogan.simpletimer.ui.timer


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.data.domain.getTimeAsString
import com.akdogan.simpletimer.data.domain.millisToSeconds
import com.akdogan.simpletimer.ui.CountDownTimerCoroutine
import com.akdogan.simpletimer.ui.CountUpTimerCoroutine

class TimerViewModel(
    private val numberOfSets: Int,
    private val timerListTemplate: List<TimerObject>
) : ViewModel() {

    private var timerInternal: CountUpTimerCoroutine? = null
    private var timerList: MutableList<TimerObject>
    private var currentSetInternal: Int = 0
        set(value) {
            field = value
            _currentSet.postValue(value)
        }

    private val _timerLabel = MutableLiveData<String>()
    val timerLabel: LiveData<String>
        get() = _timerLabel

    private val _playSound = MutableLiveData(false)
    val playSound: LiveData<Boolean>
        get() = _playSound

    private val _countingUp = MutableLiveData(false)
    val countingUp: LiveData<Boolean>
        get() = _countingUp

    private val _currentSet = MutableLiveData(1)
    val currentSet: LiveData<Int>
        get() = _currentSet

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int>
        get() = _currentRound

    init {
        timerList = timerListTemplate.toMutableList()
    }

    fun onPlaySoundDone() = _playSound.postValue(false)
    fun userStoppedTimer() = timerInternal?.stopTimer()

    fun startNextSet() {
        if (currentSetInternal < numberOfSets) {
            timerList = timerListTemplate.toMutableList()
            currentSetInternal++
            startNextTimer()
        } else {
            allTimersAreFinished()
        }
    }

    private fun allTimersAreFinished() = _timerLabel.postValue("YEAH!!!")

    private fun startNextTimer() {
        _timerLabel.postValue("START")
        timerInternal = null
        if (timerList.size > 0) {
            timerInternal = createTimer(timerList[0]).startTimer()
            timerList.removeAt(0)
            val round = timerListTemplate.size - timerList.size
            _currentRound.postValue(round)
        } else {
            startNextSet()
        }
    }

    private fun createTimer(t: TimerObject): CountUpTimerCoroutine {
        val onTick = { millis: Long ->
            _timerLabel.postValue(millis.millisToSeconds().getTimeAsString())
        }
        val onFinish = { millis: Long ->
            _playSound.postValue(true)
            startNextTimer()
        }
        return if (t.timerType) {
            _countingUp.postValue(false)
            object : CountDownTimerCoroutine(t.time * 1000, 1000) {
                override fun onTick(millis: Long) { onTick(millis) }
                override fun onFinish(millis: Long) { onFinish(millis) }
            }
        } else {
            _countingUp.postValue(true)
            object : CountUpTimerCoroutine(1000) {
                override fun onTick(millis: Long) { onTick(millis) }
                override fun onFinish(millis: Long) { onFinish(millis) }
            }
        }

    }
}

@Suppress("UNCHECKED_CAST")
class TimerViewModelFactory(
    val numberOfSets: Int,
    val listOfTimers: List<TimerObject>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(numberOfSets, listOfTimers) as T
        } else {
            throw IllegalArgumentException(
                "Wrong ViewModel Class! Expected " +
                        "${TimerViewModel::class.java} but found $modelClass"
            )
        }
    }
}