package com.example.budgetbuddy.ui.main.viewmodels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.*
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.ui.main.views.PieChartView
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database
    private val _state = MutableLiveData<DashboardData>()
    val state: LiveData<DashboardData> = _state

    data class DashboardData(
        val maxBudget: Double = 0.0,
        val totalSpent: Double = 0.0,
        val categoryProgresses: List<CategoryProgress> = emptyList(),
        val pieSlices: List<PieChartView.Slice> = emptyList()
    )

    data class CategoryProgress(
        val categoryName: String,
        val spent: Double,
        val limit: Double?,
        val percentage: Int,
        val color: Int
    )

    fun load(userId: Int) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val monthKey = "$year-${String.format("%02d", month)}"
            val start = "$monthKey-01"
            val end = "$monthKey-31"

            val goal = db.goalDao().getGoalForUserAndMonth(userId, monthKey)
            val totalSpent = db.expenseDao().getTotalSpent(userId, start, end) ?: 0.0
            val categories = db.categoryDao().getCategoriesByUser(userId)

            val categoryProgresses = mutableListOf<CategoryProgress>()
            val pieSlices = mutableListOf<PieChartView.Slice>()

            for (cat in categories) {
                val spent = db.expenseDao().getCategoryTotal(userId, cat.id, start, end) ?: 0.0
                val limit = goal?.maxAmount // overall max, or you can have per-category limits later
                val percent = if (limit != null && limit > 0) (spent / limit * 100).toInt() else 0
                val catColor = try { Color.parseColor(cat.colorCode) } catch (e: Exception) { Color.GRAY }

                categoryProgresses.add(
                    CategoryProgress(cat.name, spent, limit, percent, catColor)
                )
                if (spent > 0) {
                    pieSlices.add(PieChartView.Slice(cat.name, spent.toFloat(), catColor))
                }
            }

            _state.postValue(
                DashboardData(
                    maxBudget = goal?.maxAmount ?: 0.0,
                    totalSpent = totalSpent,
                    categoryProgresses = categoryProgresses,
                    pieSlices = pieSlices
                )
            )
        }
    }
}