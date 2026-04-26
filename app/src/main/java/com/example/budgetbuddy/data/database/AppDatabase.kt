package com.example.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.budgetbuddy.data.entity.*
import com.example.budgetbuddy.data.dao.*

@Database(
    entities = [User::class, Category::class, Expense::class, Goal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun goalDao(): GoalDao
}