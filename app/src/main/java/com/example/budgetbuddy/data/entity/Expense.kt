package com.example.budgetbuddy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val date: String,           // YYYY-MM-DD
    val startTime: String,      // HH:mm
    val endTime: String,        // HH:mm
    val description: String,
    val photoPath: String? = null,
    val originalCurrency: String? = null,
    val homeCurrencyAmount: Double
)