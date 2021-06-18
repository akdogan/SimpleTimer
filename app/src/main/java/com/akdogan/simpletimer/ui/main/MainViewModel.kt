package com.akdogan.simpletimer.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.akdogan.simpletimer.Consts.DEFAULT_TIMERS_ON_START
import com.akdogan.simpletimer.data.domain.AddButton
import com.akdogan.simpletimer.data.domain.MListItem
import com.akdogan.simpletimer.data.domain.TimerObject
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var numberOfSetsInternal = 1

    private val _numberOfSets = MutableLiveData(1)
    val numberOfSets : LiveData<Int>
        get() = _numberOfSets

    fun incrementSet() {
        numberOfSetsInternal++
        _numberOfSets.postValue(numberOfSetsInternal)
    }

    fun decrementSet(){
        if (numberOfSetsInternal > 1){
            numberOfSetsInternal--
            _numberOfSets.postValue(numberOfSetsInternal)
        }
    }

    private val backingListOfTimers: MutableList<TimerObject> = mutableListOf()
    private val _listOfTimers = MutableLiveData<List<MListItem>>(backingListOfTimers)

    val listOfTimers: LiveData<List<MListItem>>  = Transformations.map(_listOfTimers){
        val tempList: MutableList<MListItem> = mutableListOf()
        tempList.addAll(it)
        tempList.add(AddButton)
        Log.i("VIEWMODEL TRACING", "Transformations called with templist: $tempList")
        return@map tempList
    }

    init {
        viewModelScope.launch{
            val tempList = mutableListOf<TimerObject>()
            DEFAULT_TIMERS_ON_START.forEach {
                tempList.add(TimerObject(it))
            }
            backingListOfTimers.addAll(tempList)
        }

    }

    fun getTimerList(): List<TimerObject> = backingListOfTimers



    fun addTimer(){
        Log.i("VIEWMODEL_TRACING", "Add Timer called")
        backingListOfTimers.add(TimerObject())
        _listOfTimers.value = backingListOfTimers
    }

    fun removeTimer(index: Int){
        backingListOfTimers.removeAt(index)
        _listOfTimers.value = backingListOfTimers
    }

}