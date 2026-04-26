package com.example.budgetbuddy

import android.app.Application
import com.example.budgetbuddy.data.database.DatabaseProvider

class BudgetBuddyApplication : Application() {
    val database by lazy { DatabaseProvider.getDatabase(this) }
}