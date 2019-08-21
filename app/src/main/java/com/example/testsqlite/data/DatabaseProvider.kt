package com.example.testsqlite.data

import android.content.Context
import androidx.room.Room

class DatabaseProvider {
    companion object {
        private var instance: Database? = null
        fun instance(ctx: Context): Database {
            if (instance == null) {
                instance = Room
//                    .inMemoryDatabaseBuilder(ctx,Database::class.java)
                    .databaseBuilder(ctx, Database::class.java, "store.db")
                    .build()
            }
            return instance!!
        }
    }
}