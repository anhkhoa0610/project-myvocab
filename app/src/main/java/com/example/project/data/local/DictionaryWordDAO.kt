package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.DictionaryWord

class DictionaryWordDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Thêm từ mới
    fun addWord(word: DictionaryWord): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DICT_WORD, word.word)
            put(DatabaseHelper.COLUMN_DICT_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_DICT_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH, word.part_of_speech)
            put(DatabaseHelper.COLUMN_DICT_LEVEL, word.level)
            put(DatabaseHelper.COLUMN_DICT_CATEGORY_ID, word.category_id)
            put(DatabaseHelper.COLUMN_DICT_EXAMPLE, word.example_sentence)
            put(DatabaseHelper.COLUMN_DICT_IS_FAVORITE, if (word.is_favorite) 1 else 0)
        }
        val result = db.insert(DatabaseHelper.TABLE_DICTIONARY, null, values)
        db.close()
        return result
    }

    // Lấy tất cả từ
    fun getAllWords(): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY}", null)

        if (cursor.moveToFirst()) {
            do {
                val word = DictionaryWord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
                    meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
                    part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
                    level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL)) ?: "",
                    category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
                    example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
                    is_favorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_IS_FAVORITE)) == 1
                )
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Lấy từ theo level (A1, A2, B1, B2, C1, C2)
    fun getWordsByLevel(level: String): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_LEVEL} = ?",
            arrayOf(level)
        )

        if (cursor.moveToFirst()) {
            do {
                val word = DictionaryWord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
                    meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
                    part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
                    level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL)) ?: "",
                    category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
                    example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
                    is_favorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_IS_FAVORITE)) == 1
                )
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Lấy từ theo category
    fun getWordsByCategory(categoryId: Int): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_CATEGORY_ID} = ?",
            arrayOf(categoryId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val word = DictionaryWord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
                    meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
                    part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
                    level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL)) ?: "",
                    category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
                    example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
                    is_favorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_IS_FAVORITE)) == 1
                )
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Lấy từ yêu thích
    fun getFavoriteWords(): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_IS_FAVORITE} = 1",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val word = DictionaryWord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
                    meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
                    part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
                    level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL)) ?: "",
                    category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
                    example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
                    is_favorite = true
                )
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Toggle favorite
    fun toggleFavorite(wordId: Int): Boolean {
        val db = dbHelper.writableDatabase
        
        // Lấy trạng thái hiện tại
        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_DICT_IS_FAVORITE} FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(wordId.toString())
        )
        
        var newValue = 0
        if (cursor.moveToFirst()) {
            val currentValue = cursor.getInt(0)
            newValue = if (currentValue == 1) 0 else 1
        }
        cursor.close()
        
        // Cập nhật
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DICT_IS_FAVORITE, newValue)
        }
        val result = db.update(
            DatabaseHelper.TABLE_DICTIONARY,
            values,
            "${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(wordId.toString())
        )
        db.close()
        return result > 0
    }

    // Tìm kiếm từ
    fun searchWords(query: String): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_WORD} LIKE ? OR ${DatabaseHelper.COLUMN_DICT_MEANING} LIKE ?",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val word = DictionaryWord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
                    word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
                    meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
                    pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
                    part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
                    level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL)) ?: "",
                    category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
                    example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
                    is_favorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_IS_FAVORITE)) == 1
                )
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Seed sample dictionary words
    fun seedSampleWords() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()

        // Chỉ seed nếu chưa có từ nào
        if (count == 0) {
            // Common words (category_id = 1)
            addWord(DictionaryWord(0, "Hello", "Xin chào", "/həˈloʊ/", "Interjection", "A1", 1, "Hello, how are you?", false))
            addWord(DictionaryWord(0, "Goodbye", "Tạm biệt", "/ɡʊdˈbaɪ/", "Interjection", "A1", 1, "Goodbye, see you later!", false))
            addWord(DictionaryWord(0, "Thank you", "Cảm ơn", "/θæŋk juː/", "Phrase", "A1", 1, "Thank you for your help.", false))
            addWord(DictionaryWord(0, "Please", "Làm ơn", "/pliːz/", "Adverb", "A1", 1, "Please help me.", false))
            addWord(DictionaryWord(0, "Sorry", "Xin lỗi", "/ˈsɒri/", "Adjective", "A1", 1, "I'm sorry for being late.", false))
            
            // Business words (category_id = 2)
            addWord(DictionaryWord(0, "Meeting", "Cuộc họp", "/ˈmiːtɪŋ/", "Noun", "B1", 2, "We have a meeting at 3 PM.", false))
            addWord(DictionaryWord(0, "Deadline", "Hạn chót", "/ˈdedlaɪn/", "Noun", "B1", 2, "The deadline is tomorrow.", false))
            addWord(DictionaryWord(0, "Presentation", "Bài thuyết trình", "/ˌprezənˈteɪʃn/", "Noun", "B1", 2, "I have to give a presentation.", false))
            
            // Technology words (category_id = 5)
            addWord(DictionaryWord(0, "Computer", "Máy tính", "/kəmˈpjuːtər/", "Noun", "A2", 5, "I use my computer every day.", false))
            addWord(DictionaryWord(0, "Software", "Phần mềm", "/ˈsɒftweər/", "Noun", "B1", 5, "This software is very useful.", false))
            addWord(DictionaryWord(0, "Internet", "Mạng internet", "/ˈɪntərnet/", "Noun", "A2", 5, "I can't connect to the internet.", false))
        }
    }
    // --- THỐNG KÊ CƠ BẢN ---

    /** Thống kê 1: Số từ đã thêm (Total Words Added) */
    fun getTotalWordsAdded(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY}", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        // Dữ liệu giả định: Trả về một giá trị giả lập nếu bạn muốn
        // return 150
        return count
    }

    /** Thống kê 2: Số từ đã học (Total Words Mastered)
     * Giả định level >= 5 là đã học/thành thạo.
     */
    fun getTotalWordsMastered(masteryLevel: Int = 5): Int {
        val db = dbHelper.readableDatabase
        val selection = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_LEVEL} >= ?"
        val selectionArgs = arrayOf(masteryLevel.toString())
        val cursor = db.rawQuery(selection, selectionArgs)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        // Dữ liệu giả định: Trả về một giá trị giả lập nếu bạn muốn
        // return 65
        return count
    }

    fun getWordCountByLevel(): Map<String, Int> {
        val levelCounts = mutableMapOf<String, Int>()
        val db = dbHelper.readableDatabase

        // Truy vấn GROUP BY cột level
        val query = "SELECT ${DatabaseHelper.COLUMN_DICT_LEVEL}, COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY} GROUP BY ${DatabaseHelper.COLUMN_DICT_LEVEL}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val level = cursor.getString(0) // Cột level
                val count = cursor.getInt(1)    // Cột COUNT(*)
                levelCounts[level] = count
            } while (cursor.moveToNext())
        }
        cursor.close()
        // db.close() // Nếu bạn không đóng DB ở cuối hàm DAO, thì không cần dòng này
        return levelCounts
    }
}
