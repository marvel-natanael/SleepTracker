package com.example.sleeptracker

import android.content.res.Resources
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

    var resultString = ""
    var resultImage = R.drawable.img_berhasil

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
    }

    fun resetTimer(meter: Chronometer) {
        meter.base = SystemClock.elapsedRealtime()
        meter.stop()
    }

    fun setDialog(res: Resources){
        if(currentTime.value!! >= 5000){
            resultString = res.getString(R.string.result_cukup)
            resultImage = R.drawable.img_berhasil
        }
        else
        {
            resultString = res.getString(R.string.result_kurang)
            resultImage = R.drawable.img_gagal
        }
    }
}