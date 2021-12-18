package com.example.simplesleep.ui.home


import android.app.*
import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplesleep.MainActivity
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentHomeBinding
import com.example.simplesleep.ui.ResultDialog
import com.example.simplesleep.ui.profile.ProfileFragment
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var maxRecomSleepTimeArray: Array<String>
    private lateinit var minRecomSleepTimeArray: Array<String>
    private lateinit var maxApproSleepTimeArray: Array<String>
    private lateinit var minApproSleepTimeArray: Array<String>
    private lateinit var contexts: Context
    private lateinit var alarmManager : AlarmManager
    var data = 0
    //notifikasi
    private lateinit var notificationManager: NotificationManagerCompat
    private val CHANNEL_ID = "Channel_id"
    private val CHANNEL_NAME = "notif"
    private val notifikasiId = 101

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
        contexts = requireActivity()
        binding.alarmButton.setOnClickListener {
            if (!homeViewModel.isWorking) {
                homeViewModel.resetTimer(binding.cMeter)
                binding.alarmButton.background = resources.getDrawable(R.drawable.btn_proses)
                binding.midSmallTv.text = ""
                binding.midBoldTv.text = getString(R.string.turn_off_screen)
                //notif
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val loadBoleanNotif :Boolean = sharNot.getBoolean("BOOLEAN_KEY", false)

                if (loadBoleanNotif){
                    showNotification()
                }
                //alarm
                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val inten = Intent(context, Receiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, 100, inten, 0)
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent)
                Toast.makeText(context, "Alarm Diset "+Date().toString(), Toast.LENGTH_LONG).show()
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

    class Receiver: BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("HomeFragment", "Reciver: "+ Date().toString())
            Toast.makeText(context, "Alarm sedang berbunyi"+Date().toString(), Toast.LENGTH_LONG).show()
            var i = Intent(context, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(i)
            val obj = HomeFragment()
            obj.showNotification()
        }

    }



    fun showNotification() {
        val message = "Waktu akan berjalan ketika kamu matikan layar"

        val notificationManagerCompat =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Simple Sleep Sedang Berlangsung...")
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(100, notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(mReceiver)
    }
}