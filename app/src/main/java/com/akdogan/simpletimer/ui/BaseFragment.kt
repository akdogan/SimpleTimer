package com.akdogan.simpletimer.ui

import android.util.Log
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    protected val FRAGMENT_TAG: String
        get() = this::class.java.toString()

    protected fun log(msg: String, tag: String = FRAGMENT_TAG) = Log.i(tag, msg)
}