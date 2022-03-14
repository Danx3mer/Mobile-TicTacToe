package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView

lateinit var engine: Engine

class MainActivity : AppCompatActivity() {

    lateinit var currentTurnButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        engine = Engine(arrayOf(findViewById<ImageButton>(R.id.imageButton1),
            findViewById<ImageButton>(R.id.imageButton2),
            findViewById<ImageButton>(R.id.imageButton3),
            findViewById<ImageButton>(R.id.imageButton4),
            findViewById<ImageButton>(R.id.imageButton5),
            findViewById<ImageButton>(R.id.imageButton6),
            findViewById<ImageButton>(R.id.imageButton7),
            findViewById<ImageButton>(R.id.imageButton8),
            findViewById<ImageButton>(R.id.imageButton9)),
            findViewById<ImageView>(R.id.imageView))
    }

    fun cellClick(view: View) = engine.fieldClick(view)
    fun reset(view: View) = engine.resetField()
}
