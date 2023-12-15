package com.akdogan.simpletimer.ui.timer

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay

const val MAX_TIME = 60

@Composable
fun TimerScreen(id: Int = (11111..99999).random()) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {


    var time: Long by remember {
        mutableLongStateOf(1L)
    }

    LaunchedEffect(id) {
        while (time <= MAX_TIME * 1000) {
            delay(1)
            time += 1
        }
    }

    val angle by remember {
        derivedStateOf {
            (time.toFloat() * 12 / 1000)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            Log.i("Arif", "width=${size.width} / height=${size.height}")
            val insetX = 200
            val sideLength = size.width - (2 * insetX)
            val topLeft = Offset(
                x = size.width / 2 - sideLength / 2,
                y = size.height / 2 - sideLength / 2,
            )
            drawArc(
                color = Color.Cyan,
                topLeft = topLeft,
                size = Size(sideLength, sideLength),
                startAngle = 0f,
                sweepAngle = angle,
                useCenter = false,
                style = Stroke(width = 8f)
            )
        }
//            Text(
//                text = time.getTimeAsString()
//            )

    }
}