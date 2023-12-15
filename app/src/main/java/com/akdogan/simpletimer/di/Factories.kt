package com.akdogan.simpletimer.di

import com.akdogan.simpletimer.data.domain.TimerDomain
import com.akdogan.simpletimer.timercore.TimerManager
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.CoroutineScope

@AssistedFactory
interface TimerManagerFactory {

    fun create(
        numberOfSets: Int,
        timerListTemplate: List<TimerDomain>,
        coroutineScope: CoroutineScope
    ): TimerManager
}
