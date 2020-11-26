package com.example.worktime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {


    private var _lastTime = MutableLiveData<Long>()
    val lastTime: LiveData<Long>
        get() = _lastTime

    private var _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean>
        get() = _isRunning

    private var _isSoundOn = MutableLiveData<Boolean>()
    val isSoundOn: LiveData<Boolean>
        get() = _isSoundOn

    init {
        _lastTime.value = 0L
        _isRunning.value = false
        _isSoundOn.value = false
    }

    fun setTime(long: Long) {
        _lastTime.value = long
    }

    fun setStatus(status: Boolean) {
        _isRunning.value = status
    }

    fun setSound(isOn: Boolean) {
        _isSoundOn.value = isOn
    }

    override fun onCleared() {
        super.onCleared()
        _lastTime.value = 0L
        _isRunning.value = false
    }
}