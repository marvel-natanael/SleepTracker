package com.example.sleeptracker

import android.app.AlertDialog
import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    private val currentTime = MutableLiveData<Long>()
    private var chronometerPausedTime: Long = 0

    var resultString = "x"

    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time / 1000)
    }

    fun startTimer(meter: Chronometer) {
        meter.base = SystemClock.elapsedRealtime()
        meter.start()
        meter.setOnChronometerTickListener {
            val timeElapsed: Long = SystemClock.elapsedRealtime() - meter.base
            currentTime.value = timeElapsed
        }
    }

    fun pauseTimer(meter: Chronometer) {
        chronometerPausedTime = SystemClock.elapsedRealtime() - meter.base
        meter.stop()
        showResult()
    }

    fun resetTimer(meter: Chronometer) {
        meter.base = SystemClock.elapsedRealtime()
        meter.stop()
    }

    private fun showResult() : String{
        resultString = if(currentTime.value!! >= 5000){
            "x"
        } else "c"
        return resultString
    }
}