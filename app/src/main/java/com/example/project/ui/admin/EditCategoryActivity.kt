package com.example.project.ui.admin

import android.os.Build
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

        setControl()
        initData()
        setEvent()
    }

    private fun setControl() {
        setHeaderTitle("Edit Category\n Minh Nhựt - Nhóm 2")

        etName = findViewById(R.id.etCategoryName)
        etDesc = findViewById(R.id.etCategoryDesc)
        etColor = findViewById(R.id.etCategoryColor)
        etIcon = findViewById(R.id.etCategoryIcon)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun initData() {
        categoryDAO = CategoryDAO(this)

        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("category", Category::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("category")
        }

        if (category != null) {
            currentId = category.id
            bindData(category)
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy danh mục", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun bindData(category: Category) {
        etName.setText(category.name)
        etDesc.setText(category.description)
        etColor.setText(category.color)
        etIcon.setText(category.icon)
    }

    private fun setEvent() {
        btnUpdate.setOnClickListener {
            updateCategory()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun updateCategory() {
        val name = etName.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val color = etColor.text.toString().trim()
        val icon = etIcon.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Lỗi: Không thể cập nhật danh mục", Toast.LENGTH_SHORT).show()
        }
    }
}