package com.example.worktime

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    private var notification: NotificationCompat.Builder? = null
    private var mediaPlayer: MediaPlayer? = null


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
            setTimerColor()
        })
        timerViewModel.isRunning.observe(this, { status ->
            isRunning = status
        })

        //TODO: -> Set up sound button
        //Set sound for media player
        binding.soundIb.isActivated = timerViewModel.isSoundOn.value ?: true
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")
        binding.soundIb.setOnClickListener {
            if (it.isActivated) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(context, R.raw.ship_bell)
                Log.i(mTAG, "Sound button is: ${it.isActivated}")
                it.isActivated = false
                timerViewModel.setSound(false)

            } else {
                mediaPlayer?.release()
                mediaPlayer = null
                it.isActivated = true
                timerViewModel.setSound(true)
                Log.i(mTAG, "Sound button is: ${it.isActivated}")
            }
            Log.i(mTAG, "${it.isActivated}")
        }
        if (mediaPlayer == null) {
            mediaPlayer = if (binding.soundIb.isActivated) {

                MediaPlayer.create(context, R.raw.ship_bell)
            } else {
                mediaPlayer?.release()
                null
            }
        }
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")

        binding.timerBtnStart.setOnClickListener {
            Log.i(mTAG, "-> Start button is: Clicked")
            if (mediaPlayer == null && binding.soundIb.isActivated) mediaPlayer =
                MediaPlayer.create(context, R.raw.ship_bell)
            startTimer()
            timerViewModel.setStatus(true)
            setViewOnStart()
        }
        binding.timerBtnStop.setOnClickListener {
            Log.i(mTAG, "-> Stop button is: Clicked")

            stopTimer()
            timer = null
            timerViewModel.setTime(getLong(timePicker.hour, timePicker.minute))
            timerViewModel.setStatus(false)
            setViewOnStop()
        }

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent =
            PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        notification = NotificationCompat.Builder(requireContext(), "1")
            .setSmallIcon(R.drawable.ic_timer_24)
            .setAutoCancel(true)
            .setContentTitle(binding.timerTv.text)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)














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
                    setTimerColor()
                    //MediaPlayer if define -> start playing
                    mediaPlayer?.isLooping = true
                    mediaPlayer?.start()
                }
            }.start()
        } else {
            Log.i(mTAG, "-> Already running")
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setTimerColor() {
        if (lastTime == 0L) {
            binding.timerTv.setTextColor(Color.RED)
            binding.timerTv.text = getString(R.string.timer_off)
        }
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
            //TODO: -> Fix timer onFinish from background
            if (lastTime > currentTime)
                lastTime -= currentTime
            startTimer()
            setViewOnStart()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(mTAG, "-> onStop")
        stopTimer()
        timer = null
        Log.i(mTAG, "-> lastTime: $lastTime")

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        Log.i(mTAG, "-> elapsedTime: ${sdf.format(currentTime)}")

        if (isRunning) {
            val prevTime = timerViewModel.lastTime.value ?: lastTime
            lastTime = prevTime + currentTime


            //TODO: -> Set up timer on notification
            with(NotificationManagerCompat.from(requireContext())) {
                notify(1, notification!!.build())
            }
        }

    }

}