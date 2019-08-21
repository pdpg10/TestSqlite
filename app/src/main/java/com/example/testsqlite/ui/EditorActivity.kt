package com.example.testsqlite.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.testsqlite.R
import com.example.testsqlite.data.DatabaseProvider
import com.example.testsqlite.data.Student
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {

    private var isSaveEnabled = false
    private var isUpdate = false
    private var studentId: Long = -1
    private val cd = CompositeDisposable()
    private val studentDao by lazy(LazyThreadSafetyMode.NONE) {
        DatabaseProvider.instance(this).studentDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setUpValidate()
        loadFromIntent()
    }

    private fun loadFromIntent() {
        studentId = intent.getLongExtra(KEY_STUDENT_ID, -1)
        isUpdate = studentId > 0
        if (isUpdate) {
            studentDao
                .loadStudentById(studentId)
                .observe(this, Observer {
                    it?.apply {
                        et_name.setText(name)
                        et_age.setText("$age")
                        et_course.setText("$course")
                    }
                })
        }
    }

    private fun setUpValidate() {
        val d = Observable.combineLatest(
            et_name.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() },
            et_age.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() },
            et_course.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() },
            Function3<Boolean, Boolean, Boolean, Boolean> { name, age, course -> name && age && course }
        ).doOnNext { isSaveEnabled = it }
            .subscribe({ invalidateOptionsMenu() },
                { Log.d("Error", it.message) })
        cd.add(d)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_save) {
            if (isUpdate) {
                updateStudent()
            } else {
                insertNewItem()
            }
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun updateStudent() {
        val name = et_name.text.toString()
        val age = et_age.text.toString().toInt()
        val course = et_course.text.toString().toInt()
        val student = Student(name, age, course, studentId)
        Single.fromCallable { studentDao.updateStudent(student) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { finish() })
            .let { cd.add(it) }

    }

    private fun insertNewItem() {
        val name = et_name.text.toString()
        val age = et_age.text.toString().toInt()
        val course = et_course.text.toString().toInt()
        val student = Student(name, age, course)
        Single.fromCallable {
            studentDao.insert(student)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { finish() })
            .let { cd.add(it) }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.isEnabled = isSaveEnabled
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }
}
