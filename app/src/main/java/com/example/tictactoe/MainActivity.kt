package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    lateinit var cell1 :Cell //I put these as lateinit because I need to initialize them later in the program, after setContentView() was called
    lateinit var cell2 :Cell
    lateinit var cell3 :Cell
    lateinit var cell4 :Cell
    lateinit var cell5 :Cell
    lateinit var cell6 :Cell
    lateinit var cell7 :Cell
    lateinit var cell8 :Cell
    lateinit var cell9 :Cell

    lateinit var currentTurnButton: ImageView

    lateinit var engine: Engine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cell1=Cell(findViewById<ImageButton>(R.id.imageButton1))
        cell2=Cell(findViewById<ImageButton>(R.id.imageButton2))
        cell3=Cell(findViewById<ImageButton>(R.id.imageButton3))
        cell4=Cell(findViewById<ImageButton>(R.id.imageButton4))
        cell5=Cell(findViewById<ImageButton>(R.id.imageButton5))
        cell6=Cell(findViewById<ImageButton>(R.id.imageButton6))
        cell7=Cell(findViewById<ImageButton>(R.id.imageButton7))
        cell8=Cell(findViewById<ImageButton>(R.id.imageButton8))
        cell9=Cell(findViewById<ImageButton>(R.id.imageButton9))

        val array = arrayOf(findViewById<ImageButton>(R.id.imageButton1),findViewById<ImageButton>(R.id.imageButton2),findViewById<ImageButton>(R.id.imageButton3)
            ,findViewById<ImageButton>(R.id.imageButton4),findViewById<ImageButton>(R.id.imageButton5),findViewById<ImageButton>(R.id.imageButton6),findViewById<ImageButton>(R.id.imageButton7)
            ,findViewById<ImageButton>(R.id.imageButton8),findViewById<ImageButton>(R.id.imageButton9))

        engine = Engine(array)

        currentTurnButton=findViewById<ImageView>(R.id.imageView)
    }

    fun cellClick(v: View){
        engine.fieldClick(v)

       /* when(v.id) {
            cell1.boundImageButton.id -> cell1.cellClick()
            cell2.boundImageButton.id -> cell2.cellClick()
            cell3.boundImageButton.id -> cell3.cellClick()
            cell4.boundImageButton.id -> cell4.cellClick()
            cell5.boundImageButton.id -> cell5.cellClick()
            cell6.boundImageButton.id -> cell6.cellClick()
            cell7.boundImageButton.id -> cell7.cellClick()
            cell8.boundImageButton.id -> cell8.cellClick()
            cell9.boundImageButton.id -> cell9.cellClick()
            currentTurnButton.id -> {
                cell1.reset()
                cell2.reset()
                cell3.reset()
                cell4.reset()
                cell5.reset()
                cell6.reset()
                cell7.reset()
                cell8.reset()
                cell9.reset()
            }
            else -> Log.i("ERROR","ID IS INVALID")
        }*/
    }
}
