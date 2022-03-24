package com.example.tictactoe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class Engine(val contextOfMainActivity: Context, imageButtons: Array<ImageButton>, val imageView: ImageView, val imageViewLineDrawer: ImageView) {
    private val cells = arrayOf(Cell(imageButtons[0]),
        Cell(imageButtons[1]),
        Cell(imageButtons[2]),
        Cell(imageButtons[3]),
        Cell(imageButtons[4]),
        Cell(imageButtons[5]),
        Cell(imageButtons[6]),
        Cell(imageButtons[7]),
        Cell(imageButtons[8]))

    init {
        this.resetField()
    }

    private var numOfMoves = 0

    enum class CurrentTurnType{X,O}
    var currentTurn:CurrentTurnType = CurrentTurnType.O

    private var isGameOver: Boolean = false

    fun fieldClick(view: View){
        if(this.isGameOver) return

        for(i in 0..8){
            if(this.cells[i].boundImageButton.id == view.id){
                if(this.cells[i].cellClick()){
                    if(++this.numOfMoves>=5)
                        if(this.winCheck()) {
                            gameOver(
                                when (this.currentTurn) {
                                    CurrentTurnType.O -> true
                                    CurrentTurnType.X -> false
                                }
                            )
                        }
                    this.switchTurns()
                }
            }
        }
    }

    private fun switchTurns(){
        when(this.currentTurn){
            CurrentTurnType.O -> {
                this.currentTurn=CurrentTurnType.X
                this.imageView.setImageResource(R.drawable.x)
            }
            CurrentTurnType.X -> {
                this.currentTurn=CurrentTurnType.O
                this.imageView.setImageResource(R.drawable.o)
            }
        }
    }

    fun resetField(){
        for(i in 0..8) this.cells[i].reset()
        this.imageView.setImageResource(R.drawable.o)
        this.currentTurn = CurrentTurnType.O
        this.isGameOver = false
    }

    private fun winCheck() :Boolean {
        var cellMatchCounter = 0
        val checkImage: Cell.ImageType = when(this.currentTurn){
            CurrentTurnType.X -> Cell.ImageType.X
            CurrentTurnType.O -> Cell.ImageType.O
        }

        //Checking horizontals
        for(i in 0..2) {
            for (cellCounter in i * 3..i * 3 + 2) {
                if (this.cells[cellCounter].image == checkImage) cellMatchCounter++

                if (cellMatchCounter == 3) return true
            }
            cellMatchCounter = 0
        }

        //Checking verticals
        for(i in 0..2) {
            for (cellCounter in arrayOf(i,3+i,6+i)) {
                if (this.cells[cellCounter].image == checkImage) cellMatchCounter++

                if (cellMatchCounter == 3) return true
            }
            cellMatchCounter = 0
        }

        //Checking diagonals
        for (cellCounter in arrayOf(0,4,8)) {
            if (this.cells[cellCounter].image == checkImage) cellMatchCounter++

            if (cellMatchCounter == 3) return true
        }
        cellMatchCounter = 0

        for (cellCounter in arrayOf(2,4,6)) {
            if (this.cells[cellCounter].image == checkImage) cellMatchCounter++

            if (cellMatchCounter == 3) return true
        }

        return false
    }

    private fun gameOver(oWon: Boolean){
        this.isGameOver = true

        drawWinningLine()

        when(oWon){
            true -> AlertDialog.Builder(this.contextOfMainActivity)
                .setTitle("WINNER!!!")
                .setMessage("O won!!!")
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss()}
                .show()

            false -> AlertDialog.Builder(this.contextOfMainActivity)
                .setTitle("WINNER!!!")
                .setMessage("X won!!!")
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss()}
                .show()
        }
    }

    private fun drawWinningLine(){
        //Todo: make this function functional.

        val bitmap = Bitmap.createBitmap(20, 700, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8F
        paint.isAntiAlias = true
        val offset = 50
        canvas.drawLine(
            offset.toFloat(), (canvas.height / 2).toFloat(), (canvas.width - offset).toFloat(), (canvas.height / 2).toFloat(), paint)
        this.imageViewLineDrawer.setImageBitmap(bitmap)
    }

}
