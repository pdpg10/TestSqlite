package com.example.testsqlite.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Student(
    val name: String = "",
    val age: Int = 16,
    val course: Int = 1,
    val id: Long = 0
) : Parcelable