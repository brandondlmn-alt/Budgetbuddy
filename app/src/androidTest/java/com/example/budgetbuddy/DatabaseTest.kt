package com.example.budgetbuddy

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetbuddy.data.database.AppDatabase
import com.example.budgetbuddy.data.entity.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUserAndGetByUsername() = runBlocking {
        val user = User(username = "testUser", passwordHash = "hashed")
        db.userDao().insertUser(user)
        val fetched = db.userDao().getUserByUsername("testUser")
        assertNotNull(fetched)
        assertEquals("testUser", fetched?.username)
    }

    @Test
    fun insertCategoryAndRetrieve() = runBlocking {
        db.userDao().insertUser(User(username = "u1", passwordHash = "pw"))
        val cat = Category(name = "Groceries", userId = 1, colorCode = "#FF0000", iconText = "G")
        db.categoryDao().insertCategory(cat)
        val categories = db.categoryDao().getCategoriesByUser(1)
        assertEquals(1, categories.size)
        assertEquals("Groceries", categories[0].name)
    }

    @Test
    fun insertExpenseAndRetrieveByDateRange() = runBlocking {
        db.userDao().insertUser(User(username = "u1", passwordHash = "pw"))
        db.categoryDao().insertCategory(Category(name = "Food", userId = 1, colorCode = "#00FF00", iconText = "F"))
        val expense = Expense(
            userId = 1, categoryId = 1, amount = 250.0,
            date = "2025-03-15", startTime = "08:00", endTime = "08:30",
            description = "Breakfast", photoPath = null, originalCurrency = null,
            homeCurrencyAmount = 250.0
        )
        db.expenseDao().insertExpense(expense)
        val list = db.expenseDao().getExpensesByDateRange(1, "2025-03-01", "2025-03-31")
        assertEquals(1, list.size)
        assertEquals(250.0, list[0].amount, 0.01)
    }

    @Test
    fun getTotalSpent_returnsCorrectSum() = runBlocking {
        db.userDao().insertUser(User(username = "u1", passwordHash = "pw"))
        db.categoryDao().insertCategory(Category(name = "Food", userId = 1, colorCode = "#00FF00", iconText = "F"))
        db.expenseDao().insertExpense(Expense(userId = 1, categoryId = 1, amount = 100.0, date = "2025-06-10", startTime = "10:00", endTime = "10:30", description = "snack", photoPath = null, originalCurrency = null, homeCurrencyAmount = 100.0))
        db.expenseDao().insertExpense(Expense(userId = 1, categoryId = 1, amount = 200.0, date = "2025-06-10", startTime = "11:00", endTime = "11:30", description = "lunch", photoPath = null, originalCurrency = null, homeCurrencyAmount = 200.0))
        val total = db.expenseDao().getTotalSpent(1, "2025-06-01", "2025-06-30")
        assertEquals(300.0, total ?: 0.0, 0.01)
    }

    @Test
    fun goalInsertAndRetrieve() = runBlocking {
        db.userDao().insertUser(User(username = "u1", passwordHash = "pw"))
        val goal = Goal(userId = 1, month = "2025-07", minAmount = 1000.0, maxAmount = 5000.0)
        db.goalDao().insertGoal(goal)
        val fetched = db.goalDao().getGoalForUserAndMonth(1, "2025-07")
        assertNotNull(fetched)
        assertEquals(1000.0, fetched!!.minAmount, 0.01)
    }
}