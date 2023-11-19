package com.akdogan.simpletimer.ui.timer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.akdogan.simpletimer.data.domain.TimerObject
import com.akdogan.simpletimer.data.repository.DataRepository
import kotlinx.coroutines.launch

class TimerViewModel(
    val numberOfSets: Int,
    val timerListTemplate: List<TimerObject>,
    private val repo: DataRepository
) : ViewModel() {
    // todo ¯\_(ツ)_/¯
}

@Suppress("UNCHECKED_CAST")
class TimerViewModelFactory(
    val numberOfSets: Int,
    val listOfTimers: List<TimerObject>,
    val repo: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(numberOfSets, listOfTimers, repo) as T
        } else {
            throw IllegalArgumentException(
                "Wrong ViewModel Class! Expected ${TimerViewModel::class.java} found $modelClass"
            )
        }
    }
}