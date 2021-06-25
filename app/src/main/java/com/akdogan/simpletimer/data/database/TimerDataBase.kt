package com.akdogan.simpletimer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimerObjectDB::class], version = 1, exportSchema = false)
abstract class TimerDataBase : RoomDatabase() {
    abstract val timerDao: TimerDao

    companion object {
        @Volatile
        private var INSTANCE: TimerDataBase? = null

        fun getInstance(con: Context): TimerDataBase{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        con,
                        TimerDataBase::class.java,
                        "last_config_table"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}