package com.example.simplesleep.ui.profile

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentProfileBinding
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import com.example.simplesleep.ui.notifications.NotifikasiReceiver
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentProfileBinding? = null
    var notif = false
    var alarm = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var picker: MaterialTimePicker
    private  lateinit var calendar: Calendar
    private  lateinit var alarmManager : AlarmManager

    private var spinAdapter: ArrayAdapter<CharSequence>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        spinAdapter = activity?.applicationContext?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.age,
                android.R.layout.simple_spinner_item
            )
        }
        val shared = requireActivity().getSharedPreferences("KEY_PREF", Context.MODE_PRIVATE)
        val position = shared.getInt("KEY_POS", 0)

        _binding?.ageSpinner?.adapter = spinAdapter
        _binding?.ageSpinner?.onItemSelectedListener = this
        spinAdapter?.setDropDownViewResource(
            R.layout.support_simple_spinner_dropdown_item)

        _binding?.ageSpinner?.setSelection(position, true)

        if (_binding?.ageSpinner != null) {
            _binding?.ageSpinner?.adapter = spinAdapter
            _binding?.ageSpinner?.onItemSelectedListener = this
        }
        loadData()
        //notifikasi
        showNotifications()
        binding.switchNotif.setOnClickListener{
            if(binding.switchNotif.isChecked){
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val editNot : SharedPreferences.Editor = sharNot.edit()
                editNot.apply{
                    putBoolean("BOOLEAN_KEY", binding.switchNotif.isChecked)
                }.apply()
                binding.setNot.isEnabled = true
                Toast.makeText(context, "Notifikasi Dihidupkan", Toast.LENGTH_SHORT).show()
            }else{
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val editNot : SharedPreferences.Editor = sharNot.edit()
                editNot.apply{
                    putBoolean("BOOLEAN_KEY", binding.switchNotif.isChecked)
                }.apply()
                cancelNotifikasi()
                binding.setNot.isEnabled = false
            }
        }
        //alarm switch
        binding.switchAlarm.setOnClickListener{
            if(binding.switchAlarm.isChecked){
                val alarmNot : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
                val editNotAlarm : SharedPreferences.Editor = alarmNot.edit()
                editNotAlarm.apply{
                    putBoolean("BOOLEAN_KEY_ALARM", binding.switchAlarm.isChecked)
                }.apply()
                Toast.makeText(context, "Alarm has been set", Toast.LENGTH_SHORT).show()
            }else{
                val alarmNot : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
                val editNotAlarm : SharedPreferences.Editor = alarmNot.edit()
                editNotAlarm.apply{
                    putBoolean("BOOLEAN_KEY_ALARM", binding.switchAlarm.isChecked)
                }.apply()
                Toast.makeText(context, "Alarm off", Toast.LENGTH_SHORT).show()
            }
        }
        //timePicker
        binding.btnTime.setOnClickListener{
            showTimer()
        }
        binding.setNot.setOnClickListener{
            setNotifikasi()
        }
        // Inflate the layout for this fragment
        return root
    }

    private fun showTimer(){
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Notification Time")
            .build()

        picker.show(childFragmentManager, "foxandroid")
        picker.addOnPositiveButtonClickListener{
            if (picker.hour > 12){
                binding.selecttime.text =
                    String.format("%02d", picker.hour - 12)+":" + String.format("%02d", picker.minute) + "PM"
            }else{
                binding.selecttime.text = String.format("%02d", picker.hour)+":" + String.format("%02d", picker.minute) + "AM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun loadData(){
        val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
        val loadBoleanNotif :Boolean = sharNot.getBoolean("BOOLEAN_KEY", false)

        val AlarmNot : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
        val loadBoleanAlarm :Boolean = AlarmNot.getBoolean("BOOLEAN_KEY_ALARM", false)

        binding.switchNotif.isChecked = loadBoleanNotif
        binding.switchAlarm.isChecked = loadBoleanAlarm
        notif = loadBoleanNotif
        alarm = loadBoleanAlarm
        binding.setNot.isEnabled = notif
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val shared = requireActivity().getSharedPreferences("KEY_PREF", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putInt("KEY_AGE", p2)
        _binding?.ageSpinner?.selectedItemPosition?.let { editor.putInt("KEY_POS", it) }
        editor.apply()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        val shared = requireActivity().getSharedPreferences("KEY_PREF", Context.MODE_PRIVATE)
        val pos = shared.getInt("KEY_POS", 0)
        val name = p0?.getItemAtPosition(pos)

        val editor = shared.edit()
        editor.putString("KEY_AGE", name.toString())
        _binding?.ageSpinner?.selectedItemPosition?.let { editor.putInt("KEY_POS", it) }
        editor.apply()
    }

    private fun showNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foxandroid",
                "foxandroideminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notifMana = context?.getSystemService(
                NotificationManager::class.java
            )
            notifMana?.createNotificationChannel(channel)
        }
    }

    private fun cancelNotifikasi(){
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val inten = Intent(context, NotifikasiReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100 , inten, 0)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Notification Off", Toast.LENGTH_SHORT).show()
    }

    private fun setNotifikasi(){
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val inten = Intent(context, NotifikasiReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100 , inten, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        Toast.makeText(context, "Notification Updated", Toast.LENGTH_SHORT).show()
    }
}