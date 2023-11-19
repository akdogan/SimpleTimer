package com.akdogan.simpletimer.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {
    protected val ACTIVITY_TAG: String
        get() = this::class.java.toString()

    protected fun log(msg: String, tag: String = ACTIVITY_TAG) = Log.i(tag, msg)
}