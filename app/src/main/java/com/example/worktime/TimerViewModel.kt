package com.example.worktime

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {


    private var _lastTime = MutableLiveData<Long>()
    val lastTime: LiveData<Long>
        get() = _lastTime

    private var _startBtnText = MutableLiveData<String>()
    val startBtnText: LiveData<String>
        get() = _startBtnText


    private var _isRunning = MutableLiveData<Boolean>()
    val isRunning : LiveData<Boolean>
        get() = _isRunning

    init {
        _lastTime.value = 0L
        _isRunning.value = false
    }

    fun setTime(long: Long){
        _lastTime.value = long
    }

    fun setStatus(status: Boolean){
        _isRunning.value = status
    }

    override fun onCleared() {
        super.onCleared()
        _lastTime.value = 0L
        _isRunning.value = false
    }
}