package com.akdogan.simpletimer

import android.content.Context
import com.akdogan.simpletimer.data.database.TimerDataBase
import com.akdogan.simpletimer.data.repository.DataRepository

object ServiceLocator {
    lateinit var repo: DataRepository
        private set

    fun setupDefaultRepository(con: Context){
        val database = TimerDataBase.getInstance(con)
        repo = DataRepository(database.timerDao)
    }
}