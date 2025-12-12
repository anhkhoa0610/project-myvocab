package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.User
import java.text.SimpleDateFormat
import java.util.*

class UserDAO(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Register user mới
    fun register(email: String, password: String, name: String, role: String = "user"): Long {
        // Check email đã tồn tại chưa
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
        return user
    }

    // Seed default accounts
    fun seedDefaultAccounts() {
        val userStatsDAO = UserStatsDAO(context)
        
        // User account
        if (!isEmailExists("user@test.com")) {
            val userId = register("user@test.com", "123456", "Test User", "user")
            if (userId > 0) {
                userStatsDAO.createStats(userId.toInt())
            }
        }
        
        // Admin account
        if (!isEmailExists("admin@test.com")) {
            val userId = register("admin@test.com", "123456", "Admin User", "admin")
            if (userId > 0) {
                userStatsDAO.createStats(userId.toInt())
            }
        }
    }
}
