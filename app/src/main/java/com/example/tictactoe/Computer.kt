package com.example.tictactoe

class Computer(private var difficulty: Difficulty) {

    private var movesDone = mutableListOf<Int>()

    private enum class Strategies{DiagonalStrategyOffense, DiagonalStrategyDefense, MiddleStrategyDefense, MiddleStrategyOffense, None}
    private var currentStrategy: Strategies = Strategies.None

    fun pickCell(cells: Array<Cell>): Int {
        val availableCells = mutableListOf(0,1,2,3,4,5,6,7,8)
        for(i in 0..8){
            if(cells[i].image != Cell.ImageType.Blank) availableCells.remove(i)
        }
        when(difficulty){

            Difficulty.Easy -> {
                return availableCells.random()
            }

            Difficulty.Medium -> {
                if(availableCells.contains(4)) return 4 //This is the middle

                val offenseRes = this.checkForImage(cells, Cell.ImageType.X) /*I send in the image that the function is checking for. For attack,
                                                                              I put in X (because it is looking for it's own cells),
                                                                              but for defense I put O (because it is checking for the user's cells.)*/
                if(offenseRes != -1) return offenseRes

                val defendRes = this.checkForImage(cells, Cell.ImageType.O) /*I send in the image that the function is checking for. For attack,
                                                                              I put in O (because it is checking for the user's cells),
                                                                              but for offense I put X (because it is looking for it's own cells.)*/
                if(defendRes != -1) return defendRes

                return availableCells.random()
            }

            Difficulty.Hard -> {
                val diagonals = mutableListOf(0,2,6,8)
                val edges = mutableListOf(1,3,5,7)
                
                when(engine.numOfMoves){
                    0 -> this.currentStrategy = arrayListOf(Strategies.DiagonalStrategyOffense, Strategies.MiddleStrategyOffense).random() //Going on the offense
                    1 -> this.currentStrategy = arrayListOf(Strategies.DiagonalStrategyDefense, Strategies.MiddleStrategyDefense).random() //Going on the defense
                }

                val strategyRes = when(this.currentStrategy){
                    Strategies.DiagonalStrategyOffense -> diagonalStrategy(true, availableCells)
                    Strategies.DiagonalStrategyDefense -> diagonalStrategy(false, availableCells)
                    Strategies.MiddleStrategyOffense -> middleStrategy(true, availableCells)
                    Strategies.MiddleStrategyDefense -> middleStrategy(false, availableCells)
                    else -> -1
                }

                if(strategyRes != -1) return strategyRes

                //Switch to playing like medium mode

                val offenseRes = this.checkForImage(cells, Cell.ImageType.X) /*I send in the image that the function is checking for. For attack,
                                                                              I put in X (because it is looking for it's own cells),
                                                                              but for defense I put O (because it is checking for the user's cells.)*/
                if(offenseRes != -1) return offenseRes

                val defendRes = this.checkForImage(cells, Cell.ImageType.O) /*I send in the image that the function is checking for. For attack,
                                                                              I put in O (because it is checking for the user's cells),
                                                                              but for offense I put X (because it is looking for it's own cells.)*/
                if(defendRes != -1) return defendRes

                return availableCells.random()
            }

            else -> return -1
        }
        return -1
    }

    private fun checkForImage(cells: Array<Cell>, checkImage: Cell.ImageType): Int{
        var cellMatchCounter = 0
        var emptyCell = -1

        //Checking horizontals
        for(i in 0..2) {
            val checkingArray = arrayOf(i * 3, i * 3 + 1, i * 3 + 2)
            for (cellCounter in checkingArray) {
                if (cells[cellCounter].image == checkImage) {
                    if (++cellMatchCounter == 2) return when(emptyCell){
                        -1 -> {
                            if(cells[checkingArray.last()].image == Cell.ImageType.Blank) checkingArray.last()
                            else continue
                        }
                        else -> emptyCell
                    }
                }
                else if (cells[cellCounter].image == Cell.ImageType.Blank) emptyCell = cellCounter
            }
            cellMatchCounter = 0
            emptyCell = -1
        }

        //Checking verticals
        for(i in 0..2) {
            val checkingArray = arrayOf(i, 3 + i, 6 + i)
            for (cellCounter in checkingArray) {
                if (cells[cellCounter].image == checkImage) {
                    if (++cellMatchCounter == 2) return when(emptyCell){
                        -1 -> {
                            if(cells[checkingArray.last()].image == Cell.ImageType.Blank) checkingArray.last()
                            else continue
                        }
                        else -> emptyCell
                    }
                }
                else if (cells[cellCounter].image == Cell.ImageType.Blank) emptyCell = cellCounter
            }
            cellMatchCounter = 0
            emptyCell = -1
        }

        //Checking diagonals #1
        val checkingArrayD1 = arrayOf(0,4,8)
        for (cellCounter in checkingArrayD1) {
            if (cells[cellCounter].image == checkImage) {
                if (++cellMatchCounter == 2) return when(emptyCell){
                    -1 -> {
                        if(cells[checkingArrayD1.last()].image == Cell.ImageType.Blank) checkingArrayD1.last()
                        else continue
                    }
                    else -> emptyCell
                }
            }
            else if (cells[cellCounter].image == Cell.ImageType.Blank) emptyCell = cellCounter
        }
        cellMatchCounter = 0
        emptyCell = -1

        //Checking diagonals #2
        val checkingArrayD2 = arrayOf(2,4,6)
        for (cellCounter in checkingArrayD2) {
            if (cells[cellCounter].image == checkImage) {
                if (++cellMatchCounter == 2) return when(emptyCell){
                    -1 -> {
                        if(cells[checkingArrayD2.last()].image == Cell.ImageType.Blank) checkingArrayD2.last()
                        else continue
                    }
                    else -> emptyCell
                }
            }
            else if (cells[cellCounter].image == Cell.ImageType.Blank) emptyCell = cellCounter
        }

        return -1
    }

    fun diagonalStrategy(offense: Boolean, availableCells: MutableList<Int>): Int{
        when(engine.numOfMoves){
            0 -> return mutableListOf(0,2,6,8).random()
        }
        return -1
    }

    fun middleStrategy(offense: Boolean, availableCells: MutableList<Int>): Int{
        when(engine.numOfMoves){
            0 -> return 4
            1 -> if(4 in availableCells) return 4
        }

        return -1
    }

    fun reset(difficulty: Difficulty){
        this.currentStrategy = Strategies.None
        this.movesDone = mutableListOf()
        this.difficulty = difficulty
    }
}
