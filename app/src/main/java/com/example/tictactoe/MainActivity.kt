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

        fun updateScreen(newScreen: Int, restartGame: Boolean = true) {
            stopAllSounds()
            pastScreen = when(newScreen)
            {
                R.layout.activity_main -> R.layout.title_screen
                R.layout.title_screen -> R.layout.title_screen
                else -> this.currentScreen
            }
            this.currentScreen = newScreen
            setContentView(newScreen)

            if(newScreen == R.layout.activity_main && restartGame) restartGame()
        }
    }

    fun backToLastScreen(v:View? = null) = dataTracker.updateScreen(dataTracker.pastScreen)

    private fun startGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean = false, reInitEngine: Boolean = false){
        if(reInitEngine)
            engine.fullInit(arrayOf(this.findViewById(R.id.imageButton1),
                this.findViewById(R.id.imageButton2),
                this.findViewById(R.id.imageButton3),
                this.findViewById(R.id.imageButton4),
                this.findViewById(R.id.imageButton5),
                this.findViewById(R.id.imageButton6),
                this.findViewById(R.id.imageButton7),
                this.findViewById(R.id.imageButton8),
                this.findViewById(R.id.imageButton9)),
                this.findViewById(R.id.imageView),
                this.findViewById(R.id.imageView2))
        engine.startNewGame(difficulty, computerGoesFirst)

        if(engine.currentDifficulty == Difficulty.None){
            this.findViewById<Chip>(R.id.chip5).visibility = View.GONE
            this.findViewById<Chip>(R.id.chip6).visibility = View.GONE
            this.findViewById<Chip>(R.id.chip7).visibility = View.GONE
            this.findViewById<ToggleButton>(R.id.toggleButton).visibility = View.GONE
        }
        else {
            this.findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
            this.findViewById<ToggleButton>(R.id.toggleButton).visibility = View.VISIBLE
        }
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun newGame(view: View?) {
        dataTracker.updateScreen(R.layout.activity_main, false)
        val difficulty = when(view!!.id) {
            this.findViewById<Chip>(R.id.chip5).id -> Difficulty.Easy
            this.findViewById<Chip>(R.id.chip6).id -> Difficulty.Medium
            this.findViewById<Chip>(R.id.chip7).id -> Difficulty.Hard
            R.id.button2 -> Difficulty.None
            R.id.buttonNewGame -> engine.currentDifficulty
            else -> engine.defaultDifficulty
        }
        startGame(difficulty, engine.computerGoesFirst, true)

        when (engine.currentDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.chip5).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.chip6).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.chip7).isChecked = true
            Difficulty.None -> startGame(reInitEngine = true)
        }
    }

    fun restartGame() {
        startGame(engine.currentDifficulty, engine.computerGoesFirst, true)

        when (engine.currentDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.chip5).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.chip6).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.chip7).isChecked = true
            else -> return
        }
    }

    fun switchFirstTurn(view: View) = startGame(engine.currentDifficulty, !engine.computerGoesFirst)

    fun setScreenAbout(view :View) = dataTracker.updateScreen(R.layout.info_screen)
    fun setScreenSettings(view :View) {
        dataTracker.updateScreen(R.layout.settings)
        this.findViewById<ToggleButton>(R.id.toggleButton2).isChecked = engine.soundOn
        when (engine.defaultDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.chip8).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.chip9).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.chip10).isChecked = true
            else -> this.findViewById<Chip>(R.id.chip9).isChecked = true
        }
    }

    fun toggleSound(view: View) { engine.soundOn = !engine.soundOn }

    fun setDefaultDifficulty(view: View) {
        when(view){
            this.findViewById<Chip>(R.id.chip8) -> engine.defaultDifficulty = Difficulty.Easy
            this.findViewById<Chip>(R.id.chip9) -> engine.defaultDifficulty = Difficulty.Medium
            this.findViewById<Chip>(R.id.chip10) -> engine.defaultDifficulty = Difficulty.Hard
        }
    }
}
