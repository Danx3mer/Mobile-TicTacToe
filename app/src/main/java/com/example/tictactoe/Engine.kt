package com.example.tictactoe

import android.util.Log
import android.view.View
import android.widget.ImageButton

class Engine(imageButtons: Array<ImageButton>) {
    val cells = arrayOf<Cell>(Cell(imageButtons[0]), Cell(imageButtons[1]), Cell(imageButtons[2]), Cell(imageButtons[3]),
        Cell(imageButtons[4]), Cell(imageButtons[5]), Cell(imageButtons[6]), Cell(imageButtons[7]), Cell(imageButtons[8]))
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
            CurrentTurnType.O -> this.currentTurn=CurrentTurnType.X
            CurrentTurnType.X -> this.currentTurn=CurrentTurnType.O
        }
    }

    fun resetField(){

    }

    fun winCheck(){

    }

}
