package com.example.sleeptracker

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ResultDialog: DialogFragment() {
    var resultText = ""
    var resultImage = R.drawable.img_berhasil
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
        val btnClose = view.findViewById<Button>(R.id.closeButton)
        val img = view.findViewById<ImageView>(R.id.image)
        val sleepTime = view.findViewById<TextView>(R.id.sleep_time)

        description.text = resultText
        img.setImageResource(resultImage)
        sleepTime.text = resultSleepTime

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        return super.onCreateDialog(savedInstanceState)
    }
}