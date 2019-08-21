package com.example.testsqlite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
//    (tableName = "student_table")
data class Student(
//    @ColumnInfo(name = "student_name")
    val name: String = "",
    val age: Int = 16,
    val course: Int = 1,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)