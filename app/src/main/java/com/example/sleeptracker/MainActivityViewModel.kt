package com.example.sleeptracker

import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private var isWorking = false
    private val currentTime = MutableLiveData<Long>()
    private var chronometerPausedTime: Long = 0

    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time / 1000)
    }

    fun startTimer(meter: Chronometer) {
        isWorking = if (!isWorking) {
            meter.base = SystemClock.elapsedRealtime()
            meter.start()
            meter.setOnChronometerTickListener {
                val timeElapsed: Long = SystemClock.elapsedRealtime() - meter.base
                currentTime.value = timeElapsed
            }
            true
        } else {
            pauseTimer(meter)
            false
        }
    }

    fun pauseTimer(meter: Chronometer) {
        chronometerPausedTime = SystemClock.elapsedRealtime() - meter.base
        meter.stop()
    }

    fun resetTimer(meter: Chronometer){
        meter.base = SystemClock.elapsedRealtime()
    }
}