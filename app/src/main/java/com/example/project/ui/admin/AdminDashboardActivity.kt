package com.example.project.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.project.R
import com.example.project.ui.base.BaseActivity

class AdminDashboardActivity : BaseActivity() {

    private lateinit var cardManageWords: CardView
    private lateinit var cardManageCategories: CardView
    private lateinit var cardManageLevels: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Check if user is admin
        val userRole = com.example.project.utils.UserSession.getUserRole(this)
        if (userRole != "admin") {
            Toast.makeText(this, "Bạn không có quyền truy cập trang này", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setHeaderTitle("Admin Dashboard")
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        cardManageWords = findViewById(R.id.cardManageWords)
        cardManageCategories = findViewById(R.id.cardManageCategories)
        cardManageLevels = findViewById(R.id.cardManageLevels)
    }

    private fun setupClickListeners() {
        cardManageWords.setOnClickListener {
            val intent = Intent(this, DictionaryManagementActivity::class.java)
            startActivity(intent)
        }

        cardManageCategories.setOnClickListener {
            val intent = Intent(this, ManageCategoryActivity::class.java)
            startActivity(intent)
        }

        cardManageLevels.setOnClickListener {
            // TODO: Navigate to Manage Levels Activity
            Toast.makeText(this, "Quản lý level - Coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
