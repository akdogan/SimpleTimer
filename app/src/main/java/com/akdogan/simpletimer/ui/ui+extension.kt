package com.akdogan.simpletimer.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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

