package com.example.budgetbuddy.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "budgetbuddy_db"
            )
                .fallbackToDestructiveMigration()
                .build().also { db = it }
        }
    }
}