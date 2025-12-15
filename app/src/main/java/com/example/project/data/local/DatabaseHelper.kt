package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vocab_app.db"
        private const val DATABASE_VERSION = 11 // Incremented version to ensure onUpgrade is called

        // ... (Tất cả các hằng số tên bảng và cột không đổi)
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
        const val COLUMN_QQ_DIFFICULTY = "difficulty"  // 1=Easy, 2=Medium, 3=Hard

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
        
        // Table 12: User Word Status
        const val TABLE_USER_WORD_STATUS = "user_word_status"
        const val COLUMN_USER_WORD_ID = "id"
        const val COLUMN_USER_WORD_USER_ID = "user_id"
        const val COLUMN_USER_WORD_WORD_ID = "word_id"
        const val COLUMN_USER_WORD_STATUS = "status"
        const val COLUMN_USER_WORD_LAST_REVIEWED = "last_reviewed"
        const val COLUMN_USER_WORD_REVIEW_COUNT = "review_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // (Các lệnh CREATE TABLE không thay đổi)
        // 1. Users
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_NAME TEXT,
                $COLUMN_USER_ROLE TEXT DEFAULT 'user',
                $COLUMN_USER_CREATED_AT TEXT
            )
        """
        )

        // 2. Levels
        db.execSQL(
            """
            CREATE TABLE $TABLE_LEVELS (
                $COLUMN_LEVEL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LEVEL_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_LEVEL_COLOR TEXT
            )
        """
        )

        // 3. Categories
        db.execSQL(
            """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CAT_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_CAT_DESCRIPTION TEXT,
                $COLUMN_CAT_ICON TEXT,
                $COLUMN_CAT_COLOR TEXT
            )
        """
        )

        // 4. Words - User's vocabulary
        db.execSQL(
            """
            CREATE TABLE $TABLE_WORDS (
                $COLUMN_WORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WORD_USER_ID INTEGER NOT NULL,
                $COLUMN_WORD_WORD TEXT,
                $COLUMN_WORD_MEANING TEXT,
                $COLUMN_WORD_PRONUNCIATION TEXT,
                $COLUMN_WORD_PART_OF_SPEECH TEXT,
                FOREIGN KEY($COLUMN_WORD_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """
        )

        // 5. Dictionary Words - System
        db.execSQL(
            """
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
        """
        )

        // 6. Word Progress
        db.execSQL(
            """
            CREATE TABLE $TABLE_WORD_PROGRESS (
                $COLUMN_WP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WP_USER_ID INTEGER NOT NULL,
                $COLUMN_WP_WORD_ID INTEGER NOT NULL,
                $COLUMN_WP_STATUS TEXT DEFAULT 'new',
                $COLUMN_WP_REVIEW_COUNT INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_WP_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_WP_WORD_ID) REFERENCES $TABLE_WORDS($COLUMN_WORD_ID)
            )
        """
        )

        // 7. Quizzes
        db.execSQL(
            """
            CREATE TABLE $TABLE_QUIZZES (
                $COLUMN_QUIZ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QUIZ_TITLE TEXT NOT NULL,
                $COLUMN_QUIZ_LEVEL_ID INTEGER,
                $COLUMN_QUIZ_CATEGORY_ID INTEGER,
                FOREIGN KEY($COLUMN_QUIZ_LEVEL_ID) REFERENCES $TABLE_LEVELS($COLUMN_LEVEL_ID),
                FOREIGN KEY($COLUMN_QUIZ_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CAT_ID)
            )
        """
        )

        // 8. Quiz Questions
        db.execSQL(
            """
            CREATE TABLE $TABLE_QUIZ_QUESTIONS (
                $COLUMN_QQ_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QQ_QUIZ_ID INTEGER NOT NULL,
                $COLUMN_QQ_QUESTION TEXT NOT NULL,
                $COLUMN_QQ_ANSWER TEXT NOT NULL,
                $COLUMN_QQ_DIFFICULTY INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY($COLUMN_QQ_QUIZ_ID) REFERENCES $TABLE_QUIZZES($COLUMN_QUIZ_ID)
            )
        """
        )

        // 9. Quiz Results
        db.execSQL(
            """
            CREATE TABLE $TABLE_QUIZ_RESULTS (
                $COLUMN_QR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QR_QUIZ_ID INTEGER NOT NULL,
                $COLUMN_QR_USER_ID INTEGER NOT NULL,
                $COLUMN_QR_SCORE INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_QR_QUIZ_ID) REFERENCES $TABLE_QUIZZES($COLUMN_QUIZ_ID),
                FOREIGN KEY($COLUMN_QR_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """
        )

        // 10. Study Sessions
        db.execSQL(
            """
            CREATE TABLE $TABLE_STUDY_SESSIONS (
                $COLUMN_SS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SS_USER_ID INTEGER NOT NULL,
                $COLUMN_SS_WORDS_COUNT INTEGER DEFAULT 0,
                $COLUMN_SS_DATE TEXT,
                FOREIGN KEY($COLUMN_SS_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """
        )

        // 11. User Stats
        db.execSQL(
            """
            CREATE TABLE $TABLE_USER_STATS (
                $COLUMN_US_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_US_USER_ID INTEGER NOT NULL UNIQUE,
                $COLUMN_US_TOTAL_WORDS INTEGER DEFAULT 0,
                $COLUMN_US_LEARNED_WORDS INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_US_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """
        )

        // 12. User Word Status
        db.execSQL(
            """
            CREATE TABLE $TABLE_USER_WORD_STATUS (
                $COLUMN_USER_WORD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_WORD_USER_ID INTEGER NOT NULL,
                $COLUMN_USER_WORD_WORD_ID INTEGER NOT NULL,
                $COLUMN_USER_WORD_STATUS TEXT NOT NULL,
                $COLUMN_USER_WORD_LAST_REVIEWED TEXT,
                $COLUMN_USER_WORD_REVIEW_COUNT INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_USER_WORD_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_USER_WORD_WORD_ID) REFERENCES $TABLE_DICTIONARY($COLUMN_DICT_ID)
            )
            """
        )

        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_WORD_STATUS")
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

    private fun seedData(db: SQLiteDatabase) {
        // --- SEED DATA ---
        // Levels
        db.execSQL("INSERT INTO $TABLE_LEVELS ($COLUMN_LEVEL_NAME, $COLUMN_LEVEL_COLOR) VALUES ('A1', '#4CAF50')")
        db.execSQL("INSERT INTO $TABLE_LEVELS ($COLUMN_LEVEL_NAME, $COLUMN_LEVEL_COLOR) VALUES ('A2', '#8BC34A')")
        db.execSQL("INSERT INTO $TABLE_LEVELS ($COLUMN_LEVEL_NAME, $COLUMN_LEVEL_COLOR) VALUES ('B1', '#FFC107')")
        db.execSQL("INSERT INTO $TABLE_LEVELS ($COLUMN_LEVEL_NAME, $COLUMN_LEVEL_COLOR) VALUES ('B2', '#FF9800')")

        // Categories
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Common', 'Từ vựng thông dụng hàng ngày', 'ic_common', '#4CAF50')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Business', 'Từ vựng kinh doanh', 'ic_business', '#2196F3')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Academic', 'Từ vựng học thuật', 'ic_academic', '#9C27B0')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Travel', 'Từ vựng du lịch', 'ic_travel', '#FF9800')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Technology', 'Từ vựng công nghệ', 'ic_tech', '#607D8B')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COLUMN_CAT_NAME, $COLUMN_CAT_DESCRIPTION, $COLUMN_CAT_ICON, $COLUMN_CAT_COLOR) VALUES ('Food', 'Từ vựng ẩm thực', 'ic_food', '#FF5722')")

        // Quizzes (Giả sử category_id=1)
        db.execSQL("INSERT INTO $TABLE_QUIZZES ($COLUMN_QUIZ_TITLE, $COLUMN_QUIZ_LEVEL_ID, $COLUMN_QUIZ_CATEGORY_ID) VALUES ('Basic Quiz A1', 1, 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZZES ($COLUMN_QUIZ_TITLE, $COLUMN_QUIZ_LEVEL_ID, $COLUMN_QUIZ_CATEGORY_ID) VALUES ('Basic Quiz A2', 2, 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZZES ($COLUMN_QUIZ_TITLE, $COLUMN_QUIZ_LEVEL_ID, $COLUMN_QUIZ_CATEGORY_ID) VALUES ('Intermediate Quiz B1', 3, 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZZES ($COLUMN_QUIZ_TITLE, $COLUMN_QUIZ_LEVEL_ID, $COLUMN_QUIZ_CATEGORY_ID) VALUES ('Advanced Quiz B2', 4, 1)")

        // Questions for quiz 1 (A1) - 6 câu
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Hello in Vietnamese?', 'Xin chào', 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Goodbye in Vietnamese?', 'Tạm biệt', 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Thank you in Vietnamese?', 'Cảm ơn', 2)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Sorry in Vietnamese?', 'Xin lỗi', 2)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Cat in Vietnamese?', 'Con mèo', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (1, 'What is Dog in Vietnamese?', 'Con chó', 3)")

        // Questions for quiz 2 (A2) - Thêm 3 câu khó
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is House in Vietnamese?', 'Ngôi nhà', 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is School in Vietnamese?', 'Trường học', 1)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is Water in Vietnamese?', 'Nước', 2)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is Fire in Vietnamese?', 'Lửa', 2)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is Love in Vietnamese?', 'Tình yêu', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What is the opposite of a professional?', 'Amateur', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'What does ''ephemeral'' mean?', 'Lasting for a very short time', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (2, 'A feeling of deep anxiety or dread', 'Angst', 3)")

        // Questions for quiz 3 (B1) - Thêm 3 câu khó
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (3, 'Which word means to make something better?', 'Ameliorate', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (3, 'A person who is excessively fond of themselves', 'Narcissist', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (3, 'What is a feeling of great happiness?', 'Euphoria', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (3, 'Someone who is stubbornly persistent', 'Obstinate', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (3, 'To formally renounce a belief or claim', 'Abjure', 3)")

        // Questions for quiz 4 (B2) - 5 câu rất khó
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (4, 'A statement that appears self-contradictory but contains a latent truth', 'Paradox', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (4, 'Characterized by severe self-discipline and abstention from all forms of indulgence', 'Ascetic', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (4, 'The quality of being open and honest in expression', 'Candor', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (4, 'Excessively talkative, especially on trivial matters', 'Garrulous', 3)")
        db.execSQL("INSERT INTO $TABLE_QUIZ_QUESTIONS ($COLUMN_QQ_QUIZ_ID, $COLUMN_QQ_QUESTION, $COLUMN_QQ_ANSWER, $COLUMN_QQ_DIFFICULTY) VALUES (4, 'A remedy for all ills or difficulties', 'Panacea', 3)")

        // Seed Dictionary Words
        val values = ContentValues()
        values.put(COLUMN_DICT_WORD, "Hello")
        values.put(COLUMN_DICT_MEANING, "Xin chào")
        values.put(COLUMN_DICT_PRONUNCIATION, "/həˈloʊ/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Interjection")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "Hello, how are you?")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Goodbye")
        values.put(COLUMN_DICT_MEANING, "Tạm biệt")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ɡʊdˈbaɪ/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Interjection")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "Goodbye, see you later!")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Thank you")
        values.put(COLUMN_DICT_MEANING, "Cảm ơn")
        values.put(COLUMN_DICT_PRONUNCIATION, "/θæŋk juː/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Phrase")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "Thank you for your help.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Please")
        values.put(COLUMN_DICT_MEANING, "Làm ơn")
        values.put(COLUMN_DICT_PRONUNCIATION, "/pliːz/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Adverb")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "Please help me.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Sorry")
        values.put(COLUMN_DICT_MEANING, "Xin lỗi")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈsɒri/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Adjective")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "I'm sorry for being late.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Meeting")
        values.put(COLUMN_DICT_MEANING, "Cuộc họp")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈmiːtɪŋ/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "We have a meeting at 3 PM.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Deadline")
        values.put(COLUMN_DICT_MEANING, "Hạn chót")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈdedlaɪn/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "The deadline is tomorrow.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Presentation")
        values.put(COLUMN_DICT_MEANING, "Bài thuyết trình")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˌprezənˈteɪʃn/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "I have to give a presentation.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Computer")
        values.put(COLUMN_DICT_MEANING, "Máy tính")
        values.put(COLUMN_DICT_PRONUNCIATION, "/kəmˈpjuːtər/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "I use my computer every day.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Software")
        values.put(COLUMN_DICT_MEANING, "Phần mềm")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈsɒftweər/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "This software is very useful.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Internet")
        values.put(COLUMN_DICT_MEANING, "Mạng internet")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈɪntərnet/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "I can't connect to the internet.")
        db.insert(TABLE_DICTIONARY, null, values)

        // --- 20 NEW WORDS ---
        
        // Common words - A1
        values.clear()
        values.put(COLUMN_DICT_WORD, "Yes")
        values.put(COLUMN_DICT_MEANING, "Có/Vâng")
        values.put(COLUMN_DICT_PRONUNCIATION, "/jes/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Adverb")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "Yes, I agree with you.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "No")
        values.put(COLUMN_DICT_MEANING, "Không")
        values.put(COLUMN_DICT_PRONUNCIATION, "/noʊ/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Adverb")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "No, I don't want to go.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Travel words - A2
        values.clear()
        values.put(COLUMN_DICT_WORD, "Airport")
        values.put(COLUMN_DICT_MEANING, "Sân bay")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈeərpɔːrt/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 4)
        values.put(COLUMN_DICT_EXAMPLE, "We arrived at the airport early.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Hotel")
        values.put(COLUMN_DICT_MEANING, "Khách sạn")
        values.put(COLUMN_DICT_PRONUNCIATION, "/hoʊˈtel/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 4)
        values.put(COLUMN_DICT_EXAMPLE, "The hotel has a beautiful view.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Ticket")
        values.put(COLUMN_DICT_MEANING, "Vé")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈtɪkɪt/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 4)
        values.put(COLUMN_DICT_EXAMPLE, "I bought a ticket for the concert.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Food words - A1, A2
        values.clear()
        values.put(COLUMN_DICT_WORD, "Water")
        values.put(COLUMN_DICT_MEANING, "Nước")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈwɔːtər/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 6)
        values.put(COLUMN_DICT_EXAMPLE, "I drink water every day.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Coffee")
        values.put(COLUMN_DICT_MEANING, "Cà phê")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈkɔːfi/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 6)
        values.put(COLUMN_DICT_EXAMPLE, "I love drinking coffee in the morning.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Restaurant")
        values.put(COLUMN_DICT_MEANING, "Nhà hàng")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈrestrɒnt/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 6)
        values.put(COLUMN_DICT_EXAMPLE, "Let's go to a restaurant for dinner.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Business words - B1
        values.clear()
        values.put(COLUMN_DICT_WORD, "Manager")
        values.put(COLUMN_DICT_MEANING, "Quản lý")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈmænɪdʒər/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "She is the manager of this department.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Contract")
        values.put(COLUMN_DICT_MEANING, "Hợp đồng")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈkɒntrækt/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "Please sign the contract.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Salary")
        values.put(COLUMN_DICT_MEANING, "Lương")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈsæləri/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 2)
        values.put(COLUMN_DICT_EXAMPLE, "My salary is paid monthly.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Technology words - A2, B1
        values.clear()
        values.put(COLUMN_DICT_WORD, "Phone")
        values.put(COLUMN_DICT_MEANING, "Điện thoại")
        values.put(COLUMN_DICT_PRONUNCIATION, "/foʊn/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "Can I use your phone?")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Email")
        values.put(COLUMN_DICT_MEANING, "Thư điện tử")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈiːmeɪl/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 2)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "Send me an email when you're ready.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Application")
        values.put(COLUMN_DICT_MEANING, "Ứng dụng")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˌæplɪˈkeɪʃn/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 5)
        values.put(COLUMN_DICT_EXAMPLE, "This application is very useful.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Academic words - B1, B2
        values.clear()
        values.put(COLUMN_DICT_WORD, "Study")
        values.put(COLUMN_DICT_MEANING, "Học tập")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈstʌdi/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Verb")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 3)
        values.put(COLUMN_DICT_EXAMPLE, "I study English every day.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Research")
        values.put(COLUMN_DICT_MEANING, "Nghiên cứu")
        values.put(COLUMN_DICT_PRONUNCIATION, "/rɪˈsɜːrtʃ/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 3)
        values.put(COLUMN_DICT_CATEGORY_ID, 3)
        values.put(COLUMN_DICT_EXAMPLE, "She is doing research on climate change.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Analysis")
        values.put(COLUMN_DICT_MEANING, "Phân tích")
        values.put(COLUMN_DICT_PRONUNCIATION, "/əˈnæləsɪs/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 4)
        values.put(COLUMN_DICT_CATEGORY_ID, 3)
        values.put(COLUMN_DICT_EXAMPLE, "The analysis shows interesting results.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Theory")
        values.put(COLUMN_DICT_MEANING, "Lý thuyết")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈθɪəri/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 4)
        values.put(COLUMN_DICT_CATEGORY_ID, 3)
        values.put(COLUMN_DICT_EXAMPLE, "Einstein's theory of relativity is famous.")
        db.insert(TABLE_DICTIONARY, null, values)

        // Common words - A2, B1
        values.clear()
        values.put(COLUMN_DICT_WORD, "Friend")
        values.put(COLUMN_DICT_MEANING, "Bạn bè")
        values.put(COLUMN_DICT_PRONUNCIATION, "/frend/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "She is my best friend.")
        db.insert(TABLE_DICTIONARY, null, values)

        values.clear()
        values.put(COLUMN_DICT_WORD, "Family")
        values.put(COLUMN_DICT_MEANING, "Gia đình")
        values.put(COLUMN_DICT_PRONUNCIATION, "/ˈfæməli/")
        values.put(COLUMN_DICT_PART_OF_SPEECH, "Noun")
        values.put(COLUMN_DICT_LEVEL_ID, 1)
        values.put(COLUMN_DICT_CATEGORY_ID, 1)
        values.put(COLUMN_DICT_EXAMPLE, "I love my family very much.")
        db.insert(TABLE_DICTIONARY, null, values)
    }
}
