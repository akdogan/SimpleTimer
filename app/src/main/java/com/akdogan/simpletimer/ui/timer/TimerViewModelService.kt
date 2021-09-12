package com.akdogan.simpletimer.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.data.domain.getTimeAsString
import com.akdogan.simpletimer.data.domain.millisToSeconds
import com.akdogan.simpletimer.timercore.TimerCallback

class TimerViewModelService(

) : ViewModel() {


    private val _timerLabel = MutableLiveData<String>()
    val timerLabel: LiveData<String>
        get() = _timerLabel

    private val _countingUp = MutableLiveData(false)
    val countingUp: LiveData<Boolean>
        get() = _countingUp

    private val _currentSet = MutableLiveData(1)
    val currentSet: LiveData<Int>
        get() = _currentSet

    private val _currentRound = MutableLiveData(1)
    val currentRound: LiveData<Int>
        get() = _currentRound

    private var timerCallback: TimerCallback? = null

    init {
        timerCallback = createCallback()
    }

    fun registerCallback(
        register: (TimerCallback) -> Boolean
    ){
        timerCallback?.let{
            register.invoke(it)
        }
    }

    fun unregisterCallback(
        unRegister: (TimerCallback) -> Boolean
    ){
        timerCallback?.let{
            unRegister.invoke(it)
        }
    }

    private fun createCallback(): TimerCallback{
        return object : TimerCallback() {
            override fun onStart(currentTimer: TimerObject, currentRound: Int, currentSet: Int) {
                _timerLabel.postValue("Start")
                _currentRound.postValue(currentRound)
                _currentSet.postValue(currentSet)
            }

            override fun onTick(millisCount: Long) {
                _timerLabel.postValue(millisCount.millisToSeconds().getTimeAsString())
            }

            override fun onFinish(millisCount: Long) {
                _timerLabel.postValue("Done")
            }

            override fun onDone() {
                _timerLabel.postValue("ALL DONE YEAH!")
            }
        }
    }

    fun userStoppedTimer() = {}






}