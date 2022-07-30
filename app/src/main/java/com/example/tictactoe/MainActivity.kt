package com.example.tictactoe

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.chip.Chip
import kotlin.math.abs

lateinit var engine: Engine
enum class Difficulty{None,Easy,Medium,Hard}

private var mediaPlayer: MediaPlayer? = null
fun playSound(resource: Int, context: Context) {
    if (mediaPlayer != null) {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
    }
    mediaPlayer = MediaPlayer.create(context, resource)
    mediaPlayer!!.isLooping = false
    mediaPlayer!!.start()
}

class MainActivity : AppCompatActivity() {
    private lateinit var detector: GestureDetectorCompat
    private lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title_screen)
        detector = GestureDetectorCompat(this,GestureListener())
        screenTracker = ScreenTracker(R.layout.title_screen)
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(detector.onTouchEvent(event)) true
        else super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        private val swipeThreshold = 350
        private val swipeVelocityThreshold = 250

        override fun onFling(
            pointerDown: MotionEvent?,
            moveEvent: MotionEvent?,
            velocityX: Float,
            velocityY: Float): Boolean {
            val diffX = moveEvent?.x?.minus(pointerDown!!.x) ?:0F
            val diffY = moveEvent?.y?.minus(pointerDown!!.y) ?:0F

            if(abs(diffX) > abs(diffY)) //If this is a horizontal swipe
                if(abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) //If this is a real swipe
                    if(diffX > 0) // l -> r (right) swipe
                        this@MainActivity.backToLastScreen()

            return super.onFling(pointerDown, moveEvent, velocityX, velocityY)
        }
    }

    private inner class ScreenTracker(initialScreen: Int) {
        var currentScreen: Int = initialScreen
        private set

        var pastScreen: Int = initialScreen
        private set

        fun updateScreen(newScreen: Int) {
            pastScreen = when(newScreen)
            {
                R.layout.activity_main -> R.layout.title_screen
                R.layout.title_screen -> R.layout.title_screen
                else -> this.currentScreen
            }
            this.currentScreen = newScreen
            setContentView(newScreen)
        }
    }

    fun backToLastScreen(v:View? = null) = screenTracker.updateScreen(screenTracker.pastScreen)

    private fun initEngine(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean = false){
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
            findViewById(R.id.imageView2),
            computerGoesFirst)
        engine.startNewGame(difficulty)
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View) { //This creates a new game so that you can play vs another person on one device.
        screenTracker.updateScreen(R.layout.activity_main)
        initEngine()

        findViewById<Chip>(R.id.chip5).visibility = View.GONE
        findViewById<Chip>(R.id.chip6).visibility = View.GONE
        findViewById<Chip>(R.id.chip7).visibility = View.GONE
        findViewById<ToggleButton>(R.id.toggleButton).visibility = View.GONE
    }

    fun newPCGameEasy(view: View) = initEngine(Difficulty.Easy, engine.computerGoesFirst)

    fun newPCGameMedium(view: View) = initEngine(Difficulty.Medium, engine.computerGoesFirst)

    fun newPCGameHard(view: View) = initEngine(Difficulty.Hard, engine.computerGoesFirst)

    fun pcGameStartup(view: View) {
        screenTracker.updateScreen(R.layout.activity_main)
        initEngine(Difficulty.Medium)
    }

    fun switchFirstTurn(view: View) {
        initEngine(engine.currentDifficulty, !engine.computerGoesFirst)

        if(engine.currentDifficulty == Difficulty.None){
            findViewById<Chip>(R.id.chip5).visibility = View.GONE
            findViewById<Chip>(R.id.chip6).visibility = View.GONE
            findViewById<Chip>(R.id.chip7).visibility = View.GONE
            findViewById<ToggleButton>(R.id.toggleButton).visibility = View.GONE
        }
        else {
            findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
            findViewById<ToggleButton>(R.id.toggleButton).visibility = View.VISIBLE
        }
    }

    fun restartGame(view: View) { //This creates a new game with the previous difficulty.
        initEngine(engine.currentDifficulty, engine.computerGoesFirst)

        if(engine.currentDifficulty == Difficulty.None){
            findViewById<Chip>(R.id.chip5).visibility = View.GONE
            findViewById<Chip>(R.id.chip6).visibility = View.GONE
            findViewById<Chip>(R.id.chip7).visibility = View.GONE
            findViewById<ToggleButton>(R.id.toggleButton).visibility = View.GONE
        }
        else {
            findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
            findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
            findViewById<ToggleButton>(R.id.toggleButton).visibility = View.VISIBLE
        }
    }

    fun setScreenAbout(view :View) = screenTracker.updateScreen(R.layout.info_screen)
    fun setScreenSettings(view :View) = screenTracker.updateScreen(R.layout.settings)
}
