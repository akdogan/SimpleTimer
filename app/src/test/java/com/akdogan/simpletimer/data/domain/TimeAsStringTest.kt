package com.akdogan.simpletimer.data.domain

import org.junit.Assert.*
import org.junit.Test

class TimeAsStringTest {

    @Test
    fun testAllTheCases() {
        listOf(
            Pair(60, "01:00"),
            Pair(120, "02:00"),
            Pair(10, "00:10"),
            Pair(475, "07:55"),
            Pair(2341, "39:01"),
            Pair(0, "00:00"),
            Pair(5999, "99:59")
        ).forEach {
            singleTestCase(it)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun timeOutOfRange() {
        singleTestCase(Pair(6000, ""))
        singleTestCase(Pair(-1, ""))
    }

    private fun singleTestCase(testCase: Pair<Int, String>) {
        val result = testCase.first.toLong().getTimeAsString()
        assertEquals(testCase.second, result)
    }
}