package com.example.tictactoe

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
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
        engine.startNewGame()
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
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View){ //This creates a new game so that you can play vs another person on one device.
        setContentView(R.layout.activity_main)
        initEngine()
        engine.startNewGame()
    }

    fun newPCGame(view: View){ //This creates a new game with a bot.
        setContentView(R.layout.activity_main)
        initEngine()
        engine.startNewGame(Difficulty.Medium)
    }

    fun restartGame(view: View){ //This creates a new game with the previous difficulty.
        setContentView(R.layout.activity_main)

        val difficulty = engine.currentDifficulty //I do this to save the previous difficulty after Engine has been reset.

        initEngine()
        engine.startNewGame(difficulty)
    }
}
