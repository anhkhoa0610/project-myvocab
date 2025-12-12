package com.example.project.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vocab_app.db"
        private const val DATABASE_VERSION = 5

        // Table 1: Users
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_ROLE = "role"
        const val COLUMN_USER_CREATED_AT = "created_at"

        // Table 2: Levels
        const val TABLE_LEVELS = "levels"
        const val COLUMN_LEVEL_ID = "id"
        const val COLUMN_LEVEL_NAME = "name"
        const val COLUMN_LEVEL_COLOR = "color"

        // Table 3: Categories
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CAT_ID = "id"
        const val COLUMN_CAT_NAME = "name"
        const val COLUMN_CAT_DESCRIPTION = "description"
        const val COLUMN_CAT_ICON = "icon"
        const val COLUMN_CAT_COLOR = "color"

        // Table 4: Words (User's vocabulary)
        const val TABLE_WORDS = "words"
        const val COLUMN_WORD_ID = "id"
        const val COLUMN_WORD_USER_ID = "user_id"
        const val COLUMN_WORD_WORD = "word"
        const val COLUMN_WORD_MEANING = "meaning"
        const val COLUMN_WORD_PRONUNCIATION = "pronunciation"
        const val COLUMN_WORD_PART_OF_SPEECH = "part_of_speech"

        // Table 5: Dictionary Words (System)
        const val TABLE_DICTIONARY = "dictionary_words"
        const val COLUMN_DICT_ID = "id"
        const val COLUMN_DICT_WORD = "word"
        const val COLUMN_DICT_MEANING = "meaning"
        const val COLUMN_DICT_PRONUNCIATION = "pronunciation"
        const val COLUMN_DICT_PART_OF_SPEECH = "part_of_speech"
        const val COLUMN_DICT_LEVEL_ID = "level_id"
        const val COLUMN_DICT_CATEGORY_ID = "category_id"
        const val COLUMN_DICT_EXAMPLE = "example_sentence"
        const val COLUMN_DICT_IS_FAVORITE = "is_favorite"

        // Table 6: Word Progress
        const val TABLE_WORD_PROGRESS = "word_progress"
        const val COLUMN_WP_ID = "id"
        const val COLUMN_WP_USER_ID = "user_id"
        const val COLUMN_WP_WORD_ID = "word_id"
        const val COLUMN_WP_STATUS = "status"
        const val COLUMN_WP_REVIEW_COUNT = "review_count"

        // Table 7: Quizzes
        const val TABLE_QUIZZES = "quizzes"
        const val COLUMN_QUIZ_ID = "id"
        const val COLUMN_QUIZ_TITLE = "title"
        const val COLUMN_QUIZ_LEVEL_ID = "level_id"
        const val COLUMN_QUIZ_CATEGORY_ID = "category_id"

        // Table 8: Quiz Questions
        const val TABLE_QUIZ_QUESTIONS = "quiz_questions"
        const val COLUMN_QQ_ID = "id"
        const val COLUMN_QQ_QUIZ_ID = "quiz_id"
        const val COLUMN_QQ_QUESTION = "question"
        const val COLUMN_QQ_ANSWER = "answer"

        // Table 9: Quiz Results
        const val TABLE_QUIZ_RESULTS = "quiz_results"
        const val COLUMN_QR_ID = "id"
        const val COLUMN_QR_QUIZ_ID = "quiz_id"
        const val COLUMN_QR_USER_ID = "user_id"
        const val COLUMN_QR_SCORE = "score"

        // Table 10: Study Sessions
        const val TABLE_STUDY_SESSIONS = "study_sessions"
        const val COLUMN_SS_ID = "id"
        const val COLUMN_SS_USER_ID = "user_id"
        const val COLUMN_SS_WORDS_COUNT = "words_count"
        const val COLUMN_SS_DATE = "date"

        // Table 11: User Stats
        const val TABLE_USER_STATS = "user_stats"
        const val COLUMN_US_ID = "id"
        const val COLUMN_US_USER_ID = "user_id"
        const val COLUMN_US_TOTAL_WORDS = "total_words"
        const val COLUMN_US_LEARNED_WORDS = "learned_words"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Users
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_NAME TEXT,
                $COLUMN_USER_ROLE TEXT DEFAULT 'user',
                $COLUMN_USER_CREATED_AT TEXT
            )
        """)

        // 2. Levels
        db.execSQL("""
            CREATE TABLE $TABLE_LEVELS (
                $COLUMN_LEVEL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LEVEL_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_LEVEL_COLOR TEXT
            )
        """)

        // 3. Categories
        db.execSQL("""
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CAT_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_CAT_DESCRIPTION TEXT,
                $COLUMN_CAT_ICON TEXT,
                $COLUMN_CAT_COLOR TEXT
            )
        """)

        // 4. Words - User's vocabulary
        db.execSQL("""
            CREATE TABLE $TABLE_WORDS (
                $COLUMN_WORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD_USER_ID INTEGER NOT NULL,
                $COLUMN_WORD_WORD TEXT,
                $COLUMN_WORD_MEANING TEXT,
                $COLUMN_WORD_PRONUNCIATION TEXT,
                $COLUMN_WORD_PART_OF_SPEECH TEXT,
                FOREIGN KEY($COLUMN_WORD_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """)

        // 5. Dictionary Words - System
        db.execSQL("""
            CREATE TABLE $TABLE_DICTIONARY (
                $COLUMN_DICT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DICT_WORD TEXT,
                $COLUMN_DICT_MEANING TEXT,
                $COLUMN_DICT_PRONUNCIATION TEXT,
                $COLUMN_DICT_PART_OF_SPEECH TEXT,
                $COLUMN_DICT_LEVEL_ID INTEGER,
                $COLUMN_DICT_CATEGORY_ID INTEGER,
                $COLUMN_DICT_EXAMPLE TEXT,
                $COLUMN_DICT_IS_FAVORITE INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_DICT_LEVEL_ID) REFERENCES $TABLE_LEVELS($COLUMN_LEVEL_ID),
                FOREIGN KEY($COLUMN_DICT_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CAT_ID)
            )
        """)

        // 6. Word Progress
        db.execSQL("""
            CREATE TABLE $TABLE_WORD_PROGRESS (
                $COLUMN_WP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WP_USER_ID INTEGER NOT NULL,
                $COLUMN_WP_WORD_ID INTEGER NOT NULL,
                $COLUMN_WP_STATUS TEXT DEFAULT 'new',
                $COLUMN_WP_REVIEW_COUNT INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_WP_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_WP_WORD_ID) REFERENCES $TABLE_WORDS($COLUMN_WORD_ID)
            )
        """)

        // 7. Quizzes
        db.execSQL("""
            CREATE TABLE $TABLE_QUIZZES (
                $COLUMN_QUIZ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QUIZ_TITLE TEXT NOT NULL,
                $COLUMN_QUIZ_LEVEL_ID INTEGER,
                $COLUMN_QUIZ_CATEGORY_ID INTEGER,
                FOREIGN KEY($COLUMN_QUIZ_LEVEL_ID) REFERENCES $TABLE_LEVELS($COLUMN_LEVEL_ID),
                FOREIGN KEY($COLUMN_QUIZ_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CAT_ID)
            )
        """)

        // 8. Quiz Questions
        db.execSQL("""
            CREATE TABLE $TABLE_QUIZ_QUESTIONS (
                $COLUMN_QQ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QQ_QUIZ_ID INTEGER NOT NULL,
                $COLUMN_QQ_QUESTION TEXT NOT NULL,
                $COLUMN_QQ_ANSWER TEXT NOT NULL,
                FOREIGN KEY($COLUMN_QQ_QUIZ_ID) REFERENCES $TABLE_QUIZZES($COLUMN_QUIZ_ID)
            )
        """)

        // 9. Quiz Results
        db.execSQL("""
            CREATE TABLE $TABLE_QUIZ_RESULTS (
                $COLUMN_QR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QR_QUIZ_ID INTEGER NOT NULL,
                $COLUMN_QR_USER_ID INTEGER NOT NULL,
                $COLUMN_QR_SCORE INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_QR_QUIZ_ID) REFERENCES $TABLE_QUIZZES($COLUMN_QUIZ_ID),
                FOREIGN KEY($COLUMN_QR_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """)

        // 10. Study Sessions
        db.execSQL("""
            CREATE TABLE $TABLE_STUDY_SESSIONS (
                $COLUMN_SS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SS_USER_ID INTEGER NOT NULL,
                $COLUMN_SS_WORDS_COUNT INTEGER DEFAULT 0,
                $COLUMN_SS_DATE TEXT,
                FOREIGN KEY($COLUMN_SS_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """)

        // 11. User Stats
        db.execSQL("""
            CREATE TABLE $TABLE_USER_STATS (
                $COLUMN_US_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_US_USER_ID INTEGER NOT NULL UNIQUE,
                $COLUMN_US_TOTAL_WORDS INTEGER DEFAULT 0,
                $COLUMN_US_LEARNED_WORDS INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_US_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_STATS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDY_SESSIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUIZ_RESULTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUIZ_QUESTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUIZZES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORD_PROGRESS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DICTIONARY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LEVELS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        
        onCreate(db)
    }
}