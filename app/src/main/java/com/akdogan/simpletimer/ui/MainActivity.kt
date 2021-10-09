package com.akdogan.simpletimer.ui

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_DESC
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_ID
import com.akdogan.simpletimer.Constants.NOT_CHANNEL_NAME
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.service.TimerService
import com.akdogan.simpletimer.ui.main.MainFragment
import com.akdogan.simpletimer.ui.timer.BackPressConsumer
import com.akdogan.simpletimer.ui.timer.TimerFragment


class MainActivity : BaseActivity() {

    // TODO if service timer is running go directly to timer screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        log("TimerService running: ${isServicRunning(TimerService::class.java)}")
        if (savedInstanceState == null) {
            if (isServicRunning(TimerService::class.java)) {
                navigateToTimer()
            } else {
                navigateToMain()
            }
        }
        com.akdogan.simpletimer.service.createNotificationChannel(this)
    }

    private fun <T: Service?>isServicRunning(cls: Class<T>): Boolean {
        val manager : ActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val servicesList = manager.getRunningServices(10)
        servicesList.forEachIndexed { index, runningServiceInfo ->
            log("[$index] ${runningServiceInfo.service.className}")
            if (runningServiceInfo.service.className == cls.canonicalName) return true
        }
        log("false! Cls: ${cls.canonicalName}")
        return false
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.currentFragment()
        if (fragment is BackPressConsumer && fragment.onBackPressed()) return
        super.onBackPressed()
    }

    private fun navigateToTimer(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TimerFragment.newInstance())
            .commitNow()
    }

    private fun navigateToMain(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }


    @Deprecated("Use static createNotificationChannel(context: Context)")
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NOT_CHANNEL_ID, NOT_CHANNEL_NAME, importance
            ).apply {
                description = NOT_CHANNEL_DESC
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}