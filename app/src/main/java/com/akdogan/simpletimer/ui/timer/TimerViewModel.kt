package com.akdogan.simpletimer.ui.timer


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.akdogan.simpletimer.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    val handle: SavedStateHandle
) : ViewModel() {

    val numberOfSets: Int = handle.get(Constants.BUNDLE_KEY_NUMBER_OF_SETS) ?: 1

}
