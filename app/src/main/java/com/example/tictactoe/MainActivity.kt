package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

lateinit var engine: Engine
enum class Difficulty{None,Easy,Medium,Hard}

class MainActivity : AppCompatActivity() {

    private var engineInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title_screen)
    }

    private fun initEngine(){
        engine = Engine(this,
            arrayOf(findViewById(R.id.imageButton1),
                findViewById(R.id.imageButton2),
                findViewById(R.id.imageButton3),
                findViewById(R.id.imageButton4),
                findViewById(R.id.imageButton5),
                findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7),
                findViewById(R.id.imageButton8),
                findViewById(R.id.imageButton9)),
            findViewById(R.id.imageView),
            findViewById(R.id.imageView2))

        engineInitialized = true
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View){
        setContentView(R.layout.activity_main)
        if(!engineInitialized) initEngine()
        engine.startNewGame()
    }

    fun newPCGame(view: View){
        setContentView(R.layout.activity_main)
        if(!engineInitialized) initEngine()
        engine.startNewGame(Difficulty.Medium)
    }
}
