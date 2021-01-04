package com.example.worktime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class TimerDialogViewModel : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    var timer : OwnTimer? = null
    var startTime = 10000L

    init {
        timer = OwnTimer(startTime)
    }

    fun startJob() = scope.launch {
            timer?.startT()
        }

    suspend fun stopTimer(){
        startJob().cancelAndJoin()
    }

}