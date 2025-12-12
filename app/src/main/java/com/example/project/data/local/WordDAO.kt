package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project.data.model.Word

class WordDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addWord(word: Word): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_WORD_USER_ID, word.user_id)
            put(DatabaseHelper.COLUMN_WORD_WORD, word.word)
            put(DatabaseHelper.COLUMN_WORD_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_WORD_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH, word.part_of_speech)
        }

        val result = db.insert(DatabaseHelper.TABLE_WORDS, null, values)
        db.close()

        return result
    }

    fun getAllWords(): ArrayList<Word> {
        val listWords = ArrayList<Word>()
        val db = dbHelper.readableDatabase

        val query = "SELECT * FROM ${DatabaseHelper.TABLE_WORDS}"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID))
                val wordText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION))
                val partOfSpeech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH))

                val wordObj = Word(
                    id = id,
                    user_id = userId,
                    word = wordText,
                    meaning = meaning,
                    pronunciation = pronunciation,
                    part_of_speech = partOfSpeech,
                    isSelected = false
                )

                listWords.add(wordObj)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listWords
    }

    fun getWordsByUserId(userId: Int): ArrayList<Word> {
        val listWords = ArrayList<Word>()
        val db = dbHelper.readableDatabase

        val query = "SELECT * FROM ${DatabaseHelper.TABLE_WORDS} WHERE ${DatabaseHelper.COLUMN_WORD_USER_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
                val userIdCol = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID))
                val wordText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunciation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION))
                val partOfSpeech = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH))

                val wordObj = Word(
                    id = id,
                    user_id = userIdCol,
                    word = wordText,
                    meaning = meaning,
                    pronunciation = pronunciation,
                    part_of_speech = partOfSpeech,
                    isSelected = false
                )

                listWords.add(wordObj)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return listWords
    }

    fun updateWord(word: Word): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_WORD_USER_ID, word.user_id)
            put(DatabaseHelper.COLUMN_WORD_WORD, word.word)
            put(DatabaseHelper.COLUMN_WORD_MEANING, word.meaning)
            put(DatabaseHelper.COLUMN_WORD_PRONUNCIATION, word.pronunciation)
            put(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH, word.part_of_speech)
        }

        val result = db.update(
            DatabaseHelper.TABLE_WORDS,
            values,
            "${DatabaseHelper.COLUMN_WORD_ID} = ?",
            arrayOf(word.id.toString())
        )

        db.close()
        return result
    }

    fun deleteWord(wordId: Int): Int {
        val db = dbHelper.writableDatabase

        val result = db.delete(
            DatabaseHelper.TABLE_WORDS,
            "${DatabaseHelper.COLUMN_WORD_ID} = ?",
            arrayOf(wordId.toString())
        )

        db.close()
        return result
    }
}