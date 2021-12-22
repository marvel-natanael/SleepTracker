package com.example.simplesleep.ui.home
import android.app.*
import android.content.*
import android.content.Context.ALARM_SERVICE
import android.media.Ringtone
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplesleep.MainActivity
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentHomeBinding
import com.example.simplesleep.ui.ResultDialog
import java.util.*
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var maxRecomSleepTimeArray: Array<String>
    private lateinit var minRecomSleepTimeArray: Array<String>
    private lateinit var maxApproSleepTimeArray: Array<String>
    private lateinit var minApproSleepTimeArray: Array<String>
    private lateinit var contexts: Context
    var data = 0
    //notifikasi

    private val CHANNEL_ID = "Channel_id"
    private val CHANNEL_NAME = "notif"


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val receiver = object : BroadcastReceiver() {
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        requireActivity().registerReceiver(receiver, intentFilter)

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

        //Wake up time
        wakeUpTime = homeViewModel.getWakeUpTime(addedHours, Date())
        binding.midSmallTv.text = SimpleDateFormat("HH:mm").format(wakeUpTime)

        //Timer
        binding.cMeter.text = getString(R.string.start_button)

        //Optimal
        binding.optimalHoursTv.text = "$addedHours - $maxRecomSleepTime hours"
        //Unrecommended
        binding.lessHoursTv.text = "< $minSleepTime hours\n> $maxSleepTime hours"
        //Appropriate
        binding.moreHoursTv.text = "$minSleepTime - $addedHours hours\n$maxRecomSleepTime - $maxSleepTime hours"
        contexts = requireActivity()

        // setting default ringtone
        var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri)

        binding.alarmButton.setOnClickListener {
            if (!homeViewModel.isWorking) {
                homeViewModel.resetTimer(binding.cMeter)
                binding.alarmButton.background = resources.getDrawable(R.drawable.btn_proses)
                binding.midSmallTv.text = ""
                binding.midBoldTv.text = getString(R.string.turn_off_screen)
                binding.alarmSetTv.text = "Alarm is set at\n$wakeUpTime"
                //notif
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val sharAlarm : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
                val loadBoleanNotif :Boolean = sharNot.getBoolean("BOOLEAN_KEY", false)
                val loadBooleanAlarm : Boolean = sharAlarm.getBoolean("BOOLEAN_KEY_ALARM", false)

                if (loadBoleanNotif){
                    showNotification(requireContext())
                }
                if (loadBooleanAlarm){
                    ringAlarm(wakeUpTime)
                }

            } else {
                cancelAlarm()
                binding.midBoldTv.text = getString(R.string.wake_up_time)
                binding.cMeter.text = getString(R.string.start_button)
                binding.alarmButton.background = resources.getDrawable(R.drawable.btn_start_end)
                binding.midSmallTv.text = SimpleDateFormat("HH:mm").format(wakeUpTime)
                binding.alarmSetTv.text = getString(R.string.alarm_set)
            }
            homeViewModel.isWorking = !homeViewModel.isWorking
        }
        return root
    }

    class Receiver: BroadcastReceiver(){
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("HomeFragment", "Reciver: "+ Date().toString())
            val i = Intent(context, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ringtone.play()
            ringtone.isLooping = true
        }
    }

    private fun ringAlarm(alarmTime: Date){
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, Receiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.time, pendingIntent)
        Toast.makeText(context, "Alarm Diset "+Date().toString(), Toast.LENGTH_LONG).show()
    }

    private fun cancelAlarm(){
        ringtone.stop()
    }

    private fun showNotification(context: Context) {
        val message = "Turn off your screen to start!"
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Simple Sleep is ongoing...")
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
        requireActivity().unregisterReceiver(receiver)
    }

    companion object{
        private lateinit var ringtone: Ringtone
        private lateinit var wakeUpTime: Date
    }
}