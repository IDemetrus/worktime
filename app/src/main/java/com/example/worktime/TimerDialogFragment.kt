package com.example.worktime

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.worktime.databinding.BottomTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

private const val TAG = "TimerDialogFragment"

class TimerDialogFragment : BottomSheetDialogFragment() {

    companion object {

        private var _binding: BottomTimerBinding? = null
        private val binding get() = _binding
        private lateinit var timerVM: TimerViewModel
        private var lastTime : Long = 0L
        private lateinit var sharedPrefs: SharedPreferences

        private lateinit var startBtn: Button
        private lateinit var rootView: View
        private lateinit var stopBtn: Button
        private lateinit var timerTv: TextView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomTimerBinding.inflate(inflater, container, false)
        rootView = binding!!.root
        initViewElements()

        //Get layout params

        return rootView
    }

    override fun onStart() {
        super.onStart()
        sharedPrefs = this.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        setTimeTv()

    }

    private fun initViewElements() {
        startBtn = binding!!.btmStartBtn
        stopBtn = binding!!.btmStopBtn
        timerTv = binding!!.btmTimerTv
    }

    private fun setTimeTv(){
        timerTv.text = formatTime(sharedPrefs.getLong("lastTime", lastTime))
    }
    private fun formatTime(timeMilli: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(timeMilli)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layout = rootView.findViewById(R.id.bottom_timer_id) as ConstraintLayout
        val params : ViewGroup.LayoutParams = layout.layoutParams
        layout.layoutParams = params

        Log.d(TAG, "${params.height}")
    }

}

