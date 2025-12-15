package com.example.project.ui.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.model.Category
import com.example.project.ui.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManageCategoryActivity : BaseActivity(), CategoryAdapter.OnCategoryActionListener {

    private var categoryList = ArrayList<Category>()
    private var filteredList = ArrayList<Category>()
    private lateinit var adapter: CategoryAdapter
    private lateinit var listView: ListView
    private lateinit var etSearch: EditText
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var categoryDAO: CategoryDAO

    private val addCategoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
            Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show()
        }
    }

    private val editCategoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        setControl()
        initData()
        setEvent()
    }

    private fun setControl() {
        setHeaderTitle("Manage Categories")
        listView = findViewById(R.id.lvCategories)
        etSearch = findViewById(R.id.etSearchCategory)
        fabAdd = findViewById(R.id.fabAddCategory)
    }

    private fun initData() {
        categoryDAO = CategoryDAO(this)
        refreshData()
    }

    private fun setEvent() {
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            addCategoryLauncher.launch(intent)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }
        })
    }

    private fun refreshData() {
        categoryList = categoryDAO.getAllCategories()
        val currentSearchText = etSearch.text.toString()
        filterList(currentSearchText)
    }

    private fun filterList(query: String) {
        val searchQuery = query.trim().lowercase()
        filteredList.clear()

        if (searchQuery.isEmpty()) {
            filteredList.addAll(categoryList)
        } else {
            for (cat in categoryList) {
                if (cat.name.lowercase().contains(searchQuery) ||
                    cat.description.lowercase().contains(searchQuery)) {
                    filteredList.add(cat)
                }
            }
        }
        bindData(filteredList)
    }

    private fun bindData(list: ArrayList<Category>) {
        adapter = CategoryAdapter(this, list, this)
        listView.adapter = adapter
    }

    override fun onEditClick(category: Category) {
        val intent = Intent(this, EditCategoryActivity::class.java)
        intent.putExtra("category", category)
        editCategoryLauncher.launch(intent)
    }

    override fun onDeleteClick(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Xóa danh mục")
            .setMessage("Bạn có chắc muốn xóa \"${category.name}\"?")
            .setPositiveButton("Xóa") { _, _ ->
                val result = categoryDAO.deleteCategory(category.id)
                if (result > 0) {
                    Toast.makeText(this, "Đã xóa!", Toast.LENGTH_SHORT).show()
                    refreshData()
                } else {
                    Toast.makeText(this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}