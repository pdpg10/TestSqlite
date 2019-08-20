package com.example.testsqlite

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testsqlite.adapter.StudentAdapter
import com.example.testsqlite.data.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StudentAdapter(this, mutableListOf()) }
    private val dbHelper by lazy(LazyThreadSafetyMode.NONE) { StudentOpenHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        addTempDataToDB()

        fab.setOnClickListener {
            loadFromDB()
        }
        setUpRv()

    }

    private fun addTempDataToDB() {
        var st: Student
        (0..100).forEach {
            st = Student(
                name = "Student$it",
                age = 16 + it % 5,
                course = it % 4,
                id = it
            )
            addToDB(st)
        }
    }

    private fun addToDB(st: Student) {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, st.name)
        cv.put(COLUMN_AGE, st.age)
        cv.put(COLUMN_COURSE, st.course)
        val stId = dbHelper.writableDatabase.insert(
            TABLE_STUDENTS,
            null, cv
        )
        Log.d("MainAC", "addToDB id:$stId")

    }

    private fun loadFromDB() {
        val columns = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_COURSE)
        val cursor = dbHelper.readableDatabase
            .query(
                TABLE_STUDENTS,
                columns,
                null,
                null,
                null,
                null,
                null
            )
        val idIndex = cursor.getColumnIndex(COLUMN_ID)
        val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
        val ageIndex = cursor.getColumnIndex(COLUMN_AGE)
        val courseIndex = cursor.getColumnIndex(COLUMN_COURSE)

        var name: String
        var id: Int
        var age: Int
        var course: Int
        val studentList = mutableListOf<Student>()
        while (cursor.moveToNext()) {
            name = cursor.getString(nameIndex)
            id = cursor.getInt(idIndex)
            age = cursor.getInt(ageIndex)
            course = cursor.getInt(courseIndex)
            studentList.add(Student(id, name, age, course))
        }
        adapter.updateData(studentList)
    }

    private fun setUpRv() {
        rv.adapter = adapter
    }
}