package com.example.project.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.UserDAO
import com.example.project.data.local.UserStatsDAO
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setControl()
        setEvent()
    }

    private fun setControl() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        
        userDAO = UserDAO(this)
    }

    private fun setEvent() {
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword)) {
                register(name, email, password)
            }
        }

        tvLogin.setOnClickListener {
            finish() // Quay về LoginActivity
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        // Validate name
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return false
        }

        if (name.length < 3) {
            etName.error = "Name must be at least 3 characters"
            etName.requestFocus()
            return false
        }

        // Validate email
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

        // Check email exists
        if (userDAO.isEmailExists(email)) {
            etEmail.error = "Email already registered"
            etEmail.requestFocus()
            Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate password
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

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun register(name: String, email: String, password: String) {
        // 1. Register user
        val userId = userDAO.register(email, password, name, "user")
        
        if (userId > 0) {
            // 2. Create user_stats for new user
            val userStatsDAO = UserStatsDAO(this)
            userStatsDAO.createStats(userId.toInt())
            
            // 3. Success message
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
            
            // 4. Quay về LoginActivity
            finish()
        } else {
            // Register thất bại
            Toast.makeText(this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
