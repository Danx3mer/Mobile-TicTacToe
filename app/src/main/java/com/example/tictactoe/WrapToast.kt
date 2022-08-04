package com.example.tictactoe

import android.app.Activity
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

fun Toast.showCustomToast(message: String, activity: Activity)
{
    val layout = activity.layoutInflater.inflate (
        R.layout.toast,
        activity.findViewById(R.id.toast_container)
    )

    // set the text of the TextView of the message
    val textView = layout.findViewById<TextView>(R.id.toast_text)
    textView.text = message

    // use the application extension function
    this.apply {
        setGravity(Gravity.CENTER, 0, 40)
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}