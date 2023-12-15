package com.akdogan.simpletimer.ui

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.akdogan.simpletimer.R
import com.akdogan.simpletimer.service.TimerService
import com.akdogan.simpletimer.service.createNotificationChannel
import com.akdogan.simpletimer.ui.main.MainFragment
import com.akdogan.simpletimer.ui.timer.TimerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Log.d(TAG,"TimerService running: ${isServiceRunning(TimerService::class.java)}")
        if (savedInstanceState == null) {
            if (isServiceRunning(TimerService::class.java)) {
                navigateToTimer()
            } else {
                navigateToMain()
            }
        }
        createNotificationChannel(this)
    }

    private fun <T: Service?>isServiceRunning(cls: Class<T>): Boolean {
        val manager : ActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val servicesList = manager.getRunningServices(10)
        servicesList.forEachIndexed { index, runningServiceInfo ->
            Log.d(TAG,"[$index] ${runningServiceInfo.service.className}")
            if (runningServiceInfo.service.className == cls.canonicalName) return true
        }
        Log.d(TAG,"false! Cls: ${cls.canonicalName}")
        return false
    }

    override fun onBackPressed() {
        val fragment = currentFragment()
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
            .replace(R.id.container, MainFragment.newInstance().apply {
                arguments = bundleOf("test_key" to 42)
            })
            .commitNow()
    }
}