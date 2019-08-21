package com.example.testsqlite.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}