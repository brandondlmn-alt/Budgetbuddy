package com.example.budgetbuddy.ui.auth

import com.example.budgetbuddy.ui.main.MainActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        binding.editUsername.setText(prefs.getString("username", ""))

        binding.btnLogin.setOnClickListener {
            val user = binding.editUsername.text.toString().trim()
            val pass = binding.editPassword.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(user, pass) { userId ->
                if (userId != null) {
                    if (binding.checkRemember.isChecked) {
                        prefs.edit().putString("username", user).apply()
                    }
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    })
                    finish()
                    Toast.makeText(this, "Login successful (UserId: $userId)", Toast.LENGTH_SHORT).show()
                    finish()  // just close the app for now
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}