package com.example.testsqlite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    val name: String = "",
    val age: Int = 16,
    val course: Int = 1,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)