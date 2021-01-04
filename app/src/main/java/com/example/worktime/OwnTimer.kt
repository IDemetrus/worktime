package com.example.worktime

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.os.SystemClock
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.text.SimpleDateFormat
import java.util.*

class OwnTimer(private var timeValue: Long) {
    private var sdf: SimpleDateFormat? = null
    private var isRunning = false
    private var interval = 1000L
    private val scope = CoroutineScope(IO)

    @SuppressLint("SimpleDateFormat")


     fun startTimer() {

        sdf = SimpleDateFormat("HH:mm:ss")
        while (timeValue > 0 && isRunning) {
            timeValue -= interval
            val finishTime = sdf!!.format(timeValue - 3 * 60 * 60 * 1000)
            println(if (timeValue == 0L) "Finished" else finishTime)
            try {
                SystemClock.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }


    fun stop() {
        isRunning = false
    }

    fun startT() {
        isRunning = true
        startTimer()
    }
}


