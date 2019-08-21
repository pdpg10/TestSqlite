package com.example.testsqlite.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StudentDao {

    @Insert
    fun insert(it: Student): Long

    @Update
    fun updateStudent(student: Student): Int

    @Delete
    fun deleteById(id: Student): Int

    @Query("select * from student")
    fun loadStudents(): LiveData<MutableList<Student>>

    @Query("select * from student where id=:newStudentId")
    fun loadStudentById(newStudentId: Long): LiveData<Student?>

}