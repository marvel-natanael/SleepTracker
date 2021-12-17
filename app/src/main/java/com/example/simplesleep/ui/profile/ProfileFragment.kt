package com.example.simplesleep.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentProfileBinding
import android.content.SharedPreferences




class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentProfileBinding? = null
    var notif = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        loadDAta()

//        binding.switchNotif.isChecked = notif
        binding.switchNotif.setOnClickListener{
            if(binding.switchNotif.isChecked){
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val editNot : SharedPreferences.Editor = sharNot.edit()
                editNot.apply{
                    putBoolean("BOOLEAN_KEY", binding.switchNotif.isChecked)
                }.apply()
            }else{
                val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
                val editNot : SharedPreferences.Editor = sharNot.edit()
                editNot.apply{
                    putBoolean("BOOLEAN_KEY", binding.switchNotif.isChecked)
                }.apply()
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
            }else{
                val alarmNot : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
                val editNotAlarm : SharedPreferences.Editor = alarmNot.edit()
                editNotAlarm.apply{
                    putBoolean("BOOLEAN_KEY_ALARM", binding.switchAlarm.isChecked)
                }.apply()
            }
        }

        // Inflate the layout for this fragment
        return root
    }

    private fun loadDAta(){
        val sharNot : SharedPreferences = requireActivity().getSharedPreferences("shareNotif", Context.MODE_PRIVATE)
        val loadBoleanNotif :Boolean = sharNot.getBoolean("BOOLEAN_KEY", false)

        val AlarmNot : SharedPreferences = requireActivity().getSharedPreferences("shareAlarm", Context.MODE_PRIVATE)
        val loadBoleanAlarm :Boolean = AlarmNot.getBoolean("BOOLEAN_KEY_ALARM", false)

        binding.switchNotif.isChecked = loadBoleanNotif
        binding.switchAlarm.isChecked = loadBoleanAlarm
        notif = loadBoleanNotif
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
}