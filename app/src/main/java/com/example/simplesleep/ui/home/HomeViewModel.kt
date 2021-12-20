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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeViewModel : ViewModel() {
    private val currentTime = MutableLiveData<Long>()
    private var chronometerPausedTime: Long = 0

    var resultString = ""
    var resultImage = R.drawable.ic_home_black_24dp
    var isWorking = false
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

    fun setDialog(
        res: Resources,
        minRecom: Long,
        maxRecom: Long,
        minApp: Long,
        maxApp: Long
    ) {

        val s = TimeUnit.MILLISECONDS.toSeconds(currentTime.value!!)
        val m = TimeUnit.MILLISECONDS.toMinutes(currentTime.value!!)
        val h = TimeUnit.MILLISECONDS.toHours(currentTime.value!!)

        when {
            //recommended
            currentTime.value!! in minRecom..maxRecom -> {
                resultString = res.getString(R.string.enough_sleep)
                resultImage = R.drawable.img_berhasil
            }
            //not recommended less
            currentTime.value!! <= minApp -> {
                resultString = res.getString(R.string.less_sleep)
                resultImage = R.drawable.img_gagal
            }
            //not recommended more
            currentTime.value!! >= maxApp -> {
                resultString = res.getString(R.string.more_sleep)
                resultImage = R.drawable.img_gagal
            }
            //appropriate
            currentTime.value!! in minApp..minRecom || currentTime.value!! in maxApp..maxRecom->{
                resultString = res.getString(R.string.appropriate_sleep)
                resultImage = R.drawable.img_gagal
            }
        }
        resultSleepTime = "You have slept for $h hours $m minutes $s seconds"
    }

    fun getWakeUpTime(addedTime: Int, currentTime: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentTime
        calendar.add(Calendar.HOUR, addedTime)

        return calendar.time
    }

    fun parseData(arr: Array<String>,data: Int): Int{
        return arr[data].toInt()/1000
    }
}