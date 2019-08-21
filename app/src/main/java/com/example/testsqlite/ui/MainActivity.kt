package com.example.testsqlite.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testsqlite.R
import com.example.testsqlite.adapter.StudentAdapter
import com.example.testsqlite.common.OnItemClickListener
import com.example.testsqlite.data.Student
import com.example.testsqlite.data.StudentOpenHelper
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val ACTION_INSERT_STUDENT = 1001
const val ACTION_UPDATE_STUDENT = 1002
const val KEY_STUDENT_ID = "KEY_STUDENT_ID"
const val KEY_STUDENT = "KEY_STUDENT"

class MainActivity : AppCompatActivity(),
    OnItemClickListener {
    private val cd = CompositeDisposable()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StudentAdapter(this, mutableListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { startEditorActivity(ACTION_INSERT_STUDENT) }
        setUpRv()
        loadInitialItems()
    }

    private fun loadInitialItems() {
        StudentOpenHelper
            .instance(this)
            .loadStudents()
            .subscribe(adapter::updateData)
            .let { cd.add(it) }

    }

    private fun startEditorActivity(action: Int, id: Long? = null) {
        Intent(this, EditorActivity::class.java)
            .apply {
                putExtra(KEY_STUDENT_ID, id)
                startActivityForResult(this, action)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (resultCode == Activity.RESULT_OK
            && requestCode in arrayOf(ACTION_INSERT_STUDENT, ACTION_UPDATE_STUDENT)
        ) {
            val newStudentId = data.getLongExtra(KEY_STUDENT_ID, 0)
            StudentOpenHelper
                .instance(this)
                .loadStudentById(newStudentId)
                .subscribe(Consumer { updateOrInsert(it, requestCode) })
                .let { cd.add(it) }

        }
    }

    private fun updateOrInsert(it: Student?, requestCode: Int) {
        if (it == null) return
        if (requestCode == ACTION_INSERT_STUDENT) {
            adapter.addItem(it)
        } else if (requestCode == ACTION_UPDATE_STUDENT) {
            adapter.updateItem(it)
        }
    }

    private fun setUpRv() {
        rv.adapter = adapter
        //todo homework 0 create simple callback
        //todo add ListAdapter
        //todo optimize update op
        val swipeListener = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val pos = viewHolder.adapterPosition
                val it = adapter.getItemByPosition(pos)
                deleteStudent(it)
                    .subscribe(Consumer {
                        if (it) adapter.removeItByPosition(pos)
                    })
                    .let { cd.add(it) }

            }

        }
        val touchHelper = ItemTouchHelper(swipeListener)
        touchHelper.attachToRecyclerView(rv)
    }

    private fun deleteStudent(it: Student): Single<Boolean> {
        return StudentOpenHelper
            .instance(this)
            .deleteById(it.id)
    }

    override fun onItemClick(it: Student) = startEditorActivity(ACTION_UPDATE_STUDENT, it.id)
}