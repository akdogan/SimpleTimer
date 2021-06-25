package com.akdogan.simpletimer

import android.app.Application

class TimerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.setupDefaultRepository(applicationContext)
    }
}