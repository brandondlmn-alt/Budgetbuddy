package com.example.budgetbuddy.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.data.entity.Goal
import kotlinx.coroutines.launch
import java.util.*

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database

    fun loadGoal(userId: Int, onLoaded: (Goal?) -> Unit) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val monthKey = "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"
            val goal = db.goalDao().getGoalForUserAndMonth(userId, monthKey)
            onLoaded(goal)
        }
    }

    fun saveGoal(userId: Int, min: Double, max: Double, existingGoal: Goal? = null) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val monthKey = "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"
            if (existingGoal != null) {
                db.goalDao().updateGoal(existingGoal.copy(minAmount = min, maxAmount = max))
            } else {
                db.goalDao().insertGoal(Goal(userId = userId, month = monthKey, minAmount = min, maxAmount = max))
            }
        }
    }
}