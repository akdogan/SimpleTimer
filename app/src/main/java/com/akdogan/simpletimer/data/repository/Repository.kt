package com.akdogan.simpletimer.data.repository

import android.util.Log
import com.akdogan.simpletimer.data.database.TimerDao
import com.akdogan.simpletimer.data.database.toDatabase
import com.akdogan.simpletimer.data.database.toDomain
import com.akdogan.simpletimer.data.domain.TimerObject

// Todo add shared preferences

class DataRepository(private val db: TimerDao){
    suspend fun loadTimers(): List<TimerObject>{
        val list = db.getAll()
        Log.i("DB_TEST", "list from db: $list")
        val domainList = list?.toDomain()
        Log.i("DB_TEST", "list to domain: $domainList")
        return domainList
    }

    suspend fun saveTimers(list: List<TimerObject>){
        db.insertNew(list.toDatabase())
    }
}