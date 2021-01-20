package com.example.worktime

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.worktime.databinding.BottomTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TimerDialogFragment"

class TimerDialogFragment : BottomSheetDialogFragment() {

    companion object {

        private var _binding: BottomTimerBinding? = null
        private val binding get() = _binding
        private var lastTime : Long = 0L
        private lateinit var sharedPrefs: SharedPreferences
        private lateinit var timerFragment: TimerFragment

        private lateinit var timerBtn: Button
        private lateinit var rootView: View
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
        Log.d(TAG, "--> lastTimer = ${sharedPrefs.getLong("lastTime",lastTime)}")
    }

    private fun initViewElements() {
        timerBtn = binding!!.btmTimerBtn
    }

    private fun setTimeTv(){
        timerTv.text = formatTime(sharedPrefs.getLong("lastTime", lastTime))
    }
    private fun formatTime(timeMilli: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(timeMilli)
    }


}

