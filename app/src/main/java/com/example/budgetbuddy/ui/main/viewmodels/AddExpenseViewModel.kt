package com.example.budgetbuddy.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.data.entity.Category
import com.example.budgetbuddy.data.entity.Expense
import kotlinx.coroutines.launch

class AddExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database
    val categories = MutableLiveData<List<Category>>()
    var photoPath: String? = null

    fun loadCategories(userId: Int) {
        viewModelScope.launch {
            categories.value = db.categoryDao().getCategoriesByUser(userId)
        }
    }

    fun saveExpense(userId: Int, categoryId: Int, amount: Double, date: String, start: String, end: String,
                    desc: String, originalCurrency: String?, homeAmount: Double) {
        viewModelScope.launch {
            val expense = Expense(
                userId = userId, categoryId = categoryId,
                amount = homeAmount, date = date, startTime = start, endTime = end,
                description = desc, photoPath = photoPath,
                originalCurrency = originalCurrency, homeCurrencyAmount = homeAmount
            )
            db.expenseDao().insertExpense(expense)
        }
    }
}