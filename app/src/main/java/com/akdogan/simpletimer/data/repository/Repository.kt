package com.akdogan.simpletimer.data.repository

import com.akdogan.simpletimer.data.database.TimerDao
import com.akdogan.simpletimer.data.database.TimerObjectDB
import com.akdogan.simpletimer.data.domain.TimerDomain
import com.akdogan.simpletimer.data.domain.toDatabase
import com.akdogan.simpletimer.data.domain.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Todo add shared preferences
class DataRepository @Inject constructor(private val db: TimerDao) {

    suspend fun createItem() = withContext(Dispatchers.IO) {
        db.insert(
            TimerObjectDB(
                sort = db.getLatestSort() + 1,
                time = DEFAULT_TIME,
                manual = false
            )
        )
    }

    suspend fun removeTimer(item: TimerDomain) {
        db.deleteItem(item.toDatabase())
    }

    fun observeAll(): Flow<List<TimerDomain>> {
        return db.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getAll(): List<TimerDomain> {
        return db.getAll().map {
            it.toDomain()
        }
    }

    suspend fun updateItem(item: TimerDomain) = withContext(Dispatchers.IO) {
        db.updateItem(item.toDatabase())
    }

    companion object {
        private const val DEFAULT_TIME: Long = 15
    }
}