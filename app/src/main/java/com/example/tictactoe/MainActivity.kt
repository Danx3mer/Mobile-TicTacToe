package com.example.tictactoe

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.chip.Chip
import kotlin.math.abs

lateinit var engine: Engine
enum class Difficulty{None,Easy,Medium,Hard}

class MainActivity : AppCompatActivity() {
    private lateinit var detector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title_screen)

        detector = GestureDetectorCompat(this,GestureListener())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(detector.onTouchEvent(event)) true
        else super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        private val SWIPE_THRESHOLD = 50
        private val SWIPE_VELOCITY_THRESHOLD = 50

        override fun onFling(
            pointerDown: MotionEvent?,
            moveEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float): Boolean {
            var diffX = moveEvent?.x?.minus(pointerDown!!.x) ?:0F
            var diffY = moveEvent?.y?.minus(pointerDown!!.y) ?:0F

            if(abs(diffX) > abs(diffY)) //If this is a horizontal swipe
                if(abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) //If this is a real swipe
                    if(diffX > 0) // l -> r (right) swipe
                        this@MainActivity.backToTitleScreen()

            return super.onFling(pointerDown, moveEvent, velocityX, velocityY)
        }

    }

    fun backToTitleScreen(v:View? = null) {
        setContentView(R.layout.title_screen)
    }

    private fun initEngine(difficulty: Difficulty = Difficulty.None){
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
        engine.startNewGame(difficulty)
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View) { //This creates a new game so that you can play vs another person on one device.
        setContentView(R.layout.activity_main)
        initEngine()
        engine.startNewGame()

        findViewById<Chip>(R.id.chip5).visibility = View.GONE
        findViewById<Chip>(R.id.chip6).visibility = View.GONE
        findViewById<Chip>(R.id.chip7).visibility = View.GONE
    }

    fun newPCGameEasy(view: View) {
        initEngine()
        engine.startNewGame(Difficulty.Easy)
    }

    fun newPCGameMedium(view: View) {
        setContentView(R.layout.activity_main)
        initEngine()
        engine.startNewGame(Difficulty.Medium)
    }

    fun newPCGameHard(view: View) {
        initEngine()
        engine.startNewGame(Difficulty.Hard)
    }

    fun restartGame(view: View) { //This creates a new game with the previous difficulty.
        val difficulty = engine.currentDifficulty //I do this to save the previous difficulty after Engine has been reset.

        initEngine()
        engine.startNewGame(difficulty)

        if(difficulty == Difficulty.None){
            findViewById<Chip>(R.id.chip5).visibility = View.GONE
            findViewById<Chip>(R.id.chip6).visibility = View.GONE
            findViewById<Chip>(R.id.chip7).visibility = View.GONE
        }
        else {
            findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
        }
    }

    fun setScreenAbout(view :View) = setContentView(R.layout.info_screen)
}
