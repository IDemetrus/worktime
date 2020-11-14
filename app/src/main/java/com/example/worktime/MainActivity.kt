package com.example.worktime

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.worktime.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var timePicker: TimePicker
    private lateinit var timer: CountDownTimer
    private lateinit var timerTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("[${this.javaClass}]", "-> [onCreate]")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        Log.i("[${this.javaClass}]", "-> [onStart]")

        //Init start/stop buttons
        val startBtn = binding.timerBtnStart
        val stopBtn = binding.timerBtnStop

        //Init timer text
        timerTv = binding.timerTv
        var timerHour = 0
        var timerMinute = 45

        timePicker = findViewById(R.id.timerTp)
        timePicker.setIs24HourView(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.hour = timerHour
            timePicker.minute = timerMinute
        }
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            timerHour = hourOfDay
            timerMinute = minute
            Log.i("MainActivity", "$hourOfDay:$minute")
        }

        startBtn.setOnClickListener {
            Toast.makeText(this, "${startBtn.text}", Toast.LENGTH_SHORT).show()

            timePicker.visibility = View.INVISIBLE
            timerTv.visibility = View.VISIBLE
            timerTv.text = "$timerHour : $timerMinute"

            when (startBtn.text) {
                "Start" -> startBtn.text = getString(R.string.pause_btn)
                "Pause" -> startBtn.text = getString(R.string.start_btn)
            }
        }
        stopBtn.setOnClickListener {
            Toast.makeText(this, "${stopBtn.text}", Toast.LENGTH_SHORT).show()
            timerTv.visibility = View.INVISIBLE
            timePicker.visibility = View.VISIBLE
            startBtn.text = getString(R.string.start_btn)
        }
    }

    override fun onCreateDialog(id: Int, args: Bundle?): Dialog? {
        val builder = androidx.appcompat.app.AlertDialog.Builder(applicationContext)
        builder.setView(layoutInflater.inflate(R.layout.time_picker, null))
        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        Log.i("[${this.javaClass}]", "-> [onResume]")

    }

    override fun onPause() {
        super.onPause()
        Log.i("[${this.javaClass}]", "-> [onPause]")

    }

    override fun onRestart() {
        super.onRestart()
        Log.i("[${this.javaClass}]", "-> [onRestart]")

    }

    override fun onStop() {
        super.onStop()
        Log.i("[${this.javaClass}]", "-> [onStop]")

    }
}