package kt.game.tictactoe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.chip.Chip
import kotlin.math.abs

lateinit var engine: Engine
lateinit var settings: Settings
enum class Difficulty{None, Easy, Medium, Hard}
private var mediaPlayer: MediaPlayer? = null
var currentToast: Toast? = null
lateinit var dataTracker: MainActivity.DataTracker
const val pvpActivityMain = 1

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SQLiteDatabase.openOrCreateDatabase("${this.filesDir.path}/Settings.db", null, null)
        dataTracker = DataTracker(R.layout.title_screen)
        settings = Settings(this)

        AppCompatDelegate.setDefaultNightMode(when (settings.currentTheme) {
            false -> AppCompatDelegate.MODE_NIGHT_NO
            true -> AppCompatDelegate.MODE_NIGHT_YES
        })
        if(dataTracker.currentScreen == R.layout.settings) {
            engine = Engine(this)
            this.setScreenSettings(null)
        }
        else {
            engine = Engine(this)
            setContentView(R.layout.title_screen)
            dataTracker = DataTracker(R.layout.title_screen)
        }
        detector = GestureDetectorCompat(this,GestureListener())
    }

    override fun onStop() {
        super.onStop()
        engine.endCoroutine()
        currentToast?.cancel()
        stopAllSounds()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(detector.onTouchEvent(event!!)) true
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
                        dataTracker.updateScreen(dataTracker.pastScreen)

            return super.onFling(pointerDown, moveEvent, velocityX, velocityY)
        }
    }

    inner class DataTracker(initialScreen: Int) {
        var currentScreen: Int = initialScreen
        private set

        var pastScreen: Int = initialScreen
        private set

        fun setScreens(currentScreen: Int, pastScreen: Int) {
            this.currentScreen = currentScreen
            this.pastScreen = pastScreen
        }

        fun updateScreen(newScreen: Int, restartGame: Boolean = true, replaceLastScreen: Boolean = true) {
            engine.endCoroutine()
            currentToast?.cancel()
            stopAllSounds()

            if(currentScreen == R.layout.activity_main && newScreen != currentScreen && engine.currentDifficulty == Difficulty.None){
                if(newScreen != R.layout.settings) {
                    settings.stats.oWins = 0
                    settings.stats.xWins = 0
                    settings.stats.pvpTies = 0
                }
                settings.personIcon = settings.pveIcon
            }

            if(replaceLastScreen) pastScreen = when(newScreen) {
                R.layout.activity_main -> R.layout.title_screen
                pvpActivityMain -> R.layout.title_screen
                R.layout.title_screen -> R.layout.title_screen
                else -> this.currentScreen
            }
            if(newScreen != pvpActivityMain)
                this.currentScreen = newScreen
            else this.currentScreen = R.layout.activity_main

            setContentView(this.currentScreen)

            if(newScreen == pvpActivityMain) engine.currentDifficulty = Difficulty.None
            if((newScreen == R.layout.activity_main && restartGame) || newScreen == pvpActivityMain) restartGame()
        }
    }

    private fun showStats() {
        this.findViewById<TextView>(R.id.tv_wins_easy).text = settings.stats.winsEasy.toString()
        this.findViewById<TextView>(R.id.tv_losses_easy).text = settings.stats.lossesEasy.toString()
        this.findViewById<TextView>(R.id.tv_ties_easy).text = settings.stats.tiesEasy.toString()

        this.findViewById<TextView>(R.id.tv_wins_medium).text = settings.stats.winsMedium.toString()
        this.findViewById<TextView>(R.id.tv_losses_medium).text = settings.stats.lossesMedium.toString()
        this.findViewById<TextView>(R.id.tv_ties_medium).text = settings.stats.tiesMedium.toString()

        this.findViewById<TextView>(R.id.textView25).text = settings.stats.winsHard.toString()
        this.findViewById<TextView>(R.id.tv_losses_hard).text = settings.stats.lossesHard.toString()
        this.findViewById<TextView>(R.id.tv_ties_hard).text = settings.stats.tiesHard.toString()
    }

    fun backToLastScreen(view: View) = dataTracker.updateScreen(dataTracker.pastScreen)

    private fun startGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean = false, reInitEngine: Boolean = false) {
        currentToast?.cancel()
        if(reInitEngine)
            engine.fullInit(arrayOf(this.findViewById(R.id.ib_cell1),
                this.findViewById(R.id.ib_cell2),
                this.findViewById(R.id.ib_cell3),
                this.findViewById(R.id.ib_cell4),
                this.findViewById(R.id.ib_cell5),
                this.findViewById(R.id.ib_cell6),
                this.findViewById(R.id.ib_cell7),
                this.findViewById(R.id.ib_cell8),
                this.findViewById(R.id.ib_cell9)),
                this.findViewById(R.id.iv_turn),
                this.findViewById(R.id.iv_linedrawer))
        engine.startNewGame(difficulty, computerGoesFirst)

        if(engine.currentDifficulty == Difficulty.None) {
            this.findViewById<Chip>(R.id.cp_diff_easy).visibility = View.GONE
            this.findViewById<Chip>(R.id.cp_diff_medium).visibility = View.GONE
            this.findViewById<Chip>(R.id.cp_diff_hard).visibility = View.GONE
            this.findViewById<ToggleButton>(R.id.tb_firstturn).visibility = View.GONE

            this.findViewById<Chip>(R.id.cp_autoswitch).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.cp_manualswitch).visibility = View.VISIBLE

            this.findViewById<Button>(R.id.btn_switch_turn).visibility = View.VISIBLE

            this.findViewById<TextView>(R.id.tv_ministats_ties).text = this.getString(R.string.Ties_colon) + " " + settings.stats.pvpTies
            this.findViewById<TextView>(R.id.tv_ministats_wins).text = this.getString(R.string.OWins) + " " + settings.stats.oWins
            this.findViewById<TextView>(R.id.tv_ministats_losses).text = this.getString(R.string.XWins) + " " + settings.stats.xWins

            if(!settings.autoSwitch) this.findViewById<Chip>(R.id.cp_manualswitch).isChecked = true
        }
        else {
            this.findViewById<Chip>(R.id.cp_diff_easy).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.cp_diff_medium).visibility = View.VISIBLE
            this.findViewById<Chip>(R.id.cp_diff_hard).visibility = View.VISIBLE
            this.findViewById<ToggleButton>(R.id.tb_firstturn).visibility = View.VISIBLE

            this.findViewById<Chip>(R.id.cp_autoswitch).visibility = View.GONE
            this.findViewById<Chip>(R.id.cp_manualswitch).visibility = View.GONE

            this.findViewById<Button>(R.id.btn_switch_turn).visibility = View.GONE

            this.findViewById<ToggleButton>(R.id.tb_firstturn).isChecked = !computerGoesFirst

            this.findViewById<TextView>(R.id.tv_ministats_wins).text = when(engine.currentDifficulty) {
                Difficulty.Easy -> getString(R.string.Wins_colon) + " " + settings.stats.winsEasy
                Difficulty.Medium -> getString(R.string.Wins_colon) + " " + settings.stats.winsMedium
                Difficulty.Hard -> getString(R.string.Wins_colon) + " " + settings.stats.winsHard
                else -> getString(R.string.Wins_colon)
            }

            this.findViewById<TextView>(R.id.tv_ministats_ties).text = when(engine.currentDifficulty)
            {
                Difficulty.Easy -> getString(R.string.Ties_colon) + " " + settings.stats.tiesEasy
                Difficulty.Medium -> getString(R.string.Ties_colon) + " " + settings.stats.tiesMedium
                Difficulty.Hard -> getString(R.string.Ties_colon) + " " + settings.stats.tiesHard
                else -> getString(R.string.Ties_colon)
            }

            this.findViewById<TextView>(R.id.tv_ministats_losses).text = when(engine.currentDifficulty)
            {
                Difficulty.Easy -> getString(R.string.Losses_colon) + " " + settings.stats.lossesEasy
                Difficulty.Medium -> getString(R.string.Losses_colon) + " " + settings.stats.lossesMedium
                Difficulty.Hard -> getString(R.string.Losses_colon) + " " + settings.stats.lossesHard
                else -> getString(R.string.Losses_colon)
            }
        }
    }

    fun newGame(view: View?) {
        dataTracker.updateScreen(R.layout.activity_main, false)
        val difficulty = when(view!!.id) {
            this.findViewById<Chip>(R.id.cp_diff_easy).id -> Difficulty.Easy
            this.findViewById<Chip>(R.id.cp_diff_medium).id -> Difficulty.Medium
            this.findViewById<Chip>(R.id.cp_diff_hard).id -> Difficulty.Hard
            R.id.btn_newPVP -> Difficulty.None
            R.id.btn_newgame -> engine.currentDifficulty
            else -> settings.defaultDifficulty
        }
        startGame(difficulty, engine.computerGoesFirst, true)

        when (engine.currentDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.cp_diff_easy).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.cp_diff_medium).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.cp_diff_hard).isChecked = true
            Difficulty.None -> startGame(reInitEngine = true)
        }
    }

    fun restartGame() {
        startGame(engine.currentDifficulty, engine.computerGoesFirst, true)

        when (engine.currentDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.cp_diff_easy).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.cp_diff_medium).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.cp_diff_hard).isChecked = true
            else -> return
        }
    }

    fun cellClick(view: View) = engine.fieldClick(view)

    fun switchFirstTurn(view: View) =
        startGame(engine.currentDifficulty, !engine.computerGoesFirst)

    fun setScreenAbout(view :View) = dataTracker.updateScreen(R.layout.info_screen)
    fun setScreenSettings(view :View?) {
        if(view == null) dataTracker.updateScreen(R.layout.settings, replaceLastScreen = false)
        else dataTracker.updateScreen(R.layout.settings)

        if(settings.personIcon == Cell.ImageType.X) this.findViewById<ImageButton>(R.id.ib_usricon).setImageResource(
            R.drawable.x)
        this.findViewById<ToggleButton>(R.id.tb_sound).isChecked = settings.soundOn
        when (settings.defaultDifficulty) {
            Difficulty.Easy -> this.findViewById<Chip>(R.id.cp_settings_easy).isChecked = true
            Difficulty.Medium -> this.findViewById<Chip>(R.id.cp_settings_medium).isChecked = true
            Difficulty.Hard -> this.findViewById<Chip>(R.id.cp_settings_hard).isChecked = true
            else -> this.findViewById<Chip>(R.id.cp_settings_medium).isChecked = true
        }

        this.showStats()
        this.findViewById<ToggleButton>(R.id.tb_theme).isChecked = settings.currentTheme
    }

    fun toggleSound(view: View) { settings.soundOn = !settings.soundOn }

    fun toggleTheme(view: View) {
        settings.changeTheme()
        if(findViewById<ToggleButton>(R.id.tb_theme).isChecked)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else if(!findViewById<ToggleButton>(R.id.tb_theme).isChecked)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun setDefaultDifficulty(view: View) {
        when(view){
            this.findViewById<Chip>(R.id.cp_settings_easy) -> settings.defaultDifficulty = Difficulty.Easy
            this.findViewById<Chip>(R.id.cp_settings_medium) -> settings.defaultDifficulty = Difficulty.Medium
            this.findViewById<Chip>(R.id.cp_settings_hard) -> settings.defaultDifficulty = Difficulty.Hard
        }
    }

    fun swapIcons(view: View) {
        val res = engine.swapIcons()
        if(!res){
            this.findViewById<ImageButton>(R.id.ib_usricon).setImageResource(R.drawable.x)
            return
        }
        else if(res) this.findViewById<ImageButton>(R.id.ib_usricon).setImageResource(R.drawable.o)
    }

    fun swapPvpIcons(view: View) {
        val res = engine.swapIcons()
        if(!res) this.findViewById<ImageView>(R.id.iv_turn).setImageResource(R.drawable.x)
        else if(res) this.findViewById<ImageView>(R.id.iv_turn).setImageResource(R.drawable.o)

        this.restartGame()
    }

    fun resetStats(view: View) {
        settings.stats.updateStats(buttonResetView = view)
        this.showStats()
    }

    fun toggleAutoSwitch(view: View) {
        when(view.id) {
            R.id.cp_autoswitch -> settings.autoSwitch = true
            R.id.cp_manualswitch -> settings.autoSwitch = false
        }
    }
}
