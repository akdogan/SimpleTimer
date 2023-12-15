package com.akdogan.simpletimer.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.akdogan.simpletimer.Constants

const val TAG_PRINT_BACKSTACK = "Fragment Backstack"

fun Fragment.printBackStack(){
    val fragmentList: List<Fragment> = emptyList<Fragment>() + requireActivity().supportFragmentManager.fragments
    val stackResult = StringBuilder()
    fragmentList.forEachIndexed { i, fragment ->
        stackResult.append("[$i]: ${fragment.javaClass.canonicalName}\n")
    }

    Log.i(TAG_PRINT_BACKSTACK, "\n$stackResult")
}

fun AppCompatActivity.currentFragment(): Fragment? {
    return supportFragmentManager.fragments.lastOrNull()
}

fun Long.getTimeAsString(): String {
    fun Long.getStringPart(): String = when {
        this >= 10 -> this.toString()
        this >= 1 -> "0$this"
        else -> "00"
    }

    if (this > Constants.TIMER_MAX_VALUE || this < 0) {
        throw IllegalArgumentException("Source is out of range. Must be between 0 and 5999 (99:59)")
    }
    val minutes = this / 60
    val seconds = this - (minutes * 60)
    return "${minutes.getStringPart()}:${seconds.getStringPart()}"
}

fun Long.millisToSeconds(): Long = this / 1000

val Any.TAG
    get() = this::class.java.simpleName
