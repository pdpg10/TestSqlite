package com.example.testsqlite.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val TABLE_STUDENTS = "STUDENTS"
const val COLUMN_ID = "ID"
const val COLUMN_NAME = "NAME"
const val COLUMN_AGE = "AGE"
const val COLUMN_COURSE = "COURSE"

class StudentOpenHelper(
    ctx: Context
) : SQLiteOpenHelper(ctx, "store.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) return
        val sqlQuery = """
            CREATE TABLE $TABLE_STUDENTS(
                                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                                    $COLUMN_NAME TEXT NOT NULL,
                                    $COLUMN_AGE INTEGER NOT NULL DEFAULT 16,
                                    $COLUMN_COURSE INTEGER NOT NULL DEFAULT 1);
        """.trimIndent()
        db.execSQL(sqlQuery)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}