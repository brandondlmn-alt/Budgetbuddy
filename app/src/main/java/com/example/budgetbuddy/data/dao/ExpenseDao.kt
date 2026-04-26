package com.example.budgetbuddy.data.dao

import androidx.room.*
import com.example.budgetbuddy.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, startTime DESC")
    suspend fun getExpensesByDateRange(userId: Int, startDate: String, endDate: String): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    suspend fun getCategoryTotal(userId: Int, categoryId: Int, startDate: String, endDate: String): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSpent(userId: Int, startDate: String, endDate: String): Double?

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    suspend fun getTotalExpenseCount(userId: Int): Int

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId AND photoPath IS NOT NULL")
    suspend fun getReceiptCount(userId: Int): Int
}