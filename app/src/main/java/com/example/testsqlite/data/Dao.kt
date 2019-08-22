package com.example.testsqlite.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Query("select * from Student")
    fun loadAll(): List<Student>

    @Insert
    fun insert(it:Student)
}