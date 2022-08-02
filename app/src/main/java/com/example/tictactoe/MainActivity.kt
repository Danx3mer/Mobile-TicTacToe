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
    if(!engine.soundOn) return
    stopAllSounds()
    mediaPlayer = MediaPlayer.create(context, resource)
    mediaPlayer!!.isLooping = false
    mediaPlayer!!.start()
}

fun stopAllSounds(){
    if (mediaPlayer != null) {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var detector: GestureDetectorCompat
    private lateinit var dataTracker: DataTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.title_screen)
        detector = GestureDetectorCompat(this,GestureListener())
        dataTracker = DataTracker(R.layout.title_screen)
        engine = Engine(this)
    }

    override fun onStop() {
        super.onStop()
        stopAllSounds()
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

    private inner class DataTracker(initialScreen: Int) {
        var currentScreen: Int = initialScreen
        private set

        var pastScreen: Int = initialScreen
        private set

        fun updateScreen(newScreen: Int) {
            stopAllSounds()
            pastScreen = when(newScreen)
            {
                R.layout.activity_main -> R.layout.title_screen
                R.layout.title_screen -> R.layout.title_screen
                else -> this.currentScreen
            }
            this.currentScreen = newScreen
            setContentView(newScreen)

            if(engine.isInitialized) {
                if(newScreen == R.layout.activity_main) restartGame(null)
                else startGame(engine.currentDifficulty, engine.computerGoesFirst)
            }
        }
    }

    fun backToLastScreen(v:View? = null) = dataTracker.updateScreen(dataTracker.pastScreen)

    private fun startGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean = false, reInitEngine: Boolean = false){
        if(reInitEngine)
            engine.fullInit(arrayOf(findViewById(R.id.imageButton1),
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
        engine.startNewGame(difficulty, computerGoesFirst)
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View) { //This creates a new game so that you can play vs another person on one device.
        dataTracker.updateScreen(R.layout.activity_main)
        startGame(reInitEngine = true)

        findViewById<Chip>(R.id.chip5).visibility = View.GONE
        findViewById<Chip>(R.id.chip6).visibility = View.GONE
        findViewById<Chip>(R.id.chip7).visibility = View.GONE
        findViewById<ToggleButton>(R.id.toggleButton).visibility = View.GONE
    }

    fun newPCGameEasy(view: View) = startGame(Difficulty.Easy, engine.computerGoesFirst, true)

    fun newPCGameMedium(view: View) = startGame(Difficulty.Medium, engine.computerGoesFirst, true)

    fun newPCGameHard(view: View) = startGame(Difficulty.Hard, engine.computerGoesFirst, true)

    fun pcGameStartup(view: View) {
        dataTracker.updateScreen(R.layout.activity_main)
        startGame(engine.defaultDifficulty, reInitEngine = true)

        findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
        findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
        findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
        findViewById<ToggleButton>(R.id.toggleButton).visibility = View.VISIBLE
        when(engine.currentDifficulty){
            Difficulty.Easy -> findViewById<Chip>(R.id.chip5).isChecked = true
            Difficulty.Medium -> findViewById<Chip>(R.id.chip6).isChecked = true
            Difficulty.Hard -> findViewById<Chip>(R.id.chip7).isChecked = true
        }
    }

    fun switchFirstTurn(view: View) {
        startGame(engine.currentDifficulty, !engine.computerGoesFirst)

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

    fun restartGame(view: View?) { //This creates a new game with the previous difficulty.
        stopAllSounds()
        startGame(engine.currentDifficulty, engine.computerGoesFirst, true)

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

            when(engine.currentDifficulty){
                Difficulty.Easy -> findViewById<Chip>(R.id.chip5).isChecked = true
                Difficulty.Medium -> findViewById<Chip>(R.id.chip6).isChecked = true
                Difficulty.Hard -> findViewById<Chip>(R.id.chip7).isChecked = true
            }
        }
    }

    fun setScreenAbout(view :View) = dataTracker.updateScreen(R.layout.info_screen)
    fun setScreenSettings(view :View) {
        dataTracker.updateScreen(R.layout.settings)
        findViewById<ToggleButton>(R.id.toggleButton2).isChecked = engine.soundOn
        when (engine.defaultDifficulty) {
            Difficulty.Easy -> findViewById<Chip>(R.id.chip8).isChecked = true
            Difficulty.Medium -> findViewById<Chip>(R.id.chip9).isChecked = true
            Difficulty.Hard -> findViewById<Chip>(R.id.chip10).isChecked = true
        }
    }
    fun toggleSound(view: View) {
        engine.soundOn = !engine.soundOn
    }
    fun setDefaultDifficulty(view: View) {
        when(view){
            findViewById<Chip>(R.id.chip8) -> engine.defaultDifficulty = Difficulty.Easy
            findViewById<Chip>(R.id.chip9) -> engine.defaultDifficulty = Difficulty.Medium
            findViewById<Chip>(R.id.chip10) -> engine.defaultDifficulty = Difficulty.Hard
        }
    }
}
