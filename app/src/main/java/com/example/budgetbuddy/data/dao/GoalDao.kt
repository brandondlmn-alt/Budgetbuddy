package com.example.budgetbuddy.data.dao

import androidx.room.*
import com.example.budgetbuddy.data.entity.Goal

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId AND month = :month LIMIT 1")
    suspend fun getGoalForUserAndMonth(userId: Int, month: String): Goal?

    @Query("SELECT * FROM goals WHERE userId = :userId")
    suspend fun getAllGoals(userId: Int): List<Goal>
}