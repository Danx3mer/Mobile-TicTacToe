package com.example.tictactoe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

lateinit var engine: Engine
enum class Difficulty{None,Easy,Medium,Hard}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title_screen)

        /*engine = Engine(this,
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
            findViewById(R.id.imageView2))*/
    }

    fun cellClick(view: View) = engine.fieldClick(view)
    fun newGame(view: View) = engine.startNewGame()
    fun newPCGame(view: View) = engine.startNewGame(Difficulty.Medium)
}
