package com.example.project.ui.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.local.UserDAO
import com.example.project.data.model.User
import com.example.project.ui.base.BaseActivity

class UserManagementActivity : BaseActivity() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var btnAdd: ImageView
    private lateinit var etSearch: EditText
    private lateinit var spRoleFilter: Spinner
    private lateinit var adapter: UserAdapter
    private lateinit var userDAO: UserDAO

    private var userList = ArrayList<User>()
    private var filteredList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_user)

        setHeaderTitle("User Management")

        setControl()
        setEvent()

        setupRecyclerView()
        setupRoleFilter()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setControl() {
        rvUsers = findViewById(R.id.rvUsers)
        etSearch = findViewById(R.id.etUserSearch)
        spRoleFilter = findViewById(R.id.spUserRoleFilter)

        userDAO = UserDAO(this)

        // ===== Add Button trên Header (GIỐNG DictionaryManagement) =====
        btnAdd = ImageView(this)
        btnAdd.setImageResource(R.drawable.ic_add)
        btnAdd.setPadding(15, 15, 15, 15)

        val outValue = android.util.TypedValue()
        theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        btnAdd.setBackgroundResource(outValue.resourceId)

        frameRightAction.addView(btnAdd)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            filteredList,
            onEditClick = { user ->
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra("user_item", user)
                startActivity(intent)
            },
            onDeleteClick = { user ->
                confirmDelete(user)
            }
        )

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
    }

    private fun loadData() {
        userList = userDAO.getAllUsers()
        applyFilterAndSearch()
    }

    private fun confirmDelete(user: User) {
        if (user.id == userDAO.getCurrentUserId()) {
            Toast.makeText(this, "You cannot delete your own account!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete '${user.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                if (userDAO.deleteUser(user.id) > 0) {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupRoleFilter() {
        val roles = listOf("All", "user", "admin")
        val adapterSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spRoleFilter.adapter = adapterSpinner
        spRoleFilter.setSelection(0)

        spRoleFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    applyFilterAndSearch()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                applyFilterAndSearch()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applyFilterAndSearch() {
        val searchText = etSearch.text.toString().trim().lowercase()
        val selectedRole = spRoleFilter.selectedItem.toString()

        filteredList = userList.filter { user ->
            val matchesRole =
                selectedRole == "All" || user.role == selectedRole
            val matchesSearch =
                user.name.lowercase().contains(searchText) ||
                        user.email.lowercase().contains(searchText)
            matchesRole && matchesSearch
        } as ArrayList<User>

        adapter.updateData(filteredList)
    }
}
