package com.akdogan.simpletimer.timercore

import androidx.lifecycle.*
import com.akdogan.simpletimer.data.domain.TimerDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerManager @AssistedInject constructor(
    @Assisted private val numberOfSets: Int,
    @Assisted private val timerListTemplate: List<TimerDomain>,
    @Assisted private val coroutineScope: CoroutineScope
) : LifecycleEventObserver {

    var running = false

    private val _currentTime = MutableStateFlow<Long>(0)
    val currentTime: StateFlow<Long>
        get() = _currentTime

    private val _countingUp = MutableLiveData(false)
    val countingUp: LiveData<Boolean>
        get() = _countingUp

    private val _currentSet = MutableLiveData(1)
    val currentSet: LiveData<Int>
        get() = _currentSet

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int>
        get() = _currentRound

    private val _allTimersFinished = MutableLiveData(false)
    val allTimersFinished : LiveData<Boolean>
        get() = _allTimersFinished

    private var timerList: MutableList<TimerDomain> = mutableListOf()
    private var timerInternal: CountUpTimerCoroutine? = null
    private var currentSetInternal: Int = 0
        set(value) {
            field = value
            _currentSet.postValue(value)
        }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) timerInternal?.cancel()
    }

    fun startManager() {
        if (!running) {
            running = true
            startNextSet()
        }
    }

    private fun startNextSet() {
        if (currentSetInternal < numberOfSets) {
            timerList = timerListTemplate.toMutableList()
            currentSetInternal++
            startNextTimer()
        } else {
            allTimersAreFinished()
        }
    }

    private fun allTimersAreFinished() {
        running = false
        _allTimersFinished.postValue(true)
    }

    private fun startNextTimer() {
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

    private fun createTimer(t: TimerDomain): CountUpTimerCoroutine {
        return if (!t.manual) {
            _countingUp.postValue(false)
            object : CountDownTimerCoroutine(t.time * 1000, 1000, coroutineScope) {
                override suspend fun onTick(millis: Long) {
                    _currentTime.emit(millis)
                }

                override fun onFinish(millis: Long) {
                    startNextTimer()
                }
            }
        } else {
            _countingUp.postValue(true)
            object : CountUpTimerCoroutine(1000, coroutineScope) {
                override suspend fun onTick(millis: Long) {
                    _currentTime.emit(millis)
                }

                override fun onFinish(millis: Long) {
                    startNextTimer()
                }
            }
        }
    }

    fun userPressedNext() {
        timerInternal?.stopTimer()
    }
}
