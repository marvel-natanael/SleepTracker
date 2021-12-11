package com.example.simplesleep.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.simplesleep.R
import com.example.simplesleep.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

   private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
     val age = _binding?.ageSpinner
     age?.onItemSelectedListener = this
     val spinAdapter = activity?.applicationContext?.let {
         ArrayAdapter.createFromResource(
             it,
             R.array.age,
             android.R.layout.simple_spinner_item
         )
     }
        spinAdapter?.setDropDownViewResource(
            R.layout.support_simple_spinner_dropdown_item)
        if (age != null) {
            age.adapter = spinAdapter
            age.onItemSelectedListener = this
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}