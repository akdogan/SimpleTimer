package com.akdogan.simpletimer.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimerObjectDB)

    @Delete
    suspend fun deleteItem(item: TimerObjectDB)

    @Update
    suspend fun updateItem(item: TimerObjectDB)

    @Query("SELECT * from last_config_table")
    fun observeAll(): Flow<List<TimerObjectDB>>

    @Query("SELECT * from last_config_table")
    suspend fun getAll(): List<TimerObjectDB>

    @Query("SELECT sort from last_config_table ORDER BY sort DESC LIMIT 1")
    suspend fun getLatestSort(): Int
}