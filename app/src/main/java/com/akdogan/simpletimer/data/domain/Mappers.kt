package com.akdogan.simpletimer.data.domain

import com.akdogan.simpletimer.data.database.TimerObjectDB

fun TimerObjectDB.toDomain() = TimerDomain(
    dataBaseId = id,
    sort = sort,
    time = time,
    manual = manual
)

fun TimerDomain.toDatabase() = TimerObjectDB(
    id = dataBaseId,
    sort = sort,
    time = time,
    manual = manual
)
