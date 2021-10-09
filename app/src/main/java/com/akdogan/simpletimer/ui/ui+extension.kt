package com.akdogan.simpletimer.ui

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

const val TAG_PRINT_BACKSTACK = "Backstack Printer"

fun Fragment.printBackStack(){
    val fragmentList: List<Fragment> = emptyList<Fragment>() + requireActivity().supportFragmentManager.fragments
    val stackResult = StringBuilder()
    fragmentList.forEachIndexed { i, fragment ->
        stackResult.append("[$i]: ${fragment.javaClass.canonicalName}\n")
    }

    Log.i(TAG_PRINT_BACKSTACK, "Backstack is:\n $stackResult ")
}

fun FragmentManager.currentFragment(): Fragment? {
    return fragments.lastOrNull()
}

