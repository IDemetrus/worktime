package com.example.worktime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.worktime.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {

    private val mTAG = TimerFragment::class.java.simpleName

    companion object {
        private var _binding: FragmentTimerBinding? = null
        private val binding get() = _binding!!
//        private lateinit var timerViewModel: TimerViewModel
        private var timer: CountDownTimer? = null
        private var isRunning: Boolean? = null
        private var startTime: Long? = null
        private var endTime: Long? = null
        private var sharedPrefs: SharedPreferences? = null
        private lateinit var dialogFragment: TimerDialogFragment

        private var notification: NotificationCompat.Builder? = null
        private var mediaPlayer: MediaPlayer? = null
        private var isSoundActive: Boolean? = null
        private lateinit var timePicker: TimePicker
        private lateinit var timerTv: TextView
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val rootView = binding.root

        dialogFragment = TimerDialogFragment()

        initViews()

        //Init timePicker
        timePicker = binding.timerTp
        initTimePicker()

        binding.timerTv.text = formatTime(startTime ?: 0)

        //Set sound for media player
        setSoundBtn()
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")

        binding.timerBtnStart.setOnClickListener {
            Log.i(mTAG, "-> Start button is: Clicked")
            dialogFragment.show(requireActivity().supportFragmentManager, dialogFragment.tag)

            if (mediaPlayer == null && binding.soundIb.isActivated) mediaPlayer =
                MediaPlayer.create(context, R.raw.ship_bell)
            if (isRunning == true) pauseTimer() else startTimer()
            setViewOnStart()
        }
        binding.timerBtnStop.setOnClickListener {
            Log.i(mTAG, "-> Stop button is: Clicked")
            stopTimer()
            setViewOnStop()
        }

        initNotification()

        return rootView
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initTimePicker(): TimePicker {
        timePicker.setIs24HourView(true)
        timePicker.hour = 0
        timePicker.minute = 45
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
//            timerViewModel.setTime(getLong(hourOfDay, minute))
            if (isRunning == false) startTime = getLong(hourOfDay, minute)
            Log.d(mTAG,"TimePicker: ${formatTime(startTime!!)}")
        }
        if (startTime == null) startTime = getLong(timePicker.hour, timePicker.minute)

/*        if (startTime == 0L)
            timerViewModel.setTime(getLong(timePicker.hour, timePicker.minute))*/
        return timePicker
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
        endTime = System.currentTimeMillis() + startTime!!
        if (timer == null){
        timer = object : CountDownTimer(startTime!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(mTAG, "-> ${formatTime(millisUntilFinished)}")
                startTime = millisUntilFinished
                timerTv.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                Log.i(mTAG, "-> onFinish")
                setTimerColor()
                isRunning = false
                //MediaPlayer if define -> start playing
                    mediaPlayer?.isLooping = true
                    mediaPlayer?.start()
            }
        }.start()
        isRunning = true
        } else {
            Log.d(mTAG, "is Already running!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun stopTimer() {
        startTime = getLong(timePicker.hour, timePicker.minute)
        pauseTimer()
        timer = null

        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setTimerColor() {
        if (startTime == 0L) {
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

    private fun setSoundBtn(){

        sharedPrefs = requireActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE)
        binding.soundIb.isActivated = sharedPrefs!!.getBoolean("isSoundActive", true)
        Log.i(mTAG, "Sound button is: ${binding.soundIb.isActivated}")
        binding.soundIb.setOnClickListener {
            if (it.isActivated) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(context, R.raw.ship_bell)
                Log.i(mTAG, "Sound button is: ${it.isActivated}")
                it.isActivated = false
            } else {
                mediaPlayer?.release()
                mediaPlayer = null
                it.isActivated = true
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
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        Log.i(mTAG, "OnStart: -> lastTime: $startTime")
        sharedPrefs = requireActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE)
        startTime = sharedPrefs!!.getLong("startTime", startTime ?: getLong(timePicker.hour,
            timePicker.minute))
        isRunning = sharedPrefs!!.getBoolean("isRunning", false)

        if (isRunning == true) {
            endTime = sharedPrefs!!.getLong("endTime", 0)
            startTime = endTime!! - System.currentTimeMillis()
//            val currentTime = System.currentTimeMillis()
            //TODO: -> Fix timer onFinish from background
//            if (lastTime > currentTime && lastTime > 0)
//                lastTime -= currentTime
            if (startTime!! < 0){
                startTime = 0
                isRunning = false
            } else {
                startTimer()
                setViewOnStart()
            }
        } else {
            startTime = getLong(timePicker.hour, timePicker.minute)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(mTAG, "-> onStop")
        Log.i(mTAG, "-> lastTime: $startTime")
        isSoundActive = (isRunning == true)
        sharedPrefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        sharedPrefs!!.edit {
            putLong("startTime", startTime!!)
            putLong("endTime", endTime!!)
            putBoolean("isSoundActive", isSoundActive!!)
            putBoolean("isRunning", isRunning!!)
                .apply()
        }

        if (isRunning == true) {
            //TODO: -> Set up timer on notification
            with(NotificationManagerCompat.from(requireContext())) {
                notify(1, notification!!.build())
            }
        } else {
            timer?.cancel()
            timer = null
        }

    }

    private fun initNotification() {
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
    }

    private fun initViews(){
        timerTv = binding.timerTv
    }
}
