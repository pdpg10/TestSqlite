package com.example.testsqlite.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

const val TABLE_STUDENTS = "STUDENTS"
const val COLUMN_ID = "ID"
const val COLUMN_NAME = "NAME"
const val COLUMN_AGE = "AGE"
const val COLUMN_COURSE = "COURSE"

class StudentOpenHelper(
    ctx: Context
) : SQLiteOpenHelper(ctx, "store.db", null, 1) {
    private val columns = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_COURSE)

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) return
        val sqlQuery = """
            CREATE TABLE $TABLE_STUDENTS(
                                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                                    $COLUMN_NAME TEXT NOT NULL,
                                    $COLUMN_AGE INTEGER NOT NULL DEFAULT 16,
                                    $COLUMN_COURSE INTEGER NOT NULL DEFAULT 1);
        """.trimIndent()
        db.execSQL(sqlQuery)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insertStudent(it: Student): Single<Long> {
        val cv = toContentValues(it)
        return Single.just(writableDatabase.insert(TABLE_STUDENTS, null, cv))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun loadStudentById(newStudentId: Long): Single<Student?> {
        return Single.create<Student?> {
            var cursor: Cursor? = null
            val selection = "ID=?"
            val args = arrayOf("$newStudentId")
            try {
                cursor = readableDatabase
                    .query(
                        TABLE_STUDENTS,
                        columns,
                        selection,
                        args, null, null, null, null
                    )
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val ageIndex = cursor.getColumnIndex(COLUMN_AGE)
                val courseIndex = cursor.getColumnIndex(COLUMN_COURSE)

                val name: String
                val id: Long
                val age: Int
                val course: Int
                cursor.moveToFirst()
                name = cursor.getString(nameIndex)
                id = cursor.getLong(idIndex)
                age = cursor.getInt(ageIndex)
                course = cursor.getInt(courseIndex)
                it.onSuccess(Student(name, age, course, id))
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(e)
            } finally {
                cursor?.close()
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun loadStudents(): Single<MutableList<Student>> {
        return Single.create<MutableList<Student>> {
            try {
                val cursor = readableDatabase
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
                var id: Long
                var age: Int
                var course: Int
                val studentList = mutableListOf<Student>()
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    id = cursor.getLong(idIndex)
                    age = cursor.getInt(ageIndex)
                    course = cursor.getInt(courseIndex)
                    studentList.add(Student(name, age, course, id))
                }
                it.onSuccess(studentList)
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(e)

            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateStudent(student: Student): Single<Boolean> {
        val cv = toContentValues(student)
        val whereClause = "ID=?"
        val whereArgs = arrayOf("${student.id}")
        return Single.just(
            writableDatabase.update(
                TABLE_STUDENTS,
                cv,
                whereClause,
                whereArgs
            )
        ).map { it > 0 }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteById(id: Long): Single<Boolean> {
        val whereClause = "ID=?"
        val whereArgs = arrayOf("$id")
        return Single.just(
            writableDatabase
                .delete(
                    TABLE_STUDENTS,
                    whereClause,
                    whereArgs
                )
        ).map { it > 0 }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun toContentValues(it: Student): ContentValues {
        val cv = ContentValues()
        cv.put(COLUMN_NAME, it.name)
        cv.put(COLUMN_AGE, it.age)
        cv.put(COLUMN_COURSE, it.course)
        return cv
    }


    companion object {
        private var instance: StudentOpenHelper? = null
        fun instance(ctx: Context): StudentOpenHelper {
            if (instance == null) {
                instance = StudentOpenHelper(ctx)
            }
            return instance!!
        }
    }
}