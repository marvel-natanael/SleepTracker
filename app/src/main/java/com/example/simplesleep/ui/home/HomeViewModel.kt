package com.example.simplesleep.ui.home

import android.content.res.Resources
import android.os.SystemClock
import android.text.format.DateUtils
import android.widget.Chronometer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.simplesleep.R
import java.util.concurrent.TimeUnit

class HomeViewModel : ViewModel() {
    private val currentTime = MutableLiveData<Long>()
    private var chronometerPausedTime: Long = 0

    var resultString = ""
    var resultImage = R.drawable.ic_home_black_24dp
    var isWorking = true
    var resultSleepTime = ""

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

    fun setDialog(res: Resources, min: Long, max: Long){

        val s = TimeUnit.MILLISECONDS.toSeconds(currentTime.value!!)
        val m = TimeUnit.MILLISECONDS.toMinutes(currentTime.value!!)
        val h  = TimeUnit.MILLISECONDS.toHours(currentTime.value!!)

        when {
            currentTime.value!! in min..max -> {
                resultString = res.getString(R.string.enough_sleep)
                resultSleepTime = "Kamu sudah tidur $h jam $m menit $s detik"
                resultImage = R.drawable.ic_home_black_24dp
            }
            currentTime.value!! <= min -> {
                resultString = res.getString(R.string.less_sleep)
                resultSleepTime = "Kamu cuma tidur $h jam $m menit $s detik"
                resultImage = R.drawable.ic_notifications_black_24dp
            }
            else -> {
                resultString = res.getString(R.string.less_sleep)
                resultSleepTime = "Tidurmu $h jam $m menit $s detik terlalu lama"
                resultImage = R.drawable.ic_notifications_black_24dp
            }
        }
    }
}