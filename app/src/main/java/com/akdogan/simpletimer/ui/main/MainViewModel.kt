package com.akdogan.simpletimer.ui.main

import androidx.lifecycle.*
import com.akdogan.simpletimer.data.domain.AddButton
import com.akdogan.simpletimer.data.domain.TimerDomain
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.data.domain.decrementTime
import com.akdogan.simpletimer.data.domain.incrementTime
import com.akdogan.simpletimer.data.domain.toTimerObject
import com.akdogan.simpletimer.data.domain.toggleTimerType
import com.akdogan.simpletimer.data.repository.DataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: DataRepository
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

    fun getTimerList(): List<TimerObject> = timerList.value.mapNotNull {
        if (it is AddButton) null else { (it as TimerDomain).toTimerObject() }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repo: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repo) as T
        } else throw IllegalArgumentException(
            "Wrong ViewModel Class! Expected ${MainViewModel::class.java} found $modelClass"
        )
    }
}