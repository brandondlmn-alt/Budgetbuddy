package com.example.budgetbuddy.ui.main
import com.example.budgetbuddy.ui.main.fragments.DashboardFragment
import com.example.budgetbuddy.ui.main.fragments.ExpenseListFragment
import com.example.budgetbuddy.ui.main.fragments.AddExpenseFragment
import com.example.budgetbuddy.ui.main.fragments.ReportsFragment
import com.example.budgetbuddy.ui.main.fragments.MoreFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.ActivityMainBinding
import com.example.budgetbuddy.ui.main.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment.newInstance(userId))
                R.id.nav_expenses -> loadFragment(ExpenseListFragment.newInstance(userId))
                R.id.nav_add -> loadFragment(AddExpenseFragment.newInstance(userId))
                R.id.nav_reports -> loadFragment(ReportsFragment.newInstance(userId))
                R.id.nav_more -> loadFragment(MoreFragment.newInstance(userId))
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}