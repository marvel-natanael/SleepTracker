package com.example.simplesleep.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    private val _isNotif = MutableLiveData<Boolean>()
    val isNotif: LiveData<Boolean> = _isNotif


    val text: LiveData<String> = _text

    private fun Notifikasi(){
        _isNotif.value = true
    }


    
}