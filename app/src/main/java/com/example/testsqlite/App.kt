package com.example.testsqlite

import android.app.Application
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.testsqlite.data.DB
import java.io.File


class App : Application() {
    lateinit var dao: com.example.testsqlite.data.Dao
    override fun onCreate() {
        super.onCreate()
        val file = File("/data/data/${this.packageName}/databases/store.db")
        val db = Room.databaseBuilder(this, DB::class.java, "store.db")
            .createFromAsset("store.db")
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
    }
}