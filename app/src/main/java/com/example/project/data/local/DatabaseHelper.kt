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
        private const val DATABASE_VERSION = 3           // Phiên bản DB - TĂNG LÊN 3

        // Table 1: User Words (GIỮ NGUYÊN - KHÔNG ĐỤ NG)
        const val TABLE_WORDS = "words"
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"
        const val COLUMN_MEANING = "meaning"
        const val COLUMN_PRONUNCIATION = "pronunciation"
        const val COLUMN_PART_OF_SPEECH = "part_of_speech"

        // Table 2: Categories (MỚI THÊM)
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CAT_ID = "id"
        const val COLUMN_CAT_NAME = "name"
        const val COLUMN_CAT_DESCRIPTION = "description"
        const val COLUMN_CAT_ICON = "icon"
        const val COLUMN_CAT_COLOR = "color"

        // Table 3: Dictionary Words (MỚI THÊM)
        const val TABLE_DICTIONARY = "dictionary_words"
        const val COLUMN_DICT_ID = "id"
        const val COLUMN_DICT_WORD = "word"
        const val COLUMN_DICT_MEANING = "meaning"
        const val COLUMN_DICT_PRONUNCIATION = "pronunciation"
        const val COLUMN_DICT_PART_OF_SPEECH = "part_of_speech"
        const val COLUMN_DICT_LEVEL = "level"
        const val COLUMN_DICT_CATEGORY_ID = "category_id"  // Foreign Key
        const val COLUMN_DICT_EXAMPLE = "example_sentence"
        const val COLUMN_DICT_IS_FAVORITE = "is_favorite"
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

        // Thực thi câu lệnh SQL cho table words (GIỮ NGUYÊN)
        db.execSQL(createTableQuery)
        
        // Tạo table categories MỚI (phải tạo trước dictionary_words vì có foreign key)
        val createCategoriesTable = ("CREATE TABLE $TABLE_CATEGORIES ("
                + "$COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CAT_NAME TEXT NOT NULL UNIQUE, "
                + "$COLUMN_CAT_DESCRIPTION TEXT, "
                + "$COLUMN_CAT_ICON TEXT, "
                + "$COLUMN_CAT_COLOR TEXT)")
        db.execSQL(createCategoriesTable)
        
        // Tạo table dictionary_words MỚI (với foreign key)
        val createDictionaryTable = ("CREATE TABLE $TABLE_DICTIONARY ("
                + "$COLUMN_DICT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_DICT_WORD TEXT, "
                + "$COLUMN_DICT_MEANING TEXT, "
                + "$COLUMN_DICT_PRONUNCIATION TEXT, "
                + "$COLUMN_DICT_PART_OF_SPEECH TEXT, "
                + "$COLUMN_DICT_LEVEL TEXT, "
                + "$COLUMN_DICT_CATEGORY_ID INTEGER, "
                + "$COLUMN_DICT_EXAMPLE TEXT, "
                + "$COLUMN_DICT_IS_FAVORITE INTEGER DEFAULT 0, "
                + "FOREIGN KEY($COLUMN_DICT_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CAT_ID))")
        db.execSQL(createDictionaryTable)
    }

    // 2. Hàm onUpgrade: Chạy khi bạn thay đổi DATABASE_VERSION
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Nâng cấp từ version 1 lên 2: THÊM table dictionary_words (cũ)
        if (oldVersion < 2) {
            val createDictionaryTable = ("CREATE TABLE IF NOT EXISTS $TABLE_DICTIONARY ("
                    + "$COLUMN_DICT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_DICT_WORD TEXT, "
                    + "$COLUMN_DICT_MEANING TEXT, "
                    + "$COLUMN_DICT_PRONUNCIATION TEXT, "
                    + "$COLUMN_DICT_PART_OF_SPEECH TEXT, "
                    + "$COLUMN_DICT_LEVEL TEXT, "
                    + "$COLUMN_DICT_CATEGORY_ID INTEGER, "
                    + "$COLUMN_DICT_EXAMPLE TEXT, "
                    + "$COLUMN_DICT_IS_FAVORITE INTEGER DEFAULT 0)")
            db.execSQL(createDictionaryTable)
        }
        
        // Nâng cấp từ version 2 lên 3: THÊM table categories và cập nhật dictionary_words
        if (oldVersion < 3) {
            // Tạo table categories
            val createCategoriesTable = ("CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIES ("
                    + "$COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_CAT_NAME TEXT NOT NULL UNIQUE, "
                    + "$COLUMN_CAT_DESCRIPTION TEXT, "
                    + "$COLUMN_CAT_ICON TEXT, "
                    + "$COLUMN_CAT_COLOR TEXT)")
            db.execSQL(createCategoriesTable)
            
            // Nếu dictionary_words đã tồn tại từ version 2, cần migrate
            // Tạo lại table với foreign key
            db.execSQL("DROP TABLE IF EXISTS $TABLE_DICTIONARY")
            val createDictionaryTable = ("CREATE TABLE $TABLE_DICTIONARY ("
                    + "$COLUMN_DICT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_DICT_WORD TEXT, "
                    + "$COLUMN_DICT_MEANING TEXT, "
                    + "$COLUMN_DICT_PRONUNCIATION TEXT, "
                    + "$COLUMN_DICT_PART_OF_SPEECH TEXT, "
                    + "$COLUMN_DICT_LEVEL TEXT, "
                    + "$COLUMN_DICT_CATEGORY_ID INTEGER, "
                    + "$COLUMN_DICT_EXAMPLE TEXT, "
                    + "$COLUMN_DICT_IS_FAVORITE INTEGER DEFAULT 0, "
                    + "FOREIGN KEY($COLUMN_DICT_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CAT_ID))")
            db.execSQL(createDictionaryTable)
        }
        
        // KHÔNG XÓA table words cũ - GIỮ NGUYÊN dữ liệu user
    }
}