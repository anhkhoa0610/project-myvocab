package com.example.project.ui.rename

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.project.R
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.UserSession
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RenameActivity : BaseActivity() {

    // ================= CONTROL =================
    private lateinit var edtUsername: TextInputEditText
    private lateinit var btnSave: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rename)

        // ActionBar
        setHeaderTitle("Đổi tên")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setControl()
        setEvent()
    }

    // ================= ACTION BAR BACK =================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // ================= SET CONTROL =================
    private fun setControl() {
        edtUsername = findViewById(R.id.edtUsername)
        btnSave = findViewById(R.id.btnSaveUsername)

        // Hiển thị tên hiện tại
        edtUsername.setText(UserSession.getUserName(this))
    }

    // ================= SET EVENT =================
    private fun setEvent() {
        btnSave.setOnClickListener {
            val newName = edtUsername.text.toString().trim()

            // Validate
            if (newName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lưu tên
            UserSession.setUserName(this, newName)

            Toast.makeText(this, "Đã cập nhật tên", Toast.LENGTH_SHORT).show()

            // Quay về Settings
            finish()
        }
    }
}
