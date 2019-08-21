package com.example.testsqlite.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testsqlite.R
import com.example.testsqlite.adapter.StudentAdapter
import com.example.testsqlite.common.OnItemClickListener
import com.example.testsqlite.data.DatabaseProvider
import com.example.testsqlite.data.Student
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val KEY_STUDENT_ID = "KEY_STUDENT_ID"

class MainActivity : AppCompatActivity(),
    OnItemClickListener {
    private val cd = CompositeDisposable()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StudentAdapter(this, mutableListOf()) }
    private val studentDao by lazy(LazyThreadSafetyMode.NONE) {
        DatabaseProvider.instance(this).studentDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { startEditorActivity() }
        setUpRv()
        loadInitialItems()
    }

    private fun loadInitialItems() {
        studentDao
            .loadStudents()
            .observe(this, Observer { adapter.updateData(it) })
    }

    private fun startEditorActivity(id: Long? = null) {
        Intent(this, EditorActivity::class.java)
            .apply {
                putExtra(KEY_STUDENT_ID, id)
                startActivity(this)
            }

    }

    private fun setUpRv() {
        rv.adapter = adapter
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
                Single.fromCallable {
                    studentDao.deleteById(it)
                }.map { it > 0 }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer { if (it) adapter.removeItByPosition(pos) })
                    .let { cd.add(it) }
            }

        }
        val touchHelper = ItemTouchHelper(swipeListener)
        touchHelper.attachToRecyclerView(rv)
    }

    override fun onItemClick(it: Student) = startEditorActivity(it.id)
}