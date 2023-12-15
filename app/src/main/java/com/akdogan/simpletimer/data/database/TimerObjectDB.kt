package com.akdogan.simpletimer.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_config_table")
class TimerObjectDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val sort: Int,

    @ColumnInfo
    val time: Long,

    @ColumnInfo
    val manual: Boolean
)
