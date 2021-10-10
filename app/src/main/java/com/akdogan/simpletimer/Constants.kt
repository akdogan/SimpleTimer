package com.akdogan.simpletimer

object Constants {
    const val TIMER_MAX_VALUE = 5999 // Corresponds to 99:99

    const val TIMER_INTERVAL = 5 // Interval when increasing or decreasing a timer

    const val BUNDLE_KEY_TIMER_LIST = "BUNDLE_KEY_TIMER_LIST"
    const val BUNDLE_KEY_NUMBER_OF_SETS = "BUNDLE_KEY_NUMBER_OF_SETS"

    const val SERVICE_KEY_TIMER_LIST = "SERVICE_KEY_TIMER_LIST"
    const val SERVICE_KEY_NUMBER_OF_SETS = "SERVICE_KEY_NUMBER_OF_SETS"

    val DEFAULT_TIMERS_ON_START = listOf<Long>(15, 45, 70)

    // Notifications
    const val NOT_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOT_CHANNEL_NAME = "Simple Timer Channel"
    const val NOT_CHANNEL_DESC = "Your running timer"
    const val ONGOING_NOTIFICATION_ID = 1234

}