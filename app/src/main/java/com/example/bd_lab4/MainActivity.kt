package com.example.bd_lab4

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    lateinit var addB : Button
    lateinit var readB : Button
    lateinit var clearB : Button
    lateinit var nameEd : EditText
    lateinit var emailEd : EditText
    lateinit var content : TextView

    val LOG_TAG : String = "myLogs"

    private lateinit var  dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addB = findViewById(R.id.add)
        readB = findViewById(R.id.read)
        clearB = findViewById(R.id.clear)
        nameEd = findViewById(R.id.name)
        emailEd = findViewById(R.id.email)
        content = findViewById(R.id.journal)

        dbHelper = DBHelper(this)

    }

    fun onClick(v : View){
        val cv = ContentValues()
        val name = nameEd.text.toString()
        val email = emailEd.text.toString()

        val db : SQLiteDatabase = dbHelper.writableDatabase
        when (v.id){
            R.id.add -> {
                Log.d(LOG_TAG, "--- Insert in mytable: ---")
                cv.put("name", name)
                cv.put("email", email)
                val rowID : Long = db.insert("mytable", null, cv)
                Log.d(LOG_TAG, "row inserted, ID = $rowID")
            }
            R.id.read ->{
                Log.d(LOG_TAG, "--- Rows in mytable: ---")
                val c: Cursor = db.query("mytable", null, null, null, null, null, null)
                var dataRead: String = ""
                if(c.moveToFirst()){
                    val idColIndex: Int = c.getColumnIndex("id")
                    val nameColIndex: Int = c.getColumnIndex("name")
                    val emailColIndex: Int = c.getColumnIndex("email")
                    do {
                        dataRead+="ID = " + c.getInt(idColIndex)+", name = " + c.getString(nameColIndex)+", email = " + c.getString(emailColIndex)+"\n"
                        Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex)+
                                ", name = " + c.getString(nameColIndex)+
                                ", email = " + c.getString(emailColIndex))
                    }
                    while (c.moveToNext())
                    dataRead.dropLast(1)
                }
                else{
                    dataRead = "0 rows"
                    Log.d(LOG_TAG, dataRead)
                }
                content.text = dataRead
                c.close()
            }
            R.id.clear -> {
                Log.d(LOG_TAG, "--- Clear mytable ---")
                val clearCount: Int = db.delete("mytable", null, null)
                Log.d(LOG_TAG, "deleted rows count = $clearCount")
            }
        }
        dbHelper.close()
    }

    class DBHelper(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        val LOG_TAG: String = "myLogs"

        companion object {
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "myDatabase.db"
            private const val TABLE_NAME = "mytable"
            private const val COLUMN_ID = "id"
            private const val COLUMN_NAME = "name"
            private const val COLUMN_EMAIL = "email"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            Log.d(LOG_TAG, "--- onCreate database ---")
            db?.execSQL("create table $TABLE_NAME ($COLUMN_ID integer primary key autoincrement, "
                    +"$COLUMN_NAME text, $COLUMN_EMAIL text);")
        }

        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db?.execSQL("drop table if exists $TABLE_NAME")
            onCreate(db)
        }

    }

}
