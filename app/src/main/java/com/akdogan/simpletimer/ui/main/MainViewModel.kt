package com.akdogan.simpletimer.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.akdogan.simpletimer.Constants
import com.akdogan.simpletimer.data.domain.AddButton
import com.akdogan.simpletimer.data.domain.MListItem
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.data.repository.DataRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: DataRepository
) : ViewModel() {
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

    private val backingListOfTimers : MutableList<TimerObject> = mutableListOf()

    private val _listOfTimers = MutableLiveData<List<MListItem>>()

    val listOfTimers: LiveData<List<MListItem>>  = Transformations.map(_listOfTimers){
        val tempList: MutableList<MListItem> = mutableListOf()
        tempList.addAll(it)
        tempList.add(AddButton)
        Log.i("VIEWMODEL TRACING", "Transformations called with templist: $tempList")
        return@map tempList
    }

    init {
        viewModelScope.launch{
            val recoveredList = repo.loadTimers()
            Log.i("DB_TEST", "testList is: $recoveredList")

            if (recoveredList.isNotEmpty()){
                backingListOfTimers.addAll(recoveredList)
            } else {
                val tempList = mutableListOf<TimerObject>()
                Constants.DEFAULT_TIMERS_ON_START.forEach {
                    tempList.add(TimerObject(it))
                }
                backingListOfTimers.addAll(tempList)
            }
            _listOfTimers.postValue(backingListOfTimers)
        }
    }

    fun getTimerList(): List<TimerObject> = backingListOfTimers



    fun addTimer(){
        Log.i("VIEWMODEL_TRACING", "Add Timer called")
        backingListOfTimers.add(TimerObject())
        _listOfTimers.value = backingListOfTimers
        //saveCurrentTimers()
    }

    fun removeTimer(index: Int){
        backingListOfTimers.removeAt(index)
        _listOfTimers.value = backingListOfTimers
        //saveCurrentTimers()
    }

    // TODO saving only when a new timer is added, but also needs to save when timers are changed
    private fun saveCurrentTimers() {
        viewModelScope.launch {
            repo.saveTimers(backingListOfTimers)
        }
    }

}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repo: DataRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if ( modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repo) as T
        }
        else throw IllegalArgumentException(
            "Wrong ViewModel Class! Expected ${MainViewModel::class.java} found $modelClass"
        )
    }
}