package kt.game.tictactoe

import android.view.View
import androidx.appcompat.app.AppCompatDelegate

class Settings(contextOfMainActivity: MainActivity) {
    var soundOn: Boolean = false
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "WINS", when(value){ false -> 0; true -> 1 })}
    var personIcon = Cell.ImageType.X
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "LOSSES", when(value){ Cell.ImageType.O -> 0; else -> 1 })}
    var computerIcon = Cell.ImageType.O
    var pveIcon = personIcon
    var defaultDifficulty: Difficulty = Difficulty.Medium
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "TIES", when(value){ Difficulty.Easy -> 0; Difficulty.Medium -> 1; Difficulty.Hard -> 2; else -> -1 })}
    private val dbManager = DataBaseManager(contextOfMainActivity)

    var autoSwitch = true
    var currentTheme: Boolean = false
    private set

    fun changeTheme(theme: Boolean = !currentTheme) {
        this.writeThemeSettings()
        currentTheme = theme
    }

    inner class Statistics{
        var winsHard = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("HARD", "WINS", value) }
        var winsMedium = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("MEDIUM", "WINS", value) }
        var winsEasy = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("EASY", "WINS", value) }
        var tiesHard = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("HARD", "TIES", value) }
        var tiesMedium = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("MEDIUM", "TIES", value) }
        var tiesEasy = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("EASY", "TIES", value) }
        var lossesHard = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("HARD", "LOSSES", value) }
        var lossesMedium = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("MEDIUM", "LOSSES", value) }
        var lossesEasy = 0
            set(value) { field = value; this@Settings.dbManager.overwriteValue("EASY", "LOSSES", value) }

        var oWins = 0
        var xWins = 0
        var pvpTies = 0


        fun updateStats(gameOverCode: Engine.GameOverCode? = null, difficulty: Difficulty? = null, buttonResetView: View? = null) {
            if(buttonResetView != null) {
                when(buttonResetView.id) {
                    R.id.btn_resetall -> {
                        this.winsHard = 0
                        this.winsMedium = 0
                        this.winsEasy = 0
                        this.tiesHard = 0
                        this.tiesMedium = 0
                        this.tiesEasy = 0
                        this.lossesHard = 0
                        this.lossesMedium = 0
                        this.lossesEasy = 0
                    }
                    R.id.btn_reset_easy -> {
                        this.winsEasy = 0
                        this.tiesEasy = 0
                        this.lossesEasy = 0
                    }
                    R.id.btn_reset_medium -> {
                        this.winsMedium = 0
                        this.tiesMedium = 0
                        this.lossesMedium = 0
                    }
                    R.id.btn_reset_hard -> {
                        this.winsHard = 0
                        this.tiesHard = 0
                        this.lossesHard = 0
                    }
                }
                return
            }
            when(gameOverCode!!){
                Engine.GameOverCode.Win -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.winsEasy++
                        Difficulty.Medium -> this.winsMedium++
                        Difficulty.Hard -> this.winsHard++
                        Difficulty.None ->
                            if(this@Settings.personIcon == Cell.ImageType.O) this.oWins++
                            else this.xWins++
                    }
                }
                Engine.GameOverCode.Tie -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.tiesEasy++
                        Difficulty.Medium -> this.tiesMedium++
                        Difficulty.Hard -> this.tiesHard++
                        Difficulty.None -> this.pvpTies++
                    }
                }
                Engine.GameOverCode.Loss -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.lossesEasy++
                        Difficulty.Medium -> this.lossesMedium++
                        Difficulty.Hard -> this.lossesHard++
                        Difficulty.None ->
                            if(this@Settings.personIcon == Cell.ImageType.O) this.xWins++
                            else this.oWins++
                    }
                }
            }
        }
    }
    val stats = Statistics()

    private fun loadSettings() {
        try{
            val easyStats = dbManager.getSettings("EASY")
            val mediumStats = dbManager.getSettings("MEDIUM")
            val hardStats = dbManager.getSettings("HARD")
            val loadedSettings = dbManager.getSettings("SETTINGS")
            val loadedThemeSettings = dbManager.getSettings("THEME_SETTINGS")

            this.stats.winsEasy = easyStats[0]
            this.stats.lossesEasy = easyStats[1]
            this.stats.tiesEasy = easyStats[2]

            this.stats.winsMedium = mediumStats[0]
            this.stats.lossesMedium = mediumStats[1]
            this.stats.tiesMedium = mediumStats[2]

            this.stats.winsHard = hardStats[0]
            this.stats.lossesHard = hardStats[1]
            this.stats.tiesHard = hardStats[2]

            this.soundOn = when(loadedSettings[0]) {
                0 -> false
                else -> true
            }

            this.personIcon = when(loadedSettings[1]) {
                0 -> Cell.ImageType.O
                else -> Cell.ImageType.X
            }
            pveIcon = personIcon

            this.computerIcon = when(loadedSettings[1]) {
                0 -> Cell.ImageType.X
                else -> Cell.ImageType.O
            }

            this.defaultDifficulty = when(loadedSettings[2]) {
                0 -> Difficulty.Easy
                1 -> Difficulty.Medium
                2 -> Difficulty.Hard
                else -> Difficulty.None
            }

            this.currentTheme = when(loadedThemeSettings[0]) {
                AppCompatDelegate.MODE_NIGHT_NO -> false
                else -> true
            }
            dataTracker.setScreens(loadedThemeSettings[1], loadedThemeSettings[2])
            this.dbManager.overwriteValue("THEME_SETTINGS", "LOSSES", R.layout.title_screen)
            this.dbManager.overwriteValue("THEME_SETTINGS", "TIES", R.layout.title_screen)
        } catch(e: Exception) { this.writeNewSettings(); this.loadSettings() }
    }

    private fun writeNewSettings(){
        dbManager.writeSettings("EASY", listOf(this.stats.winsEasy, this.stats.lossesEasy, this.stats.tiesEasy))
        dbManager.writeSettings("MEDIUM", listOf(this.stats.winsMedium, this.stats.lossesMedium, this.stats.tiesMedium))
        dbManager.writeSettings("HARD", listOf(this.stats.winsHard, this.stats.lossesHard, this.stats.tiesHard))
        dbManager.writeSettings("SETTINGS", listOf(when(this.soundOn){ false -> 0; true -> 1},
            when(this.personIcon){ Cell.ImageType.O -> 0; else -> 1 },
            when(this.defaultDifficulty){ Difficulty.Easy -> 0; Difficulty.Medium -> 1; Difficulty.Hard -> 2; else -> -1}))
        dbManager.writeSettings("THEME_SETTINGS",
            listOf(when(AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> 0
                else -> 1
                                                                 },
            dataTracker.currentScreen, dataTracker.pastScreen))
    }

    private fun writeThemeSettings() {
        this.dbManager.overwriteValue("THEME_SETTINGS", "WINS", when(this.currentTheme){ false -> 0; true -> 1 })
        this.dbManager.overwriteValue("THEME_SETTINGS", "LOSSES", dataTracker.currentScreen)
        this.dbManager.overwriteValue("THEME_SETTINGS", "TIES",
            if(engine.currentDifficulty == Difficulty.None) pvpActivityMain
            else dataTracker.pastScreen)
    }

    init { this.loadSettings() }
}