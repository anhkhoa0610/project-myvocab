package com.example.project.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.UserDAO
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initData()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun initData() {
        userDAO = UserDAO(this)
        
        // Seed all data (chạy 1 lần khi app khởi động)
        seedAllData()
    }
    
    private fun seedAllData() {
        // 1. Seed levels (required first)
        val levelDAO = com.example.project.data.local.LevelDAO(this)
        levelDAO.seedLevels()
        
        // 2. Seed categories
        val categoryDAO = com.example.project.data.local.CategoryDAO(this)
        categoryDAO.seedDefaultCategories()
        
        // 3. Seed users
        userDAO.seedDefaultAccounts()
        
        // 4. Seed dictionary words (requires levels & categories)
        val dictionaryDAO = com.example.project.data.local.DictionaryWordDAO(this)
        dictionaryDAO.seedSampleWords()
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                login(email, password)
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun login(email: String, password: String) {
        val user = userDAO.login(email, password)
        
        if (user != null) {
            // Login thành công
            val message = if (user.isAdmin()) {
                "Welcome Admin: ${user.name}!"
            } else {
                "Welcome: ${user.name}!"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            
            // Save user session
            com.example.project.utils.UserSession.saveUser(this, user.id, user.email, user.name, user.role)
            
            // Navigate to Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // Login thất bại
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show()
            etPassword.error = "Invalid credentials"
            etPassword.requestFocus()
        }
    }
}
