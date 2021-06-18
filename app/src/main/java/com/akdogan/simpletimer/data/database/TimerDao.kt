package com.akdogan.simpletimer.data.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface TimerDao {

    @Insert
    suspend fun insertItem(item: TimerObjectDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TimerObjectDB>)

    @Query("SELECT *")
    suspend fun getAll(): List<TimerObjectDB>

    @Query("DELETE FROM last_config_table")
    suspend fun deleteAll()
}