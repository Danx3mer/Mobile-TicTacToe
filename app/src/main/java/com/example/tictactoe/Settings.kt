package com.example.tictactoe

import android.view.View

class Settings {
    var soundOn: Boolean = false
    var personIcon = Cell.ImageType.O
    var computerIcon = Cell.ImageType.X
    var defaultDifficulty: Difficulty = Difficulty.Medium
    inner class Statistics{
        var winsHard = 0
            private set
        var winsMedium = 0
            private set
        var winsEasy = 0
            private set
        var tiesHard = 0
            private set
        var tiesMedium = 0
            private set
        var tiesEasy = 0
            private set
        var lossesHard = 0
            private set
        var lossesMedium = 0
            private set
        var lossesEasy = 0
            private set
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
                        else -> return
                    }
                }
                Engine.GameOverCode.Tie -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.tiesEasy++
                        Difficulty.Medium -> this.tiesMedium++
                        Difficulty.Hard -> this.tiesHard++
                        else -> return
                    }
                }
                Engine.GameOverCode.Loss -> {
                    when(difficulty!!){
                        Difficulty.Easy -> this.lossesEasy++
                        Difficulty.Medium -> this.lossesMedium++
                        Difficulty.Hard -> this.lossesHard++
                        else -> return
                    }
                }
            }
        }
    }
    val stats = Statistics()

    fun loadSettings(){

    }
    fun writeNewSettings(){

    }
}