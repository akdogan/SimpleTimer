package com.akdogan.simpletimer.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akdogan.simpletimer.data.domain.AddButton
import com.akdogan.simpletimer.data.domain.TimerDomain
import com.akdogan.simpletimer.data.domain.decrementTime
import com.akdogan.simpletimer.data.domain.incrementTime
import com.akdogan.simpletimer.data.domain.toggleTimerType
import com.akdogan.simpletimer.data.repository.DataRepository
import com.akdogan.simpletimer.ui.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val handle: SavedStateHandle,
    val repo: DataRepository
) : ViewModel() {

    private var numberOfSetsInternal = 1

    private val _numberOfSets = MutableLiveData(1)
    val numberOfSets: LiveData<Int>
        get() = _numberOfSets

    val timerList = repo.observeAll().map {
        it + AddButton
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        val test = handle.get<Int>("test_key")
        Log.d(TAG, "Arguments from fragment: $test")
    }

    fun incrementSet() {
        numberOfSetsInternal++
        _numberOfSets.postValue(numberOfSetsInternal)
    }

    fun decrementSet() {
        if (numberOfSetsInternal > 1) {
            numberOfSetsInternal--
            _numberOfSets.postValue(numberOfSetsInternal)
        }
    }

    fun onIncrementClicked(item: TimerDomain) {
        viewModelScope.launch {
            repo.updateItem(item.incrementTime())
        }
    }

    fun onDecrementClicked(item: TimerDomain) {
        viewModelScope.launch {
            repo.updateItem(item.decrementTime())
        }
    }

    fun onToggleType(item: TimerDomain) {
        viewModelScope.launch {
            repo.updateItem(item.toggleTimerType())
        }
    }

    fun onDeleteItem(item: TimerDomain) {
        viewModelScope.launch {
            repo.removeTimer(item)
        }
    }

    fun onCreateNewItem() {
        viewModelScope.launch {
            repo.createItem()
        }
    }
}
