package com.example.simplesleep

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.simplesleep.databinding.ActivityMainBinding
import com.example.simplesleep.ui.home.HomeFragment
import com.example.simplesleep.ui.profile.ProfileFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener{
            val fm = supportFragmentManager
            val homeFragment = HomeFragment()
            val profileFragment = ProfileFragment()
            override fun onItemSelected(id: Int) {
                when(id){
                    R.id.navigation_home -> {
                        fm.beginTransaction()
                            .replace(R.id.container, homeFragment)
                            .commitAllowingStateLoss()
                    }
                    R.id.navigation_profile -> {
                        fm.beginTransaction()
                            .replace(R.id.container, profileFragment)
                            .commitAllowingStateLoss()
                    }
                }
            }
        })
    }
}