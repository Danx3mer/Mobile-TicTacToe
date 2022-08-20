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
import android.widget.TextView
import android.widget.Toast

class Engine(private val contextOfMainActivity: Context) {
    enum class GameOverCode { Win, Loss, Tie }

    private lateinit var imageView: ImageView
    private lateinit var imageViewLineDrawer: ImageView

    private val computer: Computer = Computer(Difficulty.Medium)
    private lateinit var cells: Array<Cell>
    var computerGoesFirst: Boolean = false
    private set

    var currentDifficulty: Difficulty = settings.defaultDifficulty

    private var threadRunning: Boolean = false
    private var threadEnd: Boolean = false
    private var thread: Thread? = null
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
        val tempIcon = settings.personIcon
        settings.personIcon = settings.computerIcon
        settings.computerIcon = tempIcon
        return when(settings.personIcon){
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
            playSound(R.raw.click, contextOfMainActivity)
            ++this.numOfMoves
            this.switchTurns()
        }

    }

    fun fieldClick(view: View){
        if(this.threadRunning) return
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
                                        if(settings.personIcon == Cell.ImageType.O) GameOverCode.Win
                                        else GameOverCode.Loss
                                    }
                                    CurrentTurnType.X -> {
                                        if(settings.personIcon == Cell.ImageType.O) GameOverCode.Loss
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

        if(this.currentDifficulty == Difficulty.None) return
        this.thread = Thread {
            this.threadEnd = false
            this.threadRunning = true
            Thread.sleep(500)
            if(this.threadEnd) {this.threadRunning = false; return@Thread}
            val computerPick = computer.pickCell(this.cells)
            if (computerPick == -1){ threadRunning = false; return@Thread }
            if(this.threadEnd) {this.threadRunning = false; return@Thread}
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
            this.threadRunning = false
            return@Thread
        }
        this.thread!!.start()
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
        this.threadEnd = true

        for(i in 0..8) this.cells[i].reset()
        this.isGameOver = false
        this.numOfMoves = 0

        this.imageView.setImageResource(when(settings.personIcon){
            Cell.ImageType.O -> R.drawable.o
            Cell.ImageType.X -> R.drawable.x
            else -> R.drawable.o
        })

        this.currentTurn = when(settings.personIcon) {
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
        settings.stats.updateStats(gameOverCode, this.currentDifficulty)
        (this.contextOfMainActivity as Activity).runOnUiThread{
        when(this.currentDifficulty){
            Difficulty.Hard -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity)
                }
            }
            Difficulty.Medium -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity)
                }
            }
            Difficulty.Easy -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast("You won!!!", this.contextOfMainActivity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast("Bot won!!!", this.contextOfMainActivity)
                }
            }
            Difficulty.None -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> {
                        if(settings.personIcon == Cell.ImageType.O) Toast(this.contextOfMainActivity).showCustomToast("O won!!!", this.contextOfMainActivity)
                        else Toast(this.contextOfMainActivity).showCustomToast("X won!!!", this.contextOfMainActivity)
                    }
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast("It's a tie!!!", this.contextOfMainActivity)

                    GameOverCode.Loss -> {
                        if(settings.personIcon == Cell.ImageType.O) Toast(this.contextOfMainActivity).showCustomToast("X won!!!", this.contextOfMainActivity)
                        else Toast(this.contextOfMainActivity).showCustomToast("O won!!!", this.contextOfMainActivity)
                    }
                }
            }
        }}

        this.contextOfMainActivity.findViewById<TextView>(R.id.textView28).text =
            when(engine.currentDifficulty)
            {
                Difficulty.Easy -> "Wins: " + settings.stats.winsEasy
                Difficulty.Medium -> "Wins: " + settings.stats.winsMedium
                Difficulty.Hard -> "Wins: " + settings.stats.winsHard
                else -> "Wins:"
            }
        this.contextOfMainActivity.findViewById<TextView>(R.id.textView29).text =
            when(engine.currentDifficulty)
            {
                Difficulty.Easy -> "Ties: " + settings.stats.tiesEasy
                Difficulty.Medium -> "Ties: " + settings.stats.tiesMedium
                Difficulty.Hard -> "Ties: " + settings.stats.tiesHard
                else -> "Ties:"
            }
        this.contextOfMainActivity.findViewById<TextView>(R.id.textView30).text =
            when(engine.currentDifficulty)
            {
                Difficulty.Easy -> "Losses: " + settings.stats.lossesEasy
                Difficulty.Medium -> "Losses: " + settings.stats.lossesMedium
                Difficulty.Hard -> "Losses: " + settings.stats.lossesHard
                else -> "Losses:"
            }
    }

    private fun drawWinningLine(winningLinePos: WinningLinePos){
        if(winningLinePos == WinningLinePos.Fail) return
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

