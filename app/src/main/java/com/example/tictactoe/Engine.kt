package com.example.tictactoe

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView

class Engine(imageButtons: Array<ImageButton>, imageview: ImageView) {
    private val cells = arrayOf<Cell>(Cell(imageButtons[0]),
        Cell(imageButtons[1]),
        Cell(imageButtons[2]),
        Cell(imageButtons[3]),
        Cell(imageButtons[4]),
        Cell(imageButtons[5]),
        Cell(imageButtons[6]),
        Cell(imageButtons[7]),
        Cell(imageButtons[8]))
    private val imageView = imageview

    init {
        this.resetField()
    }

    private var numOfMoves = 0

    enum class CurrentTurnType{X,O}
    var currentTurn:CurrentTurnType = CurrentTurnType.O

    fun fieldClick(view: View){
        for(i in 0..8){
            if(this.cells[i].boundImageButton.id == view.id){
                if(this.cells[i].cellClick()){
                    this.switchTurns()
                    this.numOfMoves++
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
        this.currentTurn=CurrentTurnType.O
    }

    fun winCheck(){

    }

}
