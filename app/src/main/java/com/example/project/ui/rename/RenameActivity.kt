package com.example.project.ui.rename

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.R
import com.example.project.utils.UserSession
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RenameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rename)

        val edtUsername = findViewById<TextInputEditText>(R.id.edtUsername)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveUsername)

        // 1️⃣ Hiển thị tên hiện tại
        edtUsername.setText(UserSession.getUserName(this))

        // 2️⃣ Bấm Lưu
        btnSave.setOnClickListener {
            val newName = edtUsername.text.toString().trim()

            // Validate
            if (newName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3️⃣ Lưu tên
            UserSession.setUserName(this, newName)

            Toast.makeText(this, "Đã cập nhật tên", Toast.LENGTH_SHORT).show()

            // 4️⃣ Quay về (BaseActivity sẽ tự update hamburger)
            finish()
        }
    }
}
