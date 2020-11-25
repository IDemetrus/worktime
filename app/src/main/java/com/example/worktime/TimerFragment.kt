package com.example.worktime

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.worktime.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {
    private val mTAG = TimerFragment::class.java.simpleName

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var timerViewModel: TimerViewModel
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var lastTime: Long = 0L

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val rootView = binding.root

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        lastTime = timerViewModel.lastTime.value ?: lastTime
        isRunning = timerViewModel.isRunning.value ?: isRunning

        var mediaPlayer: MediaPlayer? = null

        //Init timePicker
        val timePicker = binding.timerTp
        timePicker.setIs24HourView(true)
        timePicker.hour = 0
        timePicker.minute = 45
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            timerViewModel.setTime(getLong(hourOfDay, minute))
        }
        if (lastTime == 0L)
            timerViewModel.setTime(getLong(timePicker.hour, timePicker.minute))
        binding.timerTv.text = formatTime(lastTime)
        timerViewModel.lastTime.observe(this, { long ->
            lastTime = long
            binding.timerTv.text = formatTime(long)
        })
        timerViewModel.isRunning.observe(this, { status ->
            isRunning = status
        })

        //TODO: -> Set up sound button
        //Set sound for media player
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")
        binding.soundIb.isActivated = true
        binding.soundIb.setOnClickListener {
            if (it.isActivated) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                Log.i(mTAG, "Sound button is: ${it.isActivated}")
                it.isActivated = false
            } else {
                it.isActivated = true
                Log.i(mTAG, "Sound button is: ${it.isActivated}")
            }
            Log.i(mTAG, "${it.isActivated}")


        }
        mediaPlayer = if (binding.soundIb.isActivated) {
            MediaPlayer.create(context, R.raw.ship_bell)
        } else {
            mediaPlayer?.release()
            null
        }
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")

        binding.timerBtnStart.setOnClickListener {
            Log.i(mTAG, "-> Start button is: Clicked")
            startTimer()
            timerViewModel.setStatus(true)
            setViewOnStart()
        }
        binding.timerBtnStop.setOnClickListener {
            Log.i(mTAG, "-> Stop button is: Clicked")
            stopTimer()
            timer = null
            timerViewModel.setTime(getLong(timePicker.hour,timePicker.minute))
            timerViewModel.setStatus(false)
            setViewOnStop()
        }















        return rootView
    }

    private fun formatTime(timeMilli: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(timeMilli)
    }

    private fun getLong(hour: Int, minute: Int): Long {
        return ((hour * 3600 * 1000) + (minute * 60 * 1000)).toLong()
    }

    private fun startTimer() {
        //Set timer with start click method
        if (timer == null) {
            timer = object : CountDownTimer(lastTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.i(mTAG, "-> ${formatTime(millisUntilFinished)}")
//                    lastTime = millisUntilFinished
                    timerViewModel.setTime(millisUntilFinished)
                }

                override fun onFinish() {
                    Log.i(mTAG, "-> onFinish")
                    timerViewModel.setStatus(false)
                }
            }.start()
        } else {
            Log.i(mTAG, "-> Already running")
        }
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun setViewOnStart() {
        binding.timerTv.visibility = View.VISIBLE
        binding.timerTp.visibility = View.INVISIBLE
        binding.timerMinuteTv.visibility = View.GONE
        binding.timerHourTv.visibility = View.GONE
    }

    private fun setViewOnStop() {
        binding.timerTv.visibility = View.GONE
        binding.timerTp.visibility = View.VISIBLE
        binding.timerMinuteTv.visibility = View.VISIBLE
        binding.timerHourTv.visibility = View.VISIBLE

    }

    override fun onStart() {
        super.onStart()
        Log.i(mTAG, "OnStart: -> lastTime: $lastTime")
        if (isRunning) {
            val currentTime = System.currentTimeMillis()
            if (lastTime > currentTime)
                lastTime -= currentTime
            startTimer()
            setViewOnStart()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(mTAG,"-> onStop")
        stopTimer()
        timer = null
        Log.i(mTAG, "-> lastTime: $lastTime")

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        Log.i(mTAG, "-> elapsedTime: ${sdf.format(currentTime)}")

        if (isRunning){
            val prevTime = timerViewModel.lastTime.value ?: lastTime
            lastTime = prevTime + currentTime
        }

    }

    override fun onPause() {
        super.onPause()
        Log.i(mTAG,"-> onPause")
    }


}