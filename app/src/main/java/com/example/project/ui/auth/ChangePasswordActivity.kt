// File: com.example.project.ui.auth.ChangePasswordActivity.kt (ĐÃ HOÀN CHỈNH)

package com.example.project.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.project.R
import com.example.project.data.PasswordHasher
import com.example.project.data.local.UserDAO
import com.example.project.ui.base.BaseActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordActivity : BaseActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tilOldPassword: TextInputLayout
    private lateinit var tilNewPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        setHeaderTitle("Nhóm 2\nThanh Kiệt - Change Password")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        userDAO = UserDAO(this)
        setControl()
        setEvent()
    }

    // Xử lý nút Back trên ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setControl() {
        etOldPassword = findViewById(R.id.et_old_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        tilOldPassword = findViewById(R.id.til_old_password)
        tilNewPassword = findViewById(R.id.til_new_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
    }

    private fun setEvent() {
        btnChangePassword.setOnClickListener {
            // Xóa mọi thông báo lỗi cũ khi click nút
            clearAllErrors()
            handleChangePassword()
        }
    }

    private fun clearAllErrors() {
        tilOldPassword.error = null
        tilNewPassword.error = null
        tilConfirmPassword.error = null
    }

    private fun handleChangePassword() {
        val oldPass = etOldPassword.text.toString().trim()
        val newPass = etNewPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        val currentUserId = userDAO.getCurrentUserId()

        // --- BỔ SUNG KIỂM TRA PHIÊN NGƯỜI DÙNG ---
        if (currentUserId == 0) {
            Toast.makeText(this, "Lỗi phiên người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 1. Kiểm tra xác thực đầu vào
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 6) {
            tilNewPassword.error = "Mật khẩu mới phải có ít nhất 6 ký tự."
            return
        }

        if (newPass != confirmPass) {
            tilConfirmPassword.error = "Mật khẩu xác nhận không khớp."
            return
        }

        // Tránh trường hợp người dùng nhập mật khẩu mới trùng với mật khẩu cũ
        if (oldPass == newPass) {
            tilNewPassword.error = "Mật khẩu mới phải khác mật khẩu cũ."
            return
        }


        // 2. Xác minh Mật khẩu cũ
        val storedHash = userDAO.getHashedPasswordByUserId(currentUserId)

        // Nếu storedHash null (lỗi DB) hoặc xác minh thất bại
        if (storedHash == null || !PasswordHasher.verifyPassword(oldPass, storedHash)) {
            tilOldPassword.error = "Mật khẩu cũ không chính xác."
            return
        }

        // 3. Mã hóa Mật khẩu mới và Cập nhật
        val newHashedPassword = PasswordHasher.hashPassword(newPass)

        val success = userDAO.updatePassword(currentUserId, newHashedPassword)

        if (success) {
            Toast.makeText(this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_LONG).show()
            finish() // Quay lại màn hình Settings
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật mật khẩu. Vui lòng thử lại.", Toast.LENGTH_LONG).show()
        }
    }
}