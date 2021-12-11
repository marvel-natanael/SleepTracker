package com.example.sleeptracker

import android.content.res.Resources
import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {
    private val currentTime = MutableLiveData<Long>()
    private var chronometerPausedTime: Long = 0

    var resultString = ""
    var resultImage = R.drawable.img_berhasil

    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time / 1000)
        //00 : 00
    }

    var resultMessage = ""

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

    //    resultMessage = convertSecondsToHMmSs(currentTime.value!!)

//        1s = 1000ms
//        1m = 6000ms
//        1h = 360000
    }

    fun resetTimer(meter: Chronometer) {
        meter.base = SystemClock.elapsedRealtime()
        meter.stop()
    }

    fun setDialog(res: Resources){

        val s = TimeUnit.MILLISECONDS.toSeconds(currentTime.value!!)
        val m = TimeUnit.MILLISECONDS.toMinutes(currentTime.value!!)
        val h  = TimeUnit.MILLISECONDS.toHours(currentTime.value!!)

      //  String.format("%d:%02d:%02d", h, m, s)

        if(currentTime.value!! >= 5000){
            resultString = res.getString(R.string.result_cukup)
            resultMessage = "Kamu sudah tidur $h jam $m menit $s detik"
            resultImage = R.drawable.img_berhasil
        }
        else
        {
            resultMessage = "Kamu cuma tidur $h jam $m menit $s detik"
            resultString = res.getString(R.string.result_kurang)
            resultImage = R.drawable.img_gagal
        }
    }
}