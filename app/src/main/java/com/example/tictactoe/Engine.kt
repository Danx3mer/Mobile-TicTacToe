package com.example.tictactoe

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

class Engine(val contextOfMainActivity: Context, imageButtons: Array<ImageButton>, val imageView: ImageView) {
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
        cellMatchCounter = 0

        return false
    }

    private fun gameOver(oWon: Boolean){
        this.isGameOver = true

        //TODO: Make gameover message nicer
        when(oWon){
            true -> Toast.makeText(contextOfMainActivity, "O WON!!!", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(contextOfMainActivity, "X WON!!!", Toast.LENGTH_SHORT).show()
        }
    }
}
