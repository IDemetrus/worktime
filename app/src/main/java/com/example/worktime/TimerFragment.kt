package com.example.worktime

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.Color.green
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
import com.example.worktime.databinding.FragmentTimerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors.getColor
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TimerFragment"

class TimerFragment : BottomSheetDialogFragment() {

    companion object {
        private var _binding: FragmentTimerBinding? = null
        private val binding get() = _binding!!

        //        private lateinit var timerViewModel: TimerViewModel
        private var timer: CountDownTimer? = null
        private var isRunning: Boolean? = null
        private var startTime: Long? = null
        private var endTime: Long? = null
        private var sharedPrefs: SharedPreferences? = null
        private lateinit var behavior: BottomSheetBehavior<View>

        private var notification: NotificationCompat.Builder? = null
        private var mediaPlayer: MediaPlayer? = null
        private var isSoundActive: Boolean? = null
        private lateinit var rootView: View
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
        rootView = binding.root

        initViews()

        //Init timePicker
        timePicker = binding.timerTp
        initTimePicker()

        binding.timerTv.text = formatTime(startTime ?: 0)

        //Set sound for media player
        setSoundBtn()
        Log.i(TAG, "Sound button is: ${binding.soundIb.isActivated}")

        binding.timerBtnStart.setOnClickListener {
            Log.i(TAG, "-> Start button is: Clicked")

            if (mediaPlayer == null && binding.soundIb.isActivated) mediaPlayer =
                MediaPlayer.create(context, R.raw.ship_bell)
            if (isRunning == true) pauseTimer() else startTimer()
            setViewOnStart()
            setDimens()
        }
        binding.timerBtnStop.setOnClickListener {
            Log.i(TAG, "-> Stop button is: Clicked")
            stopTimer()
            setViewOnStop()
            setDefaultDimens()
        }

        initNotification()

        return rootView
    }

    @SuppressLint("ResourceType")
    private fun setDefaultDimens() {
        binding.soundIb.visibility = View.VISIBLE
        binding.timerTv.textSize = resources.getDimension(R.dimen.text_size_32)
        binding.timerTv.setTextColor(resources.getColor(R.color.green_500))
        binding.root.setBackgroundColor(Color.WHITE)
        binding.timerBtnStart.visibility = View.VISIBLE
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
            Log.d(TAG, "TimePicker: ${formatTime(startTime!!)}")
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
        if (timer == null) {
            timer = object : CountDownTimer(startTime!!, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.i(TAG, "-> ${formatTime(millisUntilFinished)}")
                    startTime = millisUntilFinished
                    timerTv.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    Log.i(TAG, "-> onFinish")
                    setTimerColor()
                    isRunning = false
                    //MediaPlayer if define -> start playing
                    mediaPlayer?.isLooping = true
                    mediaPlayer?.start()
                }
            }.start()
            isRunning = true
        } else {
            Log.d(TAG, "is Already running!")
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
        binding.timerTp.visibility = View.GONE
        binding.timerMinuteTv.visibility = View.GONE
        binding.timerHourTv.visibility = View.GONE
    }

    private fun setViewOnStop() {
        binding.timerTv.visibility = View.GONE
        binding.timerTp.visibility = View.VISIBLE
        binding.timerMinuteTv.visibility = View.VISIBLE
        binding.timerHourTv.visibility = View.VISIBLE

    }

    private fun setSoundBtn() {

        sharedPrefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        binding.soundIb.isActivated = sharedPrefs!!.getBoolean("isSoundActive", true)
        Log.i(TAG, "Sound button is: ${binding.soundIb.isActivated}")
        binding.soundIb.setOnClickListener {
            if (it.isActivated) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(context, R.raw.ship_bell)
                Log.i(TAG, "Sound button is: ${it.isActivated}")
                it.isActivated = false
            } else {
                mediaPlayer?.release()
                mediaPlayer = null
                it.isActivated = true
                Log.i(TAG, "Sound button is: ${it.isActivated}")
            }
            Log.i(TAG, "${it.isActivated}")
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
        Log.i(TAG, "OnStart: -> lastTime: $startTime")
        sharedPrefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        startTime = sharedPrefs!!.getLong(
            "startTime", startTime ?: getLong(
                timePicker.hour,
                timePicker.minute
            )
        )
        isRunning = sharedPrefs!!.getBoolean("isRunning", false)

        if (isRunning == true) {
            endTime = sharedPrefs!!.getLong("endTime", 0)
            startTime = endTime!! - System.currentTimeMillis()
//            val currentTime = System.currentTimeMillis()
            //TODO: -> Fix timer onFinish from background
//            if (lastTime > currentTime && lastTime > 0)
//                lastTime -= currentTime
            if (startTime!! < 0) {
                startTime = 0
                isRunning = false
            } else {
                startTimer()
                setViewOnStart()
            }
        } else {
            startTime = getLong(timePicker.hour, timePicker.minute)
            endTime = sharedPrefs!!.getLong("endTime", 0)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "-> onStop")
        Log.i(TAG, "-> lastTime: $startTime")
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
        //TODO fix notifications
        notification = NotificationCompat.Builder(requireContext(), "1")
            .setSmallIcon(R.drawable.ic_timer_24)
            .setAutoCancel(true)
            .setContentTitle(binding.timerTv.text)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun initViews() {
        timerTv = binding.timerTv
    }

    @SuppressLint("ResourceType")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet as View)
            if (isRunning==true){
                setDimens()
            }
        }

        return dialog

    }

    private fun setDimens() {
        binding.timerTv.setTextColor(Color.WHITE)
        binding.timerTv.textSize = 24f
        binding.soundIb.visibility = View.GONE
        binding.root.setBackgroundColor(resources.getColor(R.color.green_500))
        binding.timerBtnStart.visibility = View.GONE
        behavior.isFitToContents = true
    }
}
