package kt.game.tictactoe

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseManager(context: Context?) : SQLiteOpenHelper(context, "Settings.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE SETTINGS_TABLE (SETTINGS_MODE TEXT, WINS INT, LOSSES INT, TIES INT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun getSettings(settingsMode: String): ArrayList<Int> {
        val returnList = ArrayList<Int>()

        val db: SQLiteDatabase = this.writableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM SETTINGS_TABLE", null)
        if(cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != settingsMode) continue
                returnList.add(cursor.getInt(1))
                returnList.add(cursor.getInt(2))
                returnList.add(cursor.getInt(3))
            } while(cursor.moveToNext())
        }
        cursor.close()
        return returnList
    }

    fun writeSettings(settingsMode: String, values: List<Int>): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv = ContentValues()

        cv.put("SETTINGS_MODE", settingsMode)
        cv.put("WINS", values[0])
        cv.put("LOSSES", values[1])
        cv.put("TIES", values[2])

        return when(db.insert("SETTINGS_TABLE", null, cv)){
            (-1).toLong() -> false
            else -> true
        }
    }

    fun overwriteValue(settingsMode: String, valueName: String, newValue: Int) =
        this.writableDatabase.execSQL("UPDATE SETTINGS_TABLE SET $valueName = $newValue WHERE SETTINGS_MODE = '$settingsMode'")
}