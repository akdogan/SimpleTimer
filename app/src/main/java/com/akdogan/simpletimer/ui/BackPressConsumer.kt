package com.akdogan.simpletimer.ui

interface BackPressConsumer {

    /**
     * Return true when the backpress was consumed by this BackPressConsumer
     */
    fun onBackPressed(): Boolean
}