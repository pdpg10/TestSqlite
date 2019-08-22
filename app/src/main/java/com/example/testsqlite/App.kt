package com.example.testsqlite

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.testsqlite.data.DB


class App : Application() {
    lateinit var dao: com.example.testsqlite.data.Dao
    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(this, DB::class.java, "store.db")
            .fallbackToDestructiveMigration()
            .addMigrations(object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {

                }
            })
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
    }
}