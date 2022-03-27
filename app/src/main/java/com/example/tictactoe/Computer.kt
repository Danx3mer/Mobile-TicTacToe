package com.example.tictactoe

class Computer(var difficulty: Difficulty) {
    fun pickCell(): Int {
        when(difficulty){
            Difficulty.Easy -> {

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