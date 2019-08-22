package com.example.testsqlite.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 2)
abstract class DB : RoomDatabase() {
    abstract fun dao(): Dao
}