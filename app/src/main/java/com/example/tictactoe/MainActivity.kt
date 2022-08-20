package com.example.tictactoe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.chip.Chip
import kotlin.math.abs

lateinit var engine: Engine
lateinit var settings: Settings
enum class Difficulty{None, Easy, Medium, Hard}
private var mediaPlayer: MediaPlayer? = null
var currentToast: Toast? = null
fun playSound(resource: Int, context: Context) {
    if(!settings.soundOn) return
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

        SQLiteDatabase.openOrCreateDatabase("${this.filesDir.path}/Settings.db", null, null)
        settings = Settings(this)
        engine = Engine(this)
    }

    override fun onStop() {
        super.onStop()
        currentToast?.cancel()
        stopAllSounds()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(detector.onTouchEvent(event)) true
        else super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(){

        private val swipeThreshold = 250
        private val swipeVelocityThreshold = 200

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
            currentToast?.cancel()
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

    private fun showStats() {
        this.findViewById<TextView>(R.id.textView17).text = settings.stats.winsEasy.toString()
        this.findViewById<TextView>(R.id.textView18).text = settings.stats.lossesEasy.toString()
        this.findViewById<TextView>(R.id.textView19).text = settings.stats.tiesEasy.toString()

        this.findViewById<TextView>(R.id.textView21).text = settings.stats.winsMedium.toString()
        this.findViewById<TextView>(R.id.textView22).text = settings.stats.lossesMedium.toString()
        this.findViewById<TextView>(R.id.textView23).text = settings.stats.tiesMedium.toString()

        this.findViewById<TextView>(R.id.textView25).text = settings.stats.winsHard.toString()
        this.findViewById<TextView>(R.id.textView26).text = settings.stats.lossesHard.toString()
        this.findViewById<TextView>(R.id.textView27).text = settings.stats.tiesHard.toString()
    }

    fun backToLastScreen(v:View? = null) = dataTracker.updateScreen(dataTracker.pastScreen)

    private fun startGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean = false, reInitEngine: Boolean = false) {
        currentToast?.cancel()
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
            this.findViewById<TableLayout>(R.id.tableLayout2).visibility = View.GONE
        }
        else {
            this.findViewById<Chip>(R.id.chip5).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.chip6).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.chip7).visibility = View.VISIBLE
            this.findViewById<ToggleButton>(R.id.toggleButton).visibility = View.VISIBLE
            this.findViewById<TableLayout>(R.id.tableLayout2).visibility = View.VISIBLE
            this.findViewById<ToggleButton>(R.id.toggleButton).isChecked = !computerGoesFirst

            this.findViewById<TextView>(R.id.textView28).text =
                when(engine.currentDifficulty)
                {
                    Difficulty.Easy -> "Wins: " + settings.stats.winsEasy
                    Difficulty.Medium -> "Wins: " + settings.stats.winsMedium
                    Difficulty.Hard -> "Wins: " + settings.stats.winsHard
                    else -> "Wins:"
                }
            this.findViewById<TextView>(R.id.textView29).text =
                when(engine.currentDifficulty)
                {
                    Difficulty.Easy -> "Ties: " + settings.stats.tiesEasy
                    Difficulty.Medium -> "Ties: " + settings.stats.tiesMedium
                    Difficulty.Hard -> "Ties: " + settings.stats.tiesHard
                    else -> "Ties:"
                }
            this.findViewById<TextView>(R.id.textView30).text =
                when(engine.currentDifficulty)
                {
                    Difficulty.Easy -> "Losses: " + settings.stats.lossesEasy
                    Difficulty.Medium -> "Losses: " + settings.stats.lossesMedium
                    Difficulty.Hard -> "Losses: " + settings.stats.lossesHard
                    else -> "Losses:"
                }
        }
    }

    fun newGame(view: View?) {
        dataTracker.updateScreen(R.layout.activity_main, false)
        val difficulty = when(view!!.id) {
            this.findViewById<Chip>(R.id.chip5).id -> Difficulty.Easy
            this.findViewById<Chip>(R.id.chip6).id -> Difficulty.Medium
            this.findViewById<Chip>(R.id.chip7).id -> Difficulty.Hard
            R.id.button2 -> Difficulty.None
            R.id.buttonNewGame -> engine.currentDifficulty
            else -> settings.defaultDifficulty
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

    fun cellClick(view: View) = engine.fieldClick(view)

    fun switchFirstTurn(view: View) = startGame(engine.currentDifficulty, !engine.computerGoesFirst)

    fun setScreenAbout(view :View) = dataTracker.updateScreen(R.layout.info_screen)
    fun setScreenSettings(view :View) {
        dataTracker.updateScreen(R.layout.settings)

        if(settings.personIcon == Cell.ImageType.X) this.findViewById<ImageButton>(R.id.imageButtonIcon).setImageResource(R.drawable.x)
        this.findViewById<ToggleButton>(R.id.toggleButton2).isChecked = settings.soundOn
        when (settings.defaultDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.chip8).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.chip9).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.chip10).isChecked = true
            else -> this.findViewById<Chip>(R.id.chip9).isChecked = true
        }

        this.showStats()
    }

    fun toggleSound(view: View) { settings.soundOn = !settings.soundOn }

    fun setDefaultDifficulty(view: View) {
        when(view){
            this.findViewById<Chip>(R.id.chip8) -> settings.defaultDifficulty = Difficulty.Easy
            this.findViewById<Chip>(R.id.chip9) -> settings.defaultDifficulty = Difficulty.Medium
            this.findViewById<Chip>(R.id.chip10) -> settings.defaultDifficulty = Difficulty.Hard
        }
    }

    fun swapIcons(view: View) {
        val res = engine.swapIcons()
        if(!res){
            this.findViewById<ImageButton>(R.id.imageButtonIcon).setImageResource(R.drawable.x)
            return
        }
        else if(res) this.findViewById<ImageButton>(R.id.imageButtonIcon).setImageResource(R.drawable.o)
    }

    fun resetStats(view: View) {
        settings.stats.updateStats(buttonResetView = view)
        this.showStats()
    }
}
