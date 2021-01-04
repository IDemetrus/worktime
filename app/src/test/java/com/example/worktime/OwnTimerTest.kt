package com.example.worktime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat

class OwnTimerTest{
    @Test
    fun runTimer_test(){
        var sdf: SimpleDateFormat?
        var timeValue = 10000L
        var isRunning = true

        CoroutineScope(Dispatchers.IO).launch {
            sdf = SimpleDateFormat("hh:mm:ss")
            while (timeValue > 0 && isRunning) {
                val finishTime = sdf!!.format(timeValue)
                println(if (timeValue == 0L) "Finished" else finishTime)
                try {
                    Thread.sleep(1000)
                    timeValue -= 1000
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}