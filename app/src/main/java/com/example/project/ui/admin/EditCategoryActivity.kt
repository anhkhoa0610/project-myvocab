package com.example.project.ui.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.model.Category
import com.example.project.ui.base.BaseActivity

class EditCategoryActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etColor: EditText
    private lateinit var etIcon: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    private lateinit var categoryDAO: CategoryDAO
    private var currentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        categoryDAO = CategoryDAO(this)

        etName = findViewById(R.id.etCategoryName)
        etDesc = findViewById(R.id.etCategoryDesc)
        etColor = findViewById(R.id.etCategoryColor)
        etIcon = findViewById(R.id.etCategoryIcon)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)

        loadData()

        btnUpdate.setOnClickListener {
            updateCategory()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        val category = intent.getParcelableExtra<Category>("category")

        category?.let {
            currentId = it.id
            etName.setText(it.name)
            etDesc.setText(it.description)
            etColor.setText(it.color)
            etIcon.setText(it.icon)
        }
    }

    private fun updateCategory() {
        val name = etName.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val color = etColor.text.toString().trim()
        val icon = etIcon.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Category name is required!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedCategory = Category(
            id = currentId,
            name = name,
            description = desc,
            icon = icon,
            color = color
        )

        val result = categoryDAO.updateCategory(updatedCategory)
        if (result > 0) {
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show()
        }
    }
}