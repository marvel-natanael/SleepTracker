package com.example.simplesleep.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentHomeBinding
import com.example.simplesleep.ui.ResultDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var maxSleepTimeArray: Array<String>
    private lateinit var minSleepTimeArray: Array<String>
    var data = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_ON == action) {
                binding.midBoldTv.visibility = View.VISIBLE
                Handler().postDelayed(this::add, 3000)
            } else if (Intent.ACTION_SCREEN_OFF == action) {
                homeViewModel.startTimer(binding.cMeter)
            }
        }

        fun add() {
            homeViewModel.pauseTimer(binding.cMeter)
            alert()
        }
    }

    fun alert() {
        val fm = childFragmentManager
        val resultDialog = ResultDialog()
        homeViewModel.setDialog(resources,
            minSleepTimeArray[data].toLong(),
            maxSleepTimeArray[data].toLong())
        resultDialog.resultText = homeViewModel.resultString
        resultDialog.resultImage = homeViewModel.resultImage

        homeViewModel.currentTimeString.observe(this, {
            resultDialog.resultSleepTime = it
        })

        resultDialog.resultSleepTime = homeViewModel.resultSleepTime

        resultDialog.show(fm, "fragment_alert")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        requireActivity().registerReceiver(mReceiver, intentFilter)

        binding.midSmallTv.text = getWakeUpTime()

        val shared = requireActivity().getSharedPreferences("KEY_PREF", Context.MODE_PRIVATE)
        maxSleepTimeArray = resources.getStringArray(R.array.sleep_time_max)
        minSleepTimeArray = resources.getStringArray(R.array.sleep_time_min)
        data = shared.getInt("KEY_AGE", 0)

        binding.optimalSleepTimeTv.text =
            "${getString(R.string.optimal_sleep_time)} ${maxSleepTimeArray[data]} hours"

        binding.alarmButton.setOnClickListener {
            if (homeViewModel.isWorking) {
                homeViewModel.resetTimer(binding.cMeter)
                binding.midSmallTv.text = ""
                binding.midBoldTv.text = getString(R.string.turn_off_screen)
            } else {
                binding.midBoldTv.text = getString(R.string.wake_up_time)
                binding.midSmallTv.text = getWakeUpTime()
            }
            homeViewModel.isWorking = !homeViewModel.isWorking
        }

        return root
    }

    private fun getWakeUpTime(): String {
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

        return wakeUpTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(mReceiver)
    }
}