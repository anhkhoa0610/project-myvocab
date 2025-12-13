package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.DictionaryWord

class DictionaryWordDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Helper function để map cursor to DictionaryWord
    private fun mapCursorToWord(cursor: android.database.Cursor): DictionaryWord {
        return DictionaryWord(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_ID)),
            word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_WORD)),
            meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_MEANING)),
            pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PRONUNCIATION)) ?: "",
            part_of_speech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH)) ?: "",
            level_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_LEVEL_ID)),
            category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_CATEGORY_ID)),
            example_sentence = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_EXAMPLE)) ?: "",
            is_favorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DICT_IS_FAVORITE)) == 1
        )
    }

    // Thêm từ mới
    fun addWord(word: DictionaryWord): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DICT_WORD, word.word)
            put(DatabaseHelper.COLUMN_DICT_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_DICT_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH, word.part_of_speech)
            put(DatabaseHelper.COLUMN_DICT_LEVEL_ID, word.level_id)
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
                wordList.add(mapCursorToWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Lấy từ theo level_id
    fun getWordsByLevelId(levelId: Int): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_LEVEL_ID} = ?",
            arrayOf(levelId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                wordList.add(mapCursorToWord(cursor))
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
                wordList.add(mapCursorToWord(cursor))
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
                wordList.add(mapCursorToWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Toggle favorite
    fun toggleFavorite(wordId: Int): Boolean {
        val db = dbHelper.writableDatabase
        
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
                wordList.add(mapCursorToWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return wordList
    }

    // Cập nhật từ
    fun updateWord(word: DictionaryWord): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DICT_WORD, word.word)
            put(DatabaseHelper.COLUMN_DICT_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_DICT_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_DICT_PART_OF_SPEECH, word.part_of_speech)
            put(DatabaseHelper.COLUMN_DICT_LEVEL_ID, word.level_id)
            put(DatabaseHelper.COLUMN_DICT_CATEGORY_ID, word.category_id)
            put(DatabaseHelper.COLUMN_DICT_EXAMPLE, word.example_sentence)
            put(DatabaseHelper.COLUMN_DICT_IS_FAVORITE, if (word.is_favorite) 1 else 0)
        }
        val result = db.update(
            DatabaseHelper.TABLE_DICTIONARY,
            values,
            "${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(word.id.toString())
        )
        db.close()
        return result
    }

    // Xóa từ
    fun deleteWord(wordId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_DICTIONARY,
            "${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(wordId.toString())
        )
        db.close()
        return result
    }

    // Seed sample dictionary words
    fun seedSampleWords() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()

        if (count == 0) {
            // level_id: 1=A1, 2=A2, 3=B1, 4=B2, 5=C1, 6=C2
            
            // Common words (category_id = 1, level_id = 1)
            addWord(DictionaryWord(0, "Hello", "Xin chào", "/həˈloʊ/", "Interjection", 1, 1, "Hello, how are you?", false))
            addWord(DictionaryWord(0, "Goodbye", "Tạm biệt", "/ɡʊdˈbaɪ/", "Interjection", 1, 1, "Goodbye, see you later!", false))
            addWord(DictionaryWord(0, "Thank you", "Cảm ơn", "/θæŋk juː/", "Phrase", 1, 1, "Thank you for your help.", false))
            addWord(DictionaryWord(0, "Please", "Làm ơn", "/pliːz/", "Adverb", 1, 1, "Please help me.", false))
            addWord(DictionaryWord(0, "Sorry", "Xin lỗi", "/ˈsɒri/", "Adjective", 1, 1, "I'm sorry for being late.", false))
            
            // Business words (category_id = 2, level_id = 3)
            addWord(DictionaryWord(0, "Meeting", "Cuộc họp", "/ˈmiːtɪŋ/", "Noun", 3, 2, "We have a meeting at 3 PM.", false))
            addWord(DictionaryWord(0, "Deadline", "Hạn chót", "/ˈdedlaɪn/", "Noun", 3, 2, "The deadline is tomorrow.", false))
            addWord(DictionaryWord(0, "Presentation", "Bài thuyết trình", "/ˌprezənˈteɪʃn/", "Noun", 3, 2, "I have to give a presentation.", false))
            
            // Technology words (category_id = 5, level_id = 2)
            addWord(DictionaryWord(0, "Computer", "Máy tính", "/kəmˈpjuːtər/", "Noun", 2, 5, "I use my computer every day.", false))
            addWord(DictionaryWord(0, "Software", "Phần mềm", "/ˈsɒftweər/", "Noun", 3, 5, "This software is very useful.", false))
            addWord(DictionaryWord(0, "Internet", "Mạng internet", "/ˈɪntərnet/", "Noun", 2, 5, "I can't connect to the internet.", false))
        }
    }
}
