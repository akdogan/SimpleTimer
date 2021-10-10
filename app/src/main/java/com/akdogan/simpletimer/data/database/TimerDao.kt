package com.akdogan.simpletimer.data.database

import android.util.Log
import androidx.room.*

@Dao
interface TimerDao {

    @Transaction
    suspend fun insertNew(items: List<TimerObjectDB>) {
        Log.i("Database", "saving to database on thread ${Thread.currentThread().name}")
        deleteAll()
        insertAll(items)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TimerObjectDB>)

    @Query("SELECT * from last_config_table")
    suspend fun getAll(): List<TimerObjectDB>

    @Query("DELETE FROM last_config_table")
    suspend fun deleteAll()
}