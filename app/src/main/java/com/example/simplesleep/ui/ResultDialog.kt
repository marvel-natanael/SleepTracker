package com.example.simplesleep.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.simplesleep.R


class ResultDialog: DialogFragment() {
    var resultText = ""
    var resultImage = R.drawable.ic_notifications_black_24dp
    var resultSleepTime = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_result, null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val description = view.findViewById<TextView>(R.id.description)
        val btnClose = view.findViewById<Button>(R.id.close_button)
        val img = view.findViewById<ImageView>(R.id.image)
        val sleepTime = view.findViewById<TextView>(R.id.sleep_time_tv)

        description.text = resultText
        img.setImageResource(resultImage)
        sleepTime.text = resultSleepTime

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        return super.onCreateDialog(savedInstanceState)
    }
}