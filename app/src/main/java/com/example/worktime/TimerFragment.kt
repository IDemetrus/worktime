package com.example.worktime

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import com.example.worktime.databinding.FragmentTimerBinding
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var timer: CountDownTimer


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater,container,false)
        val rootView = binding.root

        //Init timePicker
        val timePicker = rootView.findViewById<TimePicker>(R.id.timerTp)
        timePicker.setIs24HourView(true)
        timePicker.hour = 0
        timePicker.minute = 45

        //Set timer with start click method
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale("RU"))
        binding.timerBtnStart.setOnClickListener {


            //Changes button text
            if (binding.timerBtnStart.text == getString(R.string.start_btn)){
                binding.timerBtnStart.text = getString(R.string.pause_btn)

                //Init CountDownTimer as "cDt"
                timer = object : CountDownTimer(getLong(timePicker.hour.toString(),
                    timePicker.minute.toString()),1000){
                    override fun onTick(millisUntilFinished: Long) {
                        Log.i("TimerFragment", simpleDateFormat.format(millisUntilFinished))
                        binding.timerTv.visibility = View.VISIBLE
                        timePicker.visibility = View.INVISIBLE
                        //TODO change date to System date
                        binding.timerTv.text = simpleDateFormat.format(millisUntilFinished-(3*3600*1000))
                    }

                    override fun onFinish() {
                        Log.i("TimerFragment", "Timer: Finished")
                    }
                }.start()
            } else {
                timer.cancel()
                //TODO add last timer time to start new* after pause
            }

        }
        binding.timerBtnStop.setOnClickListener {
            Log.i("TimerFragment", "Timer: Stopped")
            binding.timerTv.visibility = View.INVISIBLE
            timePicker.visibility = View.VISIBLE
            binding.timerBtnStart.text = getString(R.string.start_btn)
            timer.cancel()
        }














        return rootView
    }

    private fun getLong(hour: String, minute: String): Long {
        return ((hour.toInt()*3600*1000)+(minute.toInt()*60*1000)).toLong()
    }

}