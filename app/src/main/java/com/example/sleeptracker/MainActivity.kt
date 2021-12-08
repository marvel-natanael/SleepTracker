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
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager

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

    fun alert()
    {
        val fm = supportFragmentManager
        val resultDialog = ResultDialog()
        resultDialog.resultText = getString(R.string.result_cukup)
        resultDialog.show(fm, "fragment_alert")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, intentFilter)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        binding.alarmButton.setOnClickListener {
            alert()
        }

        viewModel.currentTimeString.observe(this) {
            binding.clockTextView.text = it
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