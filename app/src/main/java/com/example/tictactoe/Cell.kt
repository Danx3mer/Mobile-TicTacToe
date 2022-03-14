package com.example.tictactoe

import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

//I have to inherit from Activity to use the findViewById function.
class Cell(imageButtonGiven:ImageButton): AppCompatActivity() {
    enum class ImageType{Blank,O,X}

    var image:ImageType = ImageType.Blank
        private set

    val boundImageButton = imageButtonGiven

    fun cellClick(){
        when(this.image){
            ImageType.Blank -> {
                this.boundImageButton.setImageResource(R.drawable.x) //TODO: after I made Engine, change this to a when(Engine.currentTurn) statement
                this.image = ImageType.X                          //TODO: after I made Engine, change this to a when(Engine.currentTurn) statement
            }
        }
    }

    fun reset(){
        this.boundImageButton.setImageResource(R.drawable.blank)
        this.image = ImageType.Blank
    }
}