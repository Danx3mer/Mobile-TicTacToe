package com.example.tictactoe

import android.widget.ImageButton

class Cell(imageButtonGiven:ImageButton){
    enum class ImageType{Blank,O,X}
    var image:ImageType = ImageType.Blank
    private set

    val boundImageButton = imageButtonGiven

    fun cellClick(): Boolean{
        if(this.image==ImageType.Blank){
                when(engine.currentTurn){
                    Engine.CurrentTurnType.X ->{
                        this.boundImageButton.setImageResource(R.drawable.x_new)
                        this.image = ImageType.X
                    }
                    Engine.CurrentTurnType.O ->{
                        this.boundImageButton.setImageResource(R.drawable.o_new)
                        this.image = ImageType.O
                    }
                }
                return true
        }
        return false
    }

    fun reset(){
        this.boundImageButton.setImageResource(R.drawable.blank)
        this.image = ImageType.Blank
    }
}
