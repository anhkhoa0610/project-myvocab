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

        setControl()
        initData()
        setEvent()
    }

    private fun setControl() {
        setHeaderTitle("Add Category\n Minh Nhựt - Nhóm 2")

        etName = findViewById(R.id.etCategoryName)
        etDesc = findViewById(R.id.etCategoryDesc)
        etColor = findViewById(R.id.etCategoryColor)
        etIcon = findViewById(R.id.etCategoryIcon)
        btnSave = findViewById(R.id.btnAdd)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun initData() {
        categoryDAO = CategoryDAO(this)
        etColor.setText("#2196F3")
        etIcon.setText("ic_folder")
    }

    private fun setEvent() {
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
            Toast.makeText(this, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!color.startsWith("#") || color.length !in 7..9) {
            Toast.makeText(this, "Mã màu không hợp lệ! Hãy dùng mã Hex (vd: #FF0000)", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Lỗi: Không thể thêm danh mục", Toast.LENGTH_SHORT).show()
        }
    }
}