package com.example.sleeptracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.sleeptracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val meter = binding.cMeter
        val btn = binding.alarmButton
        var isWorking = false

        btn.setOnClickListener {
            isWorking = if (!isWorking) {
                meter.start()
                true
            } else {
                meter.stop()
                false
            }

            Toast.makeText(this@MainActivity, getString(
                if (isWorking)
                    R.string.working
                else
                    R.string.stopped),
                Toast.LENGTH_SHORT).show()
        }

    }
}