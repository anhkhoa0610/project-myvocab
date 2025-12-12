package com.example.project.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Class này chịu trách nhiệm: Tạo Database, Tạo Bảng, và Nâng cấp phiên bản DB
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vocab_app.db"
        private const val DATABASE_VERSION = 4  // Version mới nhất

        // Table 1: User Words (Từ người dùng tạo)
        const val TABLE_WORDS = "words"
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"
        const val COLUMN_MEANING = "meaning"
        const val COLUMN_PRONUNCIATION = "pronunciation"
        const val COLUMN_PART_OF_SPEECH = "part_of_speech"

        // Table 2: Categories (Phân loại từ vựng)
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CAT_ID = "id"
        const val COLUMN_CAT_NAME = "name"
        const val COLUMN_CAT_DESCRIPTION = "description"
        const val COLUMN_CAT_ICON = "icon"
        const val COLUMN_CAT_COLOR = "color"

        // Table 3: Dictionary Words (Từ điển có sẵn)
        const val TABLE_DICTIONARY = "dictionary_words"
        const val COLUMN_DICT_ID = "id"
        const val COLUMN_DICT_WORD = "word"
        const val COLUMN_DICT_MEANING = "meaning"
        const val COLUMN_DICT_PRONUNCIATION = "pronunciation"
        const val COLUMN_DICT_PART_OF_SPEECH = "part_of_speech"
        const val COLUMN_DICT_LEVEL = "level"
        const val COLUMN_DICT_CATEGORY_ID = "category_id"
        const val COLUMN_DICT_EXAMPLE = "example_sentence"
        const val COLUMN_DICT_IS_FAVORITE = "is_favorite"

        // Table 4: Users (Authentication)
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_ROLE = "role"
        const val COLUMN_USER_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Table 1: User Words
        val createWordsTable = ("CREATE TABLE $TABLE_WORDS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_WORD TEXT, "
                + "$COLUMN_MEANING TEXT, "
                + "$COLUMN_PRONUNCIATION TEXT, "
                + "$COLUMN_PART_OF_SPEECH TEXT)")
        db.execSQL(createWordsTable)

        // Table 2: Categories
        val createCategoriesTable = ("CREATE TABLE $TABLE_CATEGORIES ("
                + "$COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CAT_NAME TEXT NOT NULL UNIQUE, "
                + "$COLUMN_CAT_DESCRIPTION TEXT, "
                + "$COLUMN_CAT_ICON TEXT, "
                + "$COLUMN_CAT_COLOR TEXT)")
        db.execSQL(createCategoriesTable)

        // Table 3: Dictionary Words (với Foreign Key)
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

        // Table 4: Users (Authentication)
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE, "
                + "$COLUMN_USER_PASSWORD TEXT NOT NULL, "
                + "$COLUMN_USER_NAME TEXT, "
                + "$COLUMN_USER_ROLE TEXT DEFAULT 'user', "
                + "$COLUMN_USER_CREATED_AT TEXT)")
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Xóa tất cả tables cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DICTIONARY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORDS")
        
        // Tạo lại từ đầu
        onCreate(db)
    }
}