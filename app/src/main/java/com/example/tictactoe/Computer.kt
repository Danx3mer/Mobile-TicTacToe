package com.example.tictactoe

class Computer(var difficulty: Difficulty) {
    fun pickCell(cells: Array<Cell>): Int {
        var availableCells = mutableListOf(0,1,2,3,4,5,6,7,8)
        for(i in 0..8){
            if(cells[i].image!=Cell.ImageType.Blank) availableCells.remove(i)
        }
        when(difficulty){
            Difficulty.Easy -> {
                return availableCells.random()
            }
            Difficulty.Medium -> {

            }
            Difficulty.Hard -> {
                if(engine.numOfMoves==0) return 4 //This is the middle
                val attackRes = this.checkForWin()
                if(attackRes!=-1) return attackRes
                val defendRes = this.checkForWin()
                if(defendRes!=-1) return defendRes
                return availableCells.random()
            }
        }
        return -1
    }

    private fun checkForWin() :Int{

        return -1
    }

    private fun checkForDanger() :Int{

        return -1
    }

    fun reset(difficulty: Difficulty) { this.difficulty = difficulty }
}