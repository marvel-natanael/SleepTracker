package com.example.simplesleep.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentHomeBinding
import com.example.simplesleep.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var spinAdapter: ArrayAdapter<CharSequence>? = null
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
        Toast.makeText(requireContext(), position.toString(), Toast.LENGTH_LONG).show()

//
        _binding?.ageSpinner?.adapter = spinAdapter
        _binding?.ageSpinner?.onItemSelectedListener = this
        spinAdapter?.setDropDownViewResource(
            R.layout.support_simple_spinner_dropdown_item)


        _binding?.ageSpinner?.setSelection(position, true)
//
        if (_binding?.ageSpinner != null) {
            _binding?.ageSpinner?.adapter = spinAdapter
            _binding?.ageSpinner?.onItemSelectedListener = this
        }
//
        // Inflate the layout for this fragment
        return root
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val name = p0?.getItemAtPosition(p2).toString()
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