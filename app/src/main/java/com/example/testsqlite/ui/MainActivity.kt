package com.example.testsqlite.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testsqlite.App
import com.example.testsqlite.R
import com.example.testsqlite.adapter.StudentAdapter
import com.example.testsqlite.data.Student
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val cd = CompositeDisposable()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StudentAdapter(this, mutableListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        rv.adapter = adapter

//        (0..10).forEach {
//            (application as App).dao.insert(Student("student-$it"))
//        }

        loadInitialItems()
    }

    private fun loadInitialItems() {
        val list = (application as App).dao.loadAll()
        adapter.updateData(list)
    }
}