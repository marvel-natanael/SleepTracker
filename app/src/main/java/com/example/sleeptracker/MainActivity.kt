package com.example.sleeptracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.sleeptracker.databinding.ActivityMainBinding
import android.content.IntentFilter
import android.view.View
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_ON == action) {
                binding.textView.visibility = View.VISIBLE
                Handler().postDelayed(this::add, 3000)
            } else if (Intent.ACTION_SCREEN_OFF == action) {
                viewModel.startTimer(binding.cMeter)
            }
        }

        fun add() {
            viewModel.pauseTimer(binding.cMeter)
            alert()
        }
    }

    fun alert() {
        val fm = supportFragmentManager
        val resultDialog = ResultDialog()
        resultDialog.resultText = viewModel.showResultText(resources)
        resultDialog.resultImage = viewModel.showResultImage()
        resultDialog.show(fm, "fragment_alert")

        binding.textView.text = getString(R.string.you_have_slept)
        viewModel.currentTimeString.observe(this) {
            binding.clockTextView.text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, intentFilter)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        var date: Date? = null

        try {
            date = sdf.parse(currentTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR, 8)

        val wakeUpTime =
            calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE)
                .toString()
        binding.textView.text = getString(R.string.wake_up_text)
        binding.clockTextView.text = wakeUpTime

        binding.cMeter.text = getString(R.string.start)

        binding.alarmButton.setOnClickListener {
            viewModel.resetTimer(binding.cMeter)
            binding.textView.visibility = View.GONE
            binding.clockTextView.text = getString(R.string.Turn_off_screen)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingIntent = Intent(this, SettingActivity::class.java)
                startActivity(settingIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
}