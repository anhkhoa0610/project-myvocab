package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.PasswordHasher
import com.example.project.data.model.User
import com.example.project.utils.UserSession
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
        
        // Hash password before storing
        val hashedPassword = PasswordHasher.hashPassword(password)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_EMAIL, email)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, hashedPassword)  // Lưu password đã hash
            put(DatabaseHelper.COLUMN_USER_NAME, name)
            put(DatabaseHelper.COLUMN_USER_ROLE, role)
            put(DatabaseHelper.COLUMN_USER_CREATED_AT, currentTime)
        }

        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        return result
    }

    // Login - Check email & password
    // File: com.example.project.data.local.UserDAO.kt (HÀM LOGIN ĐÃ SỬA)

    // File: com.example.project.data.local.UserDAO.kt (HÀM LOGIN ĐÃ SỬA HOÀN TOÀN)

    fun login(email: String, password: String): User? {
        val db = dbHelper.readableDatabase

        // --- BỔ SUNG: TRIM EMAIL VÀ PASSWORD NHẬP VÀO ---
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        // 1. CHỈ TRUY VẤN THEO EMAIL ĐÃ ĐƯỢC TRIM
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(trimmedEmail) // Sử dụng trimmedEmail
        )

        var user: User? = null
        if (cursor.moveToFirst()) {

            val storedHash = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD))

            // 2. XÁC MINH: DÙNG trimmedPassword VỚI HASH ĐÃ LƯU
            // Nếu mật khẩu đã được lưu mà không có khoảng trắng, thì việc trim này đảm bảo tính nhất quán.
            if (PasswordHasher.verifyPassword(trimmedPassword, storedHash)) { // <-- SỬ DỤNG trimmedPassword
                user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                    password = storedHash,
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)) ?: "",
                    role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)) ?: "user",
                    created_at = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_CREATED_AT)) ?: ""
                )

                // LƯU SESSION
                UserSession.saveUser(
                    context,
                    user.id,
                    user.email,
                    user.name,
                    user.role
                )
            }
            // Nếu xác minh thất bại, user vẫn là null
        }
        cursor.close()
        db.close()
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

        // User account (password will be hashed automatically by register())
        if (!isEmailExists("user@test.com")) {
            val userId = register("user@test.com", "123456", "Test User", "user")
            if (userId > 0) {
                userStatsDAO.createStats(userId.toInt())
            }
        }

        // Admin account (password will be hashed automatically by register())
        if (!isEmailExists("admin@test.com")) {
            val userId = register("admin@test.com", "123456", "Admin User", "admin")
            if (userId > 0) {
                userStatsDAO.createStats(userId.toInt())
            }
        }
    }
    fun getCurrentUserId(): Int {
        // Trả về ID người dùng đang đăng nhập. Nếu chưa đăng nhập, trả về 0 (hoặc giá trị mặc định)
        return UserSession.getUserId(context)
    }

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ ĐỔI MẬT KHẨU (Giữ nguyên) ---

    fun getHashedPasswordByUserId(userId: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_USER_PASSWORD} FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )

        var storedValue: String? = null
        if (cursor.moveToFirst()) {
            storedValue = cursor.getString(0)
        }
        cursor.close()
        db.close()

        if (storedValue == null) {
            return null
        }

        val isHashed = storedValue.startsWith("$2a") || storedValue.startsWith("$2b")

        if (!isHashed) {
            val newHashedPassword = PasswordHasher.hashPassword(storedValue)
            val success = updatePassword(userId, newHashedPassword)

            if (success) {
                return newHashedPassword
            } else {
                return null
            }
        }
        return storedValue
    }

    fun updatePassword(userId: Int, newHashedPassword: String): Boolean {
        // ... (Logic cập nhật hash vào DB) ...
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_PASSWORD, newHashedPassword)
        }
        val rowsAffected = db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return rowsAffected > 0
    }
    fun getUserCountLast7Days(): List<Int> {
        val result = MutableList(7) { 0 }
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT DATE(created_at) as day, COUNT(*) 
        FROM users
        WHERE created_at >= date('now', '-6 day')
        GROUP BY DATE(created_at)
        ORDER BY day
        """,
            null
        )

        var index = 7 - cursor.count
        while (cursor.moveToNext()) {
            result[index++] = cursor.getInt(1)
        }

        cursor.close()
        return result
    }
    fun getLatestUserName(): String? {
        val db = dbHelper.readableDatabase
        var name: String? = null

        val cursor = db.rawQuery(
            """
        SELECT ${DatabaseHelper.COLUMN_USER_NAME}
        FROM ${DatabaseHelper.TABLE_USERS}
        ORDER BY ${DatabaseHelper.COLUMN_USER_CREATED_AT} DESC
        LIMIT 1
        """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }

        cursor.close()
        db.close()
        return name
    }

    fun getAllUsers(): ArrayList<User> {
        val userList = ArrayList<User>()
        val db = dbHelper.readableDatabase
        // Sắp xếp theo ngày tạo mới nhất trước
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USERS} ORDER BY ${DatabaseHelper.COLUMN_USER_CREATED_AT} DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                    password = "", // Không cần load hash password ra list để hiển thị
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)) ?: "",
                    role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)) ?: "user",
                    created_at = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_CREATED_AT)) ?: ""
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    // 2. Cập nhật thông tin User (Tên, Role) - Không đổi pass ở đây
    fun updateUserInfo(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_NAME, user.name)
            put(DatabaseHelper.COLUMN_USER_EMAIL, user.email)
            put(DatabaseHelper.COLUMN_USER_ROLE, user.role)
        }
        val rows = db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(user.id.toString())
        )
        db.close()
        return rows
    }

    // 3. Xóa User
    fun deleteUser(userId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_USERS,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return result
    }


}
