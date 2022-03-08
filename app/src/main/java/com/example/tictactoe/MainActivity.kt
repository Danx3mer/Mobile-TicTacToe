package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.graphics.drawable.toDrawable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun cellClick(v: View){
        //v.foreground=R.drawable.o.toDrawable()
        val currentImageButton=findViewById<ImageButton>(v.id)
        currentImageButton.setImageResource(R.drawable.x)
        //TODO: Make it so that all of the other buttons could be clicked, and add logic to the clicks.
    }
}