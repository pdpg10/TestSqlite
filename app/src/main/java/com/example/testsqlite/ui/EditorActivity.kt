package com.example.testsqlite.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.testsqlite.R
import com.example.testsqlite.data.Student
import com.example.testsqlite.data.StudentOpenHelper
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {

    private var isSaveEnabled = false
    private var isUpdate = false
    private var studentId: Long = -1
    private val cd = CompositeDisposable()

    //todo convert to rx homework 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setUpValidate()
        loadFromIntent()
    }

    private fun loadFromIntent() {
        studentId = intent.getLongExtra(KEY_STUDENT_ID, -1)
        isUpdate = studentId > 0
        val student = StudentOpenHelper
            .instance(this)
            .loadStudentById(studentId)
        student?.apply {
            et_name.setText(name)
            et_age.setText("$age")
            et_course.setText("$course")
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
        val id = StudentOpenHelper
            .instance(this)
            .updateStudent(student)
        Log.d("Editor", "updateStudent $id")
        setAndFinish(studentId)
    }

    private fun insertNewItem() {
        val name = et_name.text.toString()
        val age = et_age.text.toString().toInt()
        val course = et_course.text.toString().toInt()
        val student = Student(name, age, course)
        val id = StudentOpenHelper
            .instance(this)
            .insertStudent(student)

        setAndFinish(id)
    }

    private fun setAndFinish(id: Long) {
        intent.putExtra(KEY_STUDENT_ID, id)
        setResult(Activity.RESULT_OK, intent)
        finish()
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
