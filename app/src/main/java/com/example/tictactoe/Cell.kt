package com.example.tictactoe

import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

//I have to inherit from Activity to use the findViewById function.
class Cell(imageButtonGiven:ImageButton): AppCompatActivity() {
    enum class ImageType{Blank,O,X}

    var image:ImageType = ImageType.Blank
        private set

    val boundImageButton = imageButtonGiven

    fun cellClick(): Boolean{
        when(this.image){
            ImageType.Blank -> {

                when(engine.currentTurn){
                    Engine.CurrentTurnType.X ->{
                        this.boundImageButton.setImageResource(R.drawable.x)
                        this.image = ImageType.X
                    }
                    Engine.CurrentTurnType.O ->{
                        this.boundImageButton.setImageResource(R.drawable.o)
                        this.image = ImageType.O
                    }
                }
                return true
            }
        }
        return false
    }

    fun reset(){
        this.boundImageButton.setImageResource(R.drawable.blank)
        this.image = ImageType.Blank
    }
}