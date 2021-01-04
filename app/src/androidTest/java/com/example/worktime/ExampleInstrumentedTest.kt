package com.example.worktime

import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val TAG = "TimerTest"
    var isRunning : Boolean = false
    private val startTime = System.currentTimeMillis()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.worktime", appContext.packageName)
        val timer  = MyTimer(10000,1000)
        timer.start()
    }
    class MyTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        private val TAG = this.javaClass.simpleName
        override fun onTick(millisUntilFinished: Long) {
            Log.i(TAG, "---> onTick : ${formatTime(millisUntilFinished)}")
        }

        override fun onFinish() {
            Log.i(TAG, "---> onFinish : ")
        }
        private fun formatTime(long: Long) {

            val sdf = SimpleDateFormat("hh:mm:ss")
            sdf.format(long)
        }
    }


}