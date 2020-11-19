package com.example.worktime

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var _timeLong  = MutableLiveData<Long>()
    val timeLong: LiveData<Long>
        get() = _timeLong
    init {
        Log.i("[:::MainViewModel:::]", "-> initialized")
    }

    fun setTimeLong(value: Long){
        viewModelScope.launch {
            _timeLong.value = value
            Log.i("[:::MainViewModel:::]", "-> Got new value: ${timeLong.value}")
        }
    }
}