package com.example.tictactoe

class Computer(var difficulty: Difficulty) {
    fun pickCell(): Int {
        var availableCells = mutableListOf(0,1,2,3,4,5,6,7,8)
        when(difficulty){
            Difficulty.Easy -> {
                for(i in 0..8){
                    if(engine.cells[i].image!=Cell.ImageType.Blank) availableCells.remove(i)
                }
                return availableCells.random()
            }
            Difficulty.Medium -> {

            }
            Difficulty.Hard -> {

            }
        }
        return -1
    }
    fun reset(difficulty: Difficulty) { this.difficulty = difficulty }
}