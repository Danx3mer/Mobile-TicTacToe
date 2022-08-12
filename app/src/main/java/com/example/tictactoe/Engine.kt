package com.example.tictactoe

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

class Engine(private val contextOfMainActivity: Context) {
    enum class GameOverCode { Win, Loss, Tie }
    inner class Settings{
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
            fun updateStats(gameOverCode: GameOverCode? = null, difficulty: Difficulty? = null, buttonResetView: View? = null) {
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
                    GameOverCode.Win -> {
                        when(difficulty!!){
                            Difficulty.Easy -> this.winsEasy++
                            Difficulty.Medium -> this.winsMedium++
                            Difficulty.Hard -> this.winsHard++
                            else -> return
                        }
                    }
                    GameOverCode.Tie -> {
                        when(difficulty!!){
                            Difficulty.Easy -> this.tiesEasy++
                            Difficulty.Medium -> this.tiesMedium++
                            Difficulty.Hard -> this.tiesHard++
                            else -> return
                        }
                    }
                    GameOverCode.Loss -> {
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
    val settings = Settings()

    private lateinit var imageView: ImageView
    private lateinit var imageViewLineDrawer: ImageView

    private val computer: Computer = Computer(Difficulty.Medium)
    private lateinit var cells: Array<Cell>
    var computerGoesFirst: Boolean = false
    private set

    var currentDifficulty: Difficulty = this.settings.defaultDifficulty

    fun fullInit(imageButtons: Array<ImageButton>, imageView: ImageView, imageViewLineDrawer: ImageView){
        cells = arrayOf(Cell(imageButtons[0]),
            Cell(imageButtons[1]),
            Cell(imageButtons[2]),
            Cell(imageButtons[3]),
            Cell(imageButtons[4]),
            Cell(imageButtons[5]),
            Cell(imageButtons[6]),
            Cell(imageButtons[7]),
            Cell(imageButtons[8]))
        this.imageView = imageView
        this.imageViewLineDrawer = imageViewLineDrawer
        this.startNewGame(computerGoesFirst = false)
    }

    var numOfMoves = 0
    private set

    enum class WinningLinePos{VLeft,VMiddle,VRight,HTop,HMiddle,HBottom,D1,D2,Fail}

    enum class CurrentTurnType{X,O}
    var currentTurn:CurrentTurnType = CurrentTurnType.O

    private var isGameOver = false

    fun swapIcons(): Boolean {
        val tempIcon = this.settings.personIcon
        this.settings.personIcon = this.settings.computerIcon
        this.settings.computerIcon = tempIcon
        return when(this.settings.personIcon){
            Cell.ImageType.O -> true
            Cell.ImageType.X -> false
            else -> false
        }
    }

    private fun firstMove(){
        if(this.computerGoesFirst)
        {
            this.switchTurns() //To make sure that the computer is X
            //Computer goes here
            val computerPick = computer.pickCell(this.cells)
            if(computerPick==-1) return
            this.cells[computerPick].cellClick()
            ++this.numOfMoves
            this.switchTurns()
        }

    }

    fun fieldClick(view: View){
        if(this.isGameOver) return

        //Player goes here
        for(i in 0..8) {
            if(this.cells[i].boundImageButton.id == view.id) {
                if(this.cells[i].cellClick()) {
                    playSound(R.raw.click, contextOfMainActivity)
                    if(++this.numOfMoves >= 5) {
                        val winCheckRes: WinningLinePos = this.winCheck()
                        if (winCheckRes != WinningLinePos.Fail) {
                            gameOver(
                                when(this.currentTurn) {
                                    CurrentTurnType.O -> {
                                        if(this.settings.personIcon == Cell.ImageType.O) GameOverCode.Win
                                        else GameOverCode.Loss
                                    }
                                    CurrentTurnType.X -> {
                                        if(this.settings.personIcon == Cell.ImageType.O) GameOverCode.Loss
                                        else GameOverCode.Win
                                    }
                                }, winCheckRes)
                            playSound(R.raw.win, contextOfMainActivity)
                            return
                        }
                        else if (this.numOfMoves == 9) {
                            gameOver(GameOverCode.Tie, null)
                            return //So that the computer doesn't go because it can't pick out any available cells.
                        }
                    }
                    this.switchTurns()
                }
                else return //So that the computer doesn't go in the case that you clicked on a cell that already was clicked on.
            }
        }

        //Computer goes here
        val computerPick = computer.pickCell(this.cells)
        if (computerPick == -1) return
        this.cells[computerPick].cellClick()
        playSound(R.raw.click, contextOfMainActivity)
        if (++this.numOfMoves >= 5) {
            val winCheckRes: WinningLinePos = this.winCheck()
            if (winCheckRes != WinningLinePos.Fail) {
                gameOver(GameOverCode.Loss, winCheckRes)
                playSound(R.raw.lose, contextOfMainActivity)
            }
            else if (this.numOfMoves == 9) {
                gameOver(GameOverCode.Tie, winCheckRes)
            }
        }
        this.switchTurns()
    }

    private fun switchTurns(){
        when(this.currentTurn){
            CurrentTurnType.O -> {
                this.currentTurn = CurrentTurnType.X
                this.imageView.setImageResource(R.drawable.x)
            }
            CurrentTurnType.X -> {
                this.currentTurn = CurrentTurnType.O
                this.imageView.setImageResource(R.drawable.o)
            }
        }
    }

    fun startNewGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean){
        for(i in 0..8) this.cells[i].reset()
        this.isGameOver = false
        this.numOfMoves = 0

        this.imageView.setImageResource(when(this.settings.personIcon){
            Cell.ImageType.O -> R.drawable.o
            Cell.ImageType.X -> R.drawable.x
            else -> R.drawable.o
        })

        this.currentTurn = when(this.settings.personIcon) {
            Cell.ImageType.O -> CurrentTurnType.O
            Cell.ImageType.X -> CurrentTurnType.X
            else -> CurrentTurnType.O
        }

        this.computerGoesFirst = computerGoesFirst
        this.currentDifficulty = difficulty
        computer.reset(difficulty) //resets the computer

        this.imageViewLineDrawer.setImageBitmap(Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)) //Clear the line drawing field
        if(this.computerGoesFirst) this.firstMove()
    }

    private fun winCheck() :WinningLinePos {
        var cellMatchCounter = 0
        val checkImage: Cell.ImageType = when(this.currentTurn){
            CurrentTurnType.O -> Cell.ImageType.O
            CurrentTurnType.X -> Cell.ImageType.X
        }

        //Checking horizontals
        for(i in 0..2) {
            for (cellCounter in i * 3..i * 3 + 2) {
                if (this.cells[cellCounter].image == checkImage)
                    if (++cellMatchCounter == 3) return when(i){
                        0 -> WinningLinePos.HTop
                        1 -> WinningLinePos.HMiddle
                        2 -> WinningLinePos.HBottom
                        else -> WinningLinePos.Fail
                    }
            }
            cellMatchCounter = 0
        }

        //Checking verticals
        for(i in 0..2) {
            for (cellCounter in arrayOf(i,3+i,6+i)) {
                if (this.cells[cellCounter].image == checkImage)
                    if (++cellMatchCounter == 3) return when(i){
                        0 -> WinningLinePos.VLeft
                        1 -> WinningLinePos.VMiddle
                        2 -> WinningLinePos.VRight
                        else -> WinningLinePos.Fail
                    }
            }
            cellMatchCounter = 0
        }

        //Checking diagonals #1
        for (cellCounter in arrayOf(0,4,8)) {
            if (this.cells[cellCounter].image == checkImage)
                if (++cellMatchCounter == 3) return WinningLinePos.D1
        }
        cellMatchCounter = 0

        //Checking diagonals #2
        for (cellCounter in arrayOf(2,4,6)) {
            if (this.cells[cellCounter].image == checkImage)
                if (++cellMatchCounter == 3) return WinningLinePos.D2
        }

        return WinningLinePos.Fail
    }

    private fun gameOver(gameOverCode: GameOverCode, winningLinePos: WinningLinePos?){
        this.isGameOver = true
        if(winningLinePos != null) this.drawWinningLine(winningLinePos)
        this.settings.stats.updateStats(gameOverCode, this.currentDifficulty)

        when(this.currentDifficulty){
            Difficulty.Hard -> {
                when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.Medium -> {
                when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.Easy -> {
                when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.None -> {
                when(gameOverCode){
                    GameOverCode.Win -> {
                        if(this.settings.personIcon == Cell.ImageType.O) Toast(this.contextOfMainActivity).showCustomToast("O won!!!", this.contextOfMainActivity as Activity)
                        else Toast(this.contextOfMainActivity).showCustomToast("X won!!!", this.contextOfMainActivity as Activity)
                    }
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity as Activity)

                    GameOverCode.Loss -> {
                        if(this.settings.personIcon == Cell.ImageType.O) Toast(this.contextOfMainActivity).showCustomToast("X won!!!", this.contextOfMainActivity as Activity)
                        else Toast(this.contextOfMainActivity).showCustomToast("O won!!!", this.contextOfMainActivity as Activity)
                    }
                }
            }
        }
    }

    private fun drawWinningLine(winningLinePos: WinningLinePos){
        val startX: Float = when(winningLinePos){
            WinningLinePos.VLeft -> 50F
            WinningLinePos.VMiddle -> 150F
            WinningLinePos.VRight -> 250F
            WinningLinePos.D2 -> 300F
            else -> 300F
        }
        val startY: Float = when(winningLinePos){
            WinningLinePos.HTop -> 50F
            WinningLinePos.HMiddle -> 150F
            WinningLinePos.HBottom -> 250F
            WinningLinePos.D1 -> 300F
            else -> 0F
        }
        val stopX: Float = when(winningLinePos){
            WinningLinePos.VLeft -> startX
            WinningLinePos.VMiddle -> startX
            WinningLinePos.VRight -> startX
            else -> 0F
        }
        val stopY: Float = when(winningLinePos){
            WinningLinePos.HBottom -> startY
            WinningLinePos.HMiddle -> startY
            WinningLinePos.HTop -> startY
            WinningLinePos.D1 -> 0F
            else -> 300F
        }

        val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8F
        paint.isAntiAlias = true
        canvas.drawLine(startX, startY, stopX, stopY, paint)
        imageViewLineDrawer.setImageBitmap(bitmap)
    }

}

