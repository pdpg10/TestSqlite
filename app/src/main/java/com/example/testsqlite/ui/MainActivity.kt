package com.example.testsqlite.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testsqlite.R
import com.example.testsqlite.adapter.StudentAdapter
import com.example.testsqlite.data.Student
import com.example.testsqlite.data.StudentOpenHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StudentAdapter(this, mutableListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        rv.adapter = adapter

        (0..10).forEach {
            StudentOpenHelper
                .instance(this)
                .insertStudent(Student("student-$it"))

        }

        loadInitialItems()
    }

    @SuppressLint("CheckResult")
    private fun loadInitialItems() {
        StudentOpenHelper
            .instance(this)
            .loadStudents()
            .subscribe(adapter::updateData)
    }
}