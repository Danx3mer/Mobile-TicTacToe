package kt.game.tictactoe

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
import kotlinx.coroutines.*

class Engine(private val contextOfMainActivity: Context) {
    enum class GameOverCode { Win, Loss, Tie }

    private lateinit var imageView: ImageView
    private lateinit var imageViewLineDrawer: ImageView

    private val computer: Computer = Computer(Difficulty.Medium)
    private lateinit var cells: Array<Cell>
    var computerGoesFirst: Boolean = false
    private set

    var currentDifficulty: Difficulty = settings.defaultDifficulty

    private var coroutine: CoroutineScope? = null
    private var coroutineEnded = true

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
    var currentTurn: CurrentTurnType = CurrentTurnType.O

    private var isGameOver = false

    fun endCoroutine() { this.coroutine?.cancel(); this.coroutineEnded = true }

    fun swapIcons(): Boolean {
        val tempIcon = settings.personIcon
        settings.personIcon = when(settings.personIcon) {
            Cell.ImageType.O -> Cell.ImageType.X
            Cell.ImageType.X -> Cell.ImageType.O
            else -> return false
        }
        settings.computerIcon = tempIcon

        if(this.currentDifficulty != Difficulty.None)
            settings.pveIcon = settings.personIcon

        return when(settings.personIcon){
            Cell.ImageType.O -> true
            Cell.ImageType.X -> false
            else -> false
        }
    }

    private fun firstMove() {
        if(this.computerGoesFirst) {
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

    private fun computerMove() {
        this.coroutine = CoroutineScope(Dispatchers.Main)
        this.coroutine!!.launch {
            this@Engine.coroutineEnded = false
            delay(500)

            val computerPick = computer.pickCell(this@Engine.cells)
            if (computerPick == -1) return@launch
            this@Engine.cells[computerPick].cellClick()
            playSound(R.raw.click, this@Engine.contextOfMainActivity)

            if (++this@Engine.numOfMoves >= 5) {
                val winCheckRes: WinningLinePos = this@Engine.winCheck()
                if (winCheckRes != WinningLinePos.Fail) {
                    gameOver(GameOverCode.Loss, winCheckRes)
                    playSound(R.raw.lose, contextOfMainActivity)
                }
                else if (this@Engine.numOfMoves == 9) {
                    gameOver(GameOverCode.Tie, winCheckRes)

                }

            }
            this@Engine.switchTurns()
            this@Engine.coroutineEnded = true
        }
    }

    fun fieldClick(view: View) {
        if(this.isGameOver || !this.coroutineEnded) return

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

        if(this.currentDifficulty != Difficulty.None) this.computerMove()
    }

    private fun switchTurns() {
        if(this.isGameOver) return
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

    fun startNewGame(difficulty: Difficulty = Difficulty.None, computerGoesFirst: Boolean) {
        this.endCoroutine()

        for(i in 0..8) this.cells[i].reset()
        this.isGameOver = false
        this.numOfMoves = 0

        this.imageView.setImageResource(when(settings.personIcon){
            Cell.ImageType.O -> R.drawable.o
            Cell.ImageType.X -> R.drawable.x
            else -> R.drawable.blank
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

    private fun winCheck() : WinningLinePos {
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

    private fun gameOver(gameOverCode: GameOverCode, winningLinePos: WinningLinePos?) {
        this.isGameOver = true
        if(winningLinePos != null) this.drawWinningLine(winningLinePos)
        settings.stats.updateStats(gameOverCode, this.currentDifficulty)

        when(settings.personIcon){
            Cell.ImageType.O -> {
                when(gameOverCode) {
                    GameOverCode.Win -> this.imageView.setImageResource(R.drawable.o_winner)
                    GameOverCode.Tie -> this.imageView.setImageResource(R.drawable.tie)
                    GameOverCode.Loss -> this.imageView.setImageResource(R.drawable.x_winner)
                }
            }
            else -> {
                when(gameOverCode) {
                    GameOverCode.Win -> this.imageView.setImageResource(R.drawable.x_winner)
                    GameOverCode.Tie -> this.imageView.setImageResource(R.drawable.tie)
                    GameOverCode.Loss -> this.imageView.setImageResource(R.drawable.o_winner)
                }
            }
        }

        when(this.currentDifficulty) {
            Difficulty.Hard -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.You_Won_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Its_tie_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Bot_Won_MSG), this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.Medium -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.You_Won_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Its_tie_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Bot_Won_MSG), this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.Easy -> {
                currentToast = when(gameOverCode){
                    GameOverCode.Win -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.You_Won_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Tie -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Its_tie_MSG), this.contextOfMainActivity as Activity)
                    GameOverCode.Loss -> Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Bot_Won_MSG), this.contextOfMainActivity as Activity)
                }
            }
            Difficulty.None -> {
                when(gameOverCode) {
                    GameOverCode.Win -> {
                        if(settings.personIcon == Cell.ImageType.O) {
                            currentToast = Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.O_Won_MSG), this.contextOfMainActivity as Activity)
                            if(settings.autoSwitch) settings.personIcon = Cell.ImageType.O
                        }
                        else{
                            currentToast = Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.X_Won_MSG), this.contextOfMainActivity as Activity)
                            if(settings.autoSwitch) settings.personIcon = Cell.ImageType.X
                        }
                    }

                    GameOverCode.Tie -> {
                        currentToast = Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.Its_tie_MSG), this.contextOfMainActivity as Activity)
                        if(settings.autoSwitch) this.swapIcons()
                    }

                    GameOverCode.Loss -> {
                        if(settings.personIcon == Cell.ImageType.O) {
                            currentToast = Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.X_Won_MSG), this.contextOfMainActivity as Activity)
                            if(settings.autoSwitch) settings.personIcon = Cell.ImageType.X
                        }
                        else{
                            currentToast = Toast(this.contextOfMainActivity).showCustomToast(this.contextOfMainActivity.getString(R.string.O_Won_MSG), this.contextOfMainActivity as Activity)
                            if(settings.autoSwitch) settings.personIcon = Cell.ImageType.O
                        }
                    }
                }
            }
        }

        this.contextOfMainActivity.findViewById<TextView>(R.id.tv_ministats_wins).text = when(engine.currentDifficulty) {
            Difficulty.Easy -> this.contextOfMainActivity.getString(R.string.Wins_colon) + " " + settings.stats.winsEasy
            Difficulty.Medium -> this.contextOfMainActivity.getString(R.string.Wins_colon) + " " + settings.stats.winsMedium
            Difficulty.Hard -> this.contextOfMainActivity.getString(R.string.Wins_colon) + " " + settings.stats.winsHard
            else -> this.contextOfMainActivity.getString(R.string.OWins) + " " + settings.stats.oWins
        }

        this.contextOfMainActivity.findViewById<TextView>(R.id.tv_ministats_ties).text = when(engine.currentDifficulty)
        {
            Difficulty.Easy -> this.contextOfMainActivity.getString(R.string.Ties_colon) + " " + settings.stats.tiesEasy
            Difficulty.Medium -> this.contextOfMainActivity.getString(R.string.Ties_colon) + " " + settings.stats.tiesMedium
            Difficulty.Hard -> this.contextOfMainActivity.getString(R.string.Ties_colon) + " " + settings.stats.tiesHard
            else -> this.contextOfMainActivity.getString(R.string.Ties_colon) + " " + settings.stats.pvpTies
        }

        this.contextOfMainActivity.findViewById<TextView>(R.id.tv_ministats_losses).text = when(engine.currentDifficulty)
        {
            Difficulty.Easy -> this.contextOfMainActivity.getString(R.string.Losses_colon) + " " + settings.stats.lossesEasy
            Difficulty.Medium -> this.contextOfMainActivity.getString(R.string.Losses_colon) + " " + settings.stats.lossesMedium
            Difficulty.Hard -> this.contextOfMainActivity.getString(R.string.Losses_colon) + " " + settings.stats.lossesHard
            else -> this.contextOfMainActivity.getString(R.string.XWins) + " " + settings.stats.xWins
        }
    }

    private fun drawWinningLine(winningLinePos: WinningLinePos) {
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

