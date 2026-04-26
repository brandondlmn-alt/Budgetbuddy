package com.example.budgetbuddy.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.data.entity.Expense
import kotlinx.coroutines.launch

class ExpenseListViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database
    val expenses = MutableLiveData<List<Expense>>()

    fun loadExpenses(userId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            expenses.value = db.expenseDao().getExpensesByDateRange(userId, startDate, endDate)
        }
    }
}