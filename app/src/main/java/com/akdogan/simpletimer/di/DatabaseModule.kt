package com.akdogan.simpletimer.di

import android.content.Context
import com.akdogan.simpletimer.data.database.TimerDao
import com.akdogan.simpletimer.data.database.TimerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context) : TimerDataBase {
        return TimerDataBase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDao(dataBase: TimerDataBase): TimerDao {
        return dataBase.timerDao
    }
}