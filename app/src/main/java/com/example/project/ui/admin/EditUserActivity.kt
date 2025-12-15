package com.example.project.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.UserDAO
import com.example.project.data.model.User

class EditUserActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var spRole: Spinner
    private lateinit var tvCreatedAt: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    private lateinit var userDAO: UserDAO
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        userDAO = UserDAO(this)
        initViews()

        // Nhận user từ Intent
        currentUser = intent.getParcelableExtra("user_item") ?: run {
            Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupEditMode()

        btnUpdate.setOnClickListener { handleSave() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun initViews() {
        edtEmail = findViewById(R.id.etUserEmail)
        edtPassword = findViewById(R.id.etUserPassword)
        edtName = findViewById(R.id.etUserName)
        spRole = findViewById(R.id.spUserRole)
        tvCreatedAt = findViewById(R.id.tvUserCreatedAt)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)

        // Spinner role
        val roles = listOf("user", "admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter
    }

    private fun setupEditMode() {
        edtEmail.setText(currentUser.email)
        edtName.setText(currentUser.name)

        tvCreatedAt.visibility = View.VISIBLE
        tvCreatedAt.text = currentUser.created_at ?: "-"

        val roleIndex = if (currentUser.role == "admin") 1 else 0
        spRole.setSelection(roleIndex)
    }

    private fun handleSave() {
        val email = edtEmail.text.toString().trim()
        val name = edtName.text.toString().trim()
        val role = spRole.selectedItem.toString()
        val newPassword = edtPassword.text.toString().trim()

        if (email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update info (name, email, role)
        val updatedUser = currentUser.copy(
            email = email,
            name = name,
            role = role
        )

        val result = userDAO.updateUserInfo(updatedUser)

        // Nếu có nhập password mới → update password
        if (newPassword.isNotEmpty()) {
            val hashed = com.example.project.data.PasswordHasher.hashPassword(newPassword)
            userDAO.updatePassword(currentUser.id, hashed)
        }

        if (result > 0) {
            Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error updating user", Toast.LENGTH_SHORT).show()
        }
    }
}
