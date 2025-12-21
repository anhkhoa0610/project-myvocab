package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.project.data.model.DictionaryWord

class DictionaryWordDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun close() {
        dbHelper.close()
    }

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

    fun addWord(word: DictionaryWord): Long {
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
        return db.insert(DatabaseHelper.TABLE_DICTIONARY, null, values)
    }

    fun getAllWords(): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY}", null)

        if (cursor.moveToFirst()) {
            do {
                wordList.add(mapCursorToWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return wordList
    }

    fun getWordsByLevelId(levelId: Int): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
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
        return wordList
    }

    fun getWordsByCategory(categoryId: Int): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
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
        return wordList
    }

    fun getFavoriteWords(): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
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
        return wordList
    }

    fun toggleFavorite(wordId: Int): Boolean {
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
        return result > 0
    }

    fun searchWords(query: String): ArrayList<DictionaryWord> {
        val wordList = ArrayList<DictionaryWord>()
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
        return wordList
    }

    fun updateWord(word: DictionaryWord): Int {
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
        return db.update(
            DatabaseHelper.TABLE_DICTIONARY,
            values,
            "${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(word.id.toString())
        )
    }

    fun deleteWord(wordId: Int): Int {
        return db.delete(
            DatabaseHelper.TABLE_DICTIONARY,
            "${DatabaseHelper.COLUMN_DICT_ID} = ?",
            arrayOf(wordId.toString())
        )
    }

    fun getRandomWords(limit: Int, excludeId: Int): List<DictionaryWord> {
        val list = ArrayList<DictionaryWord>()
        // Lấy ngẫu nhiên 'limit' từ, TRỪ từ đang hỏi (excludeId)
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_DICTIONARY} WHERE ${DatabaseHelper.COLUMN_DICT_ID} != ? ORDER BY RANDOM() LIMIT ?",
            arrayOf(excludeId.toString(), limit.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(mapCursorToWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getWordsByLevel(levelId: Int): List<DictionaryWord> {
        return getWordsByLevelId(levelId)
    }

    fun getTotalWordCount(): Int {
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_DICTIONARY}",
            null
        )

        cursor.moveToFirst()
        val count = cursor.getInt(0)

        cursor.close()
        return count
    }
}
