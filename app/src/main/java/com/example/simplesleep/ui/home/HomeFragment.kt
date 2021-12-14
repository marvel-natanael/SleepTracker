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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var maxRecomSleepTimeArray: Array<String>
    private lateinit var minRecomSleepTimeArray: Array<String>
    private lateinit var maxApproSleepTimeArray: Array<String>
    private lateinit var minApproSleepTimeArray: Array<String>
    var data = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_ON == action && homeViewModel.isWorking) {
                binding.midBoldTv.visibility = View.VISIBLE
                Handler().postDelayed(this::add, 3000)
            } else if (Intent.ACTION_SCREEN_OFF == action && homeViewModel.isWorking) {
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
            minRecomSleepTimeArray[data].toLong(),
            maxRecomSleepTimeArray[data].toLong(),
            minApproSleepTimeArray[data].toLong(),
            maxApproSleepTimeArray[data].toLong())
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

        val shared = requireActivity().getSharedPreferences("KEY_PREF", Context.MODE_PRIVATE)
        maxRecomSleepTimeArray = resources.getStringArray(R.array.sleep_time_recommended_max)
        minRecomSleepTimeArray = resources.getStringArray(R.array.sleep_time_recommended_min)
        maxApproSleepTimeArray = resources.getStringArray(R.array.sleep_time_appropriate_max)
        minApproSleepTimeArray = resources.getStringArray(R.array.sleep_time_appropriate_min)

        data = shared.getInt("KEY_AGE", 0)
        val addedHours = homeViewModel.parseData(minRecomSleepTimeArray, data)
        val maxRecomSleepTime = homeViewModel.parseData(maxRecomSleepTimeArray, data)
        val minSleepTime = homeViewModel.parseData(minApproSleepTimeArray, data)
        val maxSleepTime = homeViewModel.parseData(maxApproSleepTimeArray, data)
        binding.midSmallTv.text = homeViewModel.getWakeUpTime(addedHours)
        binding.cMeter.text = getString(R.string.start_button)

        //Optimal
        binding.optimalHoursTv.text = "$addedHours - $maxRecomSleepTime hours"
        //Unrecommended
        binding.lessHoursTv.text = "< $minSleepTime hours\n> $maxSleepTime hours"
        //Appropriate
        binding.moreHoursTv.text = "$minSleepTime - $addedHours hours\n$maxRecomSleepTime - $maxSleepTime hours"

        binding.alarmButton.setOnClickListener {
            if (!homeViewModel.isWorking) {
                homeViewModel.resetTimer(binding.cMeter)
                binding.alarmButton.background = resources.getDrawable(R.drawable.btn_proses)
                binding.midSmallTv.text = ""
                binding.midBoldTv.text = getString(R.string.turn_off_screen)
            } else {
                binding.midBoldTv.text = getString(R.string.wake_up_time)
                binding.cMeter.text = getString(R.string.start_button)
                binding.alarmButton.background = resources.getDrawable(R.drawable.btn_start_end)
                binding.midSmallTv.text = homeViewModel.getWakeUpTime(addedHours)
            }
            homeViewModel.isWorking = !homeViewModel.isWorking
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(mReceiver)
    }
}