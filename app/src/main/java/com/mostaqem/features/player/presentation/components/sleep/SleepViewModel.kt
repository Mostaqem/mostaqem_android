package com.mostaqem.features.player.presentation.components.sleep

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor() : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    fun startTimer(durationMillis: Long, onTimerFinished: () -> Unit) {
        countDownTimer?.cancel()
        _isTimerRunning.value = true
        _remainingTime.value = durationMillis
        countDownTimer = object : CountDownTimer(durationMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingTime.value = millisUntilFinished
            }

            override fun onFinish() {
                _isTimerRunning.value = false
                _remainingTime.value = 0L
                onTimerFinished()
            }
        }.start()
    }


    fun cancelTimer() {
        countDownTimer?.cancel()
        _isTimerRunning.value = false
        _remainingTime.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }

}