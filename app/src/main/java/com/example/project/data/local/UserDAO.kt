package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.User
import java.text.SimpleDateFormat
import java.util.*

class UserDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Register user mới
    fun register(email: String, password: String, name: String, role: String = "user"): Long {
        // Check email đã tồn tại chưa (KHÔNG close db ở đây)
        if (isEmailExists(email)) {
            return -1  // Email đã tồn tại
        }
        
        val db = dbHelper.writableDatabase
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_EMAIL, email)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, password)
            put(DatabaseHelper.COLUMN_USER_NAME, name)
            put(DatabaseHelper.COLUMN_USER_ROLE, role)
            put(DatabaseHelper.COLUMN_USER_CREATED_AT, currentTime)
        }
        
        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        // KHÔNG close db ở đây, để DatabaseHelper quản lý
        return result
    }

    // Login - Check email & password
    fun login(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL} = ? AND ${DatabaseHelper.COLUMN_USER_PASSWORD} = ?",
            arrayOf(email, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)) ?: "",
                role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)) ?: "user",
                created_at = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_CREATED_AT)) ?: ""
            )
        }
        cursor.close()
        // KHÔNG close db
        return user
    }

    // Check email đã tồn tại chưa
    fun isEmailExists(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(email)
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        // KHÔNG close db
        return count > 0
    }

    // Lấy user theo ID
    fun getUserById(id: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(id.toString())
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)) ?: "",
                role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)) ?: "user",
                created_at = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_CREATED_AT)) ?: ""
            )
        }
        cursor.close()
        // KHÔNG close db
        return user
    }

    // Seed default accounts
    fun seedDefaultAccounts() {
        // User account
        if (!isEmailExists("user@test.com")) {
            register("user@test.com", "123456", "Test User", "user")
        }
        
        // Admin account
        if (!isEmailExists("admin@test.com")) {
            register("admin@test.com", "123456", "Admin User", "admin")
        }
    }
}
