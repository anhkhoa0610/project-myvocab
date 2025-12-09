package com.example.project.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Class này chịu trách nhiệm: Tạo Database, Tạo Bảng, và Nâng cấp phiên bản DB
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // companion object giống như 'static' trong Java/PHP
    // Chứa các hằng số để dùng chung, tránh việc gõ sai tên bảng/cột ở nhiều nơi
    companion object {
        private const val DATABASE_NAME = "vocab_app.db" // Tên file database
        private const val DATABASE_VERSION = 1           // Phiên bản DB

        // Tên bảng
        const val TABLE_WORDS = "words"

        // Tên các cột (Column)
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"
        const val COLUMN_MEANING = "meaning"
        const val COLUMN_PRONUNCIATION = "pronunciation"
        const val COLUMN_PART_OF_SPEECH = "part_of_speech"

        // Lưu ý: isSelected KHÔNG được lưu vào DB vì nó chỉ là trạng thái UI tạm thời
    }

    // 1. Hàm onCreate: Chỉ chạy 1 lần duy nhất khi app được cài và DB chưa tồn tại
    override fun onCreate(db: SQLiteDatabase) {
        // Tạo câu lệnh SQL: CREATE TABLE words (...)
        val createTableQuery = ("CREATE TABLE $TABLE_WORDS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " // ID tự động tăng
                + "$COLUMN_WORD TEXT, "
                + "$COLUMN_MEANING TEXT, "
                + "$COLUMN_PRONUNCIATION TEXT, "
                + "$COLUMN_PART_OF_SPEECH TEXT)")

        // Thực thi câu lệnh SQL
        db.execSQL(createTableQuery)
    }

    // 2. Hàm onUpgrade: Chạy khi bạn thay đổi DATABASE_VERSION (ví dụ từ 1 lên 2)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Chiến lược đơn giản nhất: Xóa bảng cũ đi và tạo lại từ đầu
        // (Lưu ý: Dữ liệu cũ sẽ mất. Trong thực tế production cần code migrate phức tạp hơn)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORDS")
        onCreate(db)
    }
}