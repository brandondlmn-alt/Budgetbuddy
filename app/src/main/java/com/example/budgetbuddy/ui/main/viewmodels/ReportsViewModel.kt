package com.example.budgetbuddy.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.data.entity.Category
import kotlinx.coroutines.launch

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database
    val categoryTotals = MutableLiveData<List<Pair<Category, Double>>>()

    fun loadTotals(userId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            val categories = db.categoryDao().getCategoriesByUser(userId)
            val list = mutableListOf<Pair<Category, Double>>()
            for (cat in categories) {
                val total = db.expenseDao().getCategoryTotal(userId, cat.id, startDate, endDate) ?: 0.0
                list.add(cat to total)
            }
            categoryTotals.value = list
        }
    }
}