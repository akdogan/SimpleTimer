package com.akdogan.simpletimer.ui.timer

interface BackPressConsumer {

    /**
     * Return true when the backpress was consumed by this BackPressConsumer
     */
    fun onBackPressed(): Boolean
}