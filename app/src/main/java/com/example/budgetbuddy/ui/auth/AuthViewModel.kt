package com.example.budgetbuddy.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.data.entity.Category
import com.example.budgetbuddy.data.entity.User
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BudgetBuddyApplication).database

    fun login(username: String, password: String, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            val user = db.userDao().getUserByUsername(username)
            if (user != null && user.passwordHash == hashPassword(password)) {
                insertDefaultCategories(user.id)   // ensure defaults exist
                onResult(user.id)
            } else {
                onResult(null)
            }
        }
    }

    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existing = db.userDao().getUserByUsername(username)
            if (existing != null) {
                onResult(false)
            } else {
                val newUser = User(username = username, passwordHash = hashPassword(password))
                val newUserId = db.userDao().insertUser(newUser).toInt()
                insertDefaultCategories(newUserId)
                onResult(true)
            }
        }
    }

    private fun insertDefaultCategories(userId: Int) {
        viewModelScope.launch {
            val existing = db.categoryDao().getCategoriesByUser(userId)
            if (existing.isEmpty()) {
                val defaults = listOf(
                    Category(name = "Transport", userId = userId, colorCode = "#FF9800", iconText = "T"),
                    Category(name = "Rent", userId = userId, colorCode = "#E53935", iconText = "R"),
                    Category(name = "Groceries", userId = userId, colorCode = "#4CAF50", iconText = "G"),
                    Category(name = "Entertainment", userId = userId, colorCode = "#9C27B0", iconText = "E"),
                    Category(name = "Dining", userId = userId, colorCode = "#FFC107", iconText = "D")
                )
                for (cat in defaults) {
                    db.categoryDao().insertCategory(cat)
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}