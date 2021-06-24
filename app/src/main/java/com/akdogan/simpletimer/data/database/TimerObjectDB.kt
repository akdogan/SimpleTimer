package com.akdogan.simpletimer.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akdogan.simpletimer.data.domain.TimerObject

@Entity(tableName = "last_config_table")
class TimerObjectDB(
    @PrimaryKey
    val id: Int = 0,

    @ColumnInfo
    val sort: Int,

    @ColumnInfo
    val time: Long,

    @ColumnInfo
    val manual: Boolean
)

fun List<TimerObject>.toDatabase(): List<TimerObjectDB>{
    /*val tempList = mutableListOf<TimerObjectDB>()
    this.forEachIndexed { index, timerObject ->
        tempList.add(timerObject.toDatabase(index))
    }*/

    return this.mapIndexed { index, timerObject ->
        timerObject.toDatabase(index)
    }

    //return tempList
}

fun List<TimerObjectDB>.toDomain(): List<TimerObject>{
    return this.sortedBy{ it.sort }.toDomain()
}

fun TimerObject.toDatabase(sort: Int): TimerObjectDB = TimerObjectDB(
    sort = sort,
    time = this.time,
    manual = !this.timerType
)

fun TimerObjectDB.toTimerObject(): TimerObject = TimerObject(
    initialTime = this.time,
    initialTimerType = !this.manual
)