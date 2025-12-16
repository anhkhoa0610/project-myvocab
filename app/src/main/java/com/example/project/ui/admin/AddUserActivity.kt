package com.example.project.ui.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.data.local.UserDAO

class AddUserActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var spRole: Spinner
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var tvTitle: TextView

    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        userDAO = UserDAO(this)
        setControl()
        setEvent()
    }

    private fun setControl() {
        edtEmail = findViewById(R.id.etUserEmail)
        edtPassword = findViewById(R.id.etUserPassword)
        edtName = findViewById(R.id.etUserName)
        spRole = findViewById(R.id.spUserRole)
        btnAdd = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)
        tvTitle = findViewById(R.id.tvTitle)

        tvTitle.text = "Add User"

        val roles = listOf("user", "admin")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter
        spRole.setSelection(0)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener { handleAddUser() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun handleAddUser() {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val name = edtName.text.toString().trim()
        val role = spRole.selectedItem.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        val result = userDAO.register(
            email = email,
            password = password,
            name = name,
            role = role
        )

        when {
            result == -1L -> {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
            }
            result > 0 -> {
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            else -> {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
        }
    }
}