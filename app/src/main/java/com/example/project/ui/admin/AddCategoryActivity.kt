package com.example.project.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.model.Category
import com.example.project.ui.base.BaseActivity

class AddCategoryActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etColor: EditText
    private lateinit var etIcon: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // setHeaderTitle("New Category")

        categoryDAO = CategoryDAO(this)

        etName = findViewById(R.id.etCategoryName)
        etDesc = findViewById(R.id.etCategoryDesc)
        etColor = findViewById(R.id.etCategoryColor)
        etIcon = findViewById(R.id.etCategoryIcon)
        btnSave = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)

        // Set default color/icon if user doesn't type
        etColor.setText("#2196F3")
        etIcon.setText("ic_folder")

        btnSave.setOnClickListener {
            saveCategory()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveCategory() {
        val name = etName.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val color = etColor.text.toString().trim()
        val icon = etIcon.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Category name is required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate Color format (Optional basic check)
        if (!color.startsWith("#") || color.length !in 7..9) {
            Toast.makeText(this, "Invalid color format! Use Hex (e.g. #FF0000)", Toast.LENGTH_SHORT).show()
            return
        }

        val newCategory = Category(
            id = 0,
            name = name,
            description = desc,
            icon = icon,
            color = color
        )

        val result = categoryDAO.addCategory(newCategory)
        if (result > -1) {
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
        }
    }
}