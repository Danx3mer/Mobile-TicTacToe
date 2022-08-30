package kt.game.tictactoe

import android.view.View

class Settings(contextOfMainActivity: MainActivity) {
    var soundOn: Boolean = false
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "WINS", when(value){ false -> 0; true -> 1 })}
    var personIcon = Cell.ImageType.O
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "LOSSES", when(value){ Cell.ImageType.O -> 0; else -> 1 })}
    var computerIcon = Cell.ImageType.X
    var defaultDifficulty: Difficulty = Difficulty.Medium
        set(value) { field = value; this@Settings.dbManager.overwriteValue("SETTINGS", "TIES", when(value){ Difficulty.Easy -> 0; Difficulty.Medium -> 1; Difficulty.Hard -> 2; else -> -1 })}
    private val dbManager = DataBaseManager(contextOfMainActivity)

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

        fun updateStats(gameOverCode: Engine.GameOverCode? = null, difficulty: Difficulty? = null, buttonResetView: View? = null) {
            if(buttonResetView != null) {
                when(buttonResetView.id) {
                    R.id.buttonReset1 -> {
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
                    R.id.buttonReset2 -> {
                        this.winsEasy = 0
                        this.tiesEasy = 0
                        this.lossesEasy = 0
                    }
                    R.id.buttonReset3 -> {
                        this.winsMedium = 0
                        this.tiesMedium = 0
                        this.lossesMedium = 0
                    }
                    R.id.buttonReset4 -> {
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
                    }
                }
                Engine.GameOverCode.Tie -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.tiesEasy++
                        Difficulty.Medium -> this.tiesMedium++
                        Difficulty.Hard -> this.tiesHard++
                    }
                }
                Engine.GameOverCode.Loss -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.lossesEasy++
                        Difficulty.Medium -> this.lossesMedium++
                        Difficulty.Hard -> this.lossesHard++
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
        } catch(e: Exception) {this.writeNewSettings()}
    }

    private fun writeNewSettings(){
        dbManager.writeSettings("EASY", listOf(this.stats.winsEasy, this.stats.lossesEasy, this.stats.tiesEasy))
        dbManager.writeSettings("MEDIUM", listOf(this.stats.winsMedium, this.stats.lossesMedium, this.stats.tiesMedium))
        dbManager.writeSettings("HARD", listOf(this.stats.winsHard, this.stats.lossesHard, this.stats.tiesHard))
        dbManager.writeSettings("SETTINGS", listOf(when(this.soundOn){ false -> 0; true -> 1},
            when(this.personIcon){ Cell.ImageType.O -> 0; else -> 1 },
            when(this.defaultDifficulty){ Difficulty.Easy -> 0; Difficulty.Medium -> 1; Difficulty.Hard -> 2; else -> -1}))
    }

    init { this.loadSettings() }
}