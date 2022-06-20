package com.example.tictactoe

class Computer(private var difficulty: Difficulty) {

    private var movesDone = mutableListOf<Int>()

    private enum class Strategies{DiagonalStrategy, MiddleStrategy, None}
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
                val strategyRes = diagonalStrategy(cells, availableCells)

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

    private fun diagonalStrategy(cells: Array<Cell>, availableCells: MutableList<Int>): Int{
        //Every even number is the strategy for when the bot goes first.
        //Every odd number is the strategy for when the bot goes second.
        when(engine.numOfMoves){
            0 -> {
                this.movesDone.add(mutableListOf(0,2,6,8).random())
                return this.movesDone[0]
            }
            1 -> {
                val diagonals = mutableListOf<Int>()
                if(0 in availableCells && 8 in availableCells){
                    diagonals.add(0)
                    diagonals.add(8)
                }
                else {
                    this.movesDone.add(4)
                    return 4
                }
                if(2 in availableCells&& 6 in availableCells){
                    diagonals.add(2)
                    diagonals.add(6)
                }
                else {
                    this.movesDone.add(4)
                    return 4
                }
                this.movesDone.add(diagonals.random())
                return this.movesDone[0]
            }
            2 -> {
                when(this.movesDone[0]){
                    0 -> {
                        if(2 !in availableCells || 4 !in availableCells || 6 !in availableCells) { //means that the user went there
                            this.movesDone.add(8) //8 is the directly opposite diagonal
                            return 8
                        }
                    }
                    2 -> {
                        if(0 !in availableCells || 4 !in availableCells || 8 !in availableCells) { //means that the user went there
                            this.movesDone.add(6) //6 is the directly opposite diagonal
                            return 6
                        }
                    }
                    6 -> {
                        if(0 !in availableCells || 4 !in availableCells || 8 !in availableCells) { //means that the user went there
                            this.movesDone.add(2) //2 is the directly opposite diagonal
                            return 2
                        }
                    }
                    8 -> {
                        if(2 !in availableCells || 4 !in availableCells || 6 !in availableCells) { //means that the user went there
                            this.movesDone.add(0) //0 is the directly opposite diagonal
                            return 0
                        }
                    }
                }
                this.movesDone.add(4)
                return 4 //go to the middle in case if the user goes to the side edges
            }
            3 -> {
                if((cells[2].image==Cell.ImageType.O && cells[6].image==Cell.ImageType.O) || (cells[0].image==Cell.ImageType.O && cells[8].image==Cell.ImageType.O)){
                    this.movesDone.add(arrayListOf(1,3,5,7).random())
                    return this.movesDone[1]
                }
                if(this.checkForImage(cells, Cell.ImageType.X) != -1) return this.checkForImage(cells, Cell.ImageType.X)
                if(this.checkForImage(cells, Cell.ImageType.O) != -1) return this.checkForImage(cells, Cell.ImageType.O)
                when(this.movesDone[0]){
                    0 -> {
                        if(2 !in availableCells || 4 !in availableCells || 6 !in availableCells) { //means that the user went there
                            if(8 !in availableCells) {
                                this.movesDone.add(arrayListOf(2,6).random())
                                return this.movesDone[1]
                            }
                            this.movesDone.add(8) //8 is the directly opposite diagonal
                            return 8
                        }
                    }
                    2 -> {
                        if(0 !in availableCells || 4 !in availableCells || 8 !in availableCells) { //means that the user went there
                            if(6 !in availableCells) {
                                this.movesDone.add(arrayListOf(0,8).random())
                                return this.movesDone[1]
                            }
                            this.movesDone.add(6) //6 is the directly opposite diagonal
                            return 6
                        }
                    }
                    6 -> {
                        if(0 !in availableCells || 4 !in availableCells || 8 !in availableCells) { //means that the user went there
                            if(2 !in availableCells) {
                                this.movesDone.add(arrayListOf(0,8).random())
                                return this.movesDone[1]
                            }
                            this.movesDone.add(2) //2 is the directly opposite diagonal
                            return 2
                        }
                    }
                    8 -> {
                        if(2 !in availableCells || 4 !in availableCells || 6 !in availableCells) { //means that the user went there
                            if(0 !in availableCells) {
                                this.movesDone.add(arrayListOf(2,6).random())
                                return this.movesDone[1]
                            }
                            this.movesDone.add(0) //0 is the directly opposite diagonal
                            return 0
                        }
                    }
                }
                this.movesDone.add(4)
                return 4 //go to the middle in case if the user goes to the side edges
            }
            4 -> {
                if(this.checkForImage(cells, Cell.ImageType.X) != -1) return this.checkForImage(cells, Cell.ImageType.X)
                if(this.checkForImage(cells, Cell.ImageType.O) != -1) return this.checkForImage(cells, Cell.ImageType.O)

                if(4 in movesDone)
                    if(0 in movesDone) return arrayListOf(1,2).random()
                    else if(2 in movesDone) return arrayListOf(0,1).random()
                    else if(6 in movesDone) return arrayListOf(0,3).random()
                    else if(8 in movesDone) return arrayListOf(6,7).random()

                if (0 in movesDone && 8 in movesDone) {
                    val diagonals = mutableListOf<Int>()
                    if (2 in availableCells) diagonals.add(2)
                    if (6 in availableCells) diagonals.add(6)
                    this.movesDone.add(diagonals.random())
                    return this.movesDone[2]
                }
                else if (2 in movesDone && 6 in movesDone) {
                    val diagonals = mutableListOf<Int>()
                    if (0 in availableCells) diagonals.add(2)
                    if (8 in availableCells) diagonals.add(6)
                    this.movesDone.add(diagonals.random())
                    return this.movesDone[2]
                }
            }
            5 -> {
                if(this.checkForImage(cells, Cell.ImageType.X) != -1) return this.checkForImage(cells, Cell.ImageType.X)
                if(this.checkForImage(cells, Cell.ImageType.O) != -1) return this.checkForImage(cells, Cell.ImageType.O)
                if(0 in movesDone && 8 in movesDone) {
                    val diagonals = mutableListOf<Int>()
                    if(2 in availableCells) diagonals.add(2)
                    if(6 in availableCells) diagonals.add(6)
                    this.movesDone.add(diagonals.random())
                    return this.movesDone[2]
                }
                else if(2 in movesDone && 6 in movesDone) {
                    val diagonals = mutableListOf<Int>()
                    if(0 in availableCells) diagonals.add(2)
                    if(8 in availableCells) diagonals.add(6)
                    this.movesDone.add(diagonals.random())
                    return this.movesDone[2]
                }
            }
        }
        return -1
    }

    fun reset(difficulty: Difficulty){
        this.currentStrategy = Strategies.None
        this.movesDone = mutableListOf()
        this.difficulty = difficulty
    }
}
