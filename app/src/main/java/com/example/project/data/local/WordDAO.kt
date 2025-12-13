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
                val userId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID))
                val wordText =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunciation =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION))
                val partOfSpeech =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH))

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

        val query =
            "SELECT * FROM ${DatabaseHelper.TABLE_WORDS} WHERE ${DatabaseHelper.COLUMN_WORD_USER_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
                val userIdCol =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID))
                val wordText =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
                val meaning =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))
                val pronunciation =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION))
                val partOfSpeech =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH))

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

    // Hàm thêm từ mẫu cho user
    fun seedDefaultWordsForUser(userId: Int) {
        val db = dbHelper.writableDatabase

        // 1. Kiểm tra xem user này đã có dữ liệu chưa
        // Chỉ đếm các từ của RIÊNG user này thôi
        val cursor = db.rawQuery(
            "SELECT count(*) FROM ${DatabaseHelper.TABLE_WORDS} WHERE ${DatabaseHelper.COLUMN_WORD_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        // 2. Nếu đã có từ rồi (>0) thì return, không làm gì cả
        if (count > 0) {
            db.close()
            return
        }

        val sampleWords = listOf(
            Word(0, userId, "Hello", "Xin chào", "/həˈləʊ/", "noun", false),
            Word(0, userId, "Thank you", "Cảm ơn", "/θæŋk juː/", "phrase", false),
            Word(0, userId, "Success", "Thành công", "/səkˈses/", "noun", false),
            Word(0, userId, "Dream", "Giấc mơ", "/driːm/", "noun", false),
            Word(0, userId, "Knowledge", "Kiến thức", "/ˈnɒlɪdʒ/", "noun", false),

            Word(0, userId, "Developer", "Lập trình viên", "/dɪˈveləpə(r)/", "noun", false),
            Word(0, userId, "Computer", "Máy tính", "/kəmˈpjuːtə(r)/", "noun", false),
            Word(0, userId, "Algorithm", "Thuật toán", "/ˈælɡərɪðəm/", "noun", false),
            Word(0, userId, "Database", "Cơ sở dữ liệu", "/ˈdeɪtəbeɪs/", "noun", false),
            Word(0, userId, "Internet", "Mạng Internet", "/ˈɪntənet/", "noun", false),
            Word(0, userId, "Software", "Phần mềm", "/ˈsɒftweə(r)/", "noun", false),
            Word(0, userId, "Hardware", "Phần cứng", "/ˈhɑːdweə(r)/", "noun", false),
            Word(0, userId, "Keyboard", "Bàn phím", "/ˈkiːbɔːd/", "noun", false),
            Word(0, userId, "Screen", "Màn hình", "/skriːn/", "noun", false),

            Word(0, userId, "Project", "Dự án", "/ˈprɒdʒekt/", "noun", false),
            Word(0, userId, "Deadline", "Hạn chót", "/ˈdedlaɪn/", "noun", false),
            Word(0, userId, "Meeting", "Cuộc họp", "/ˈmiːtɪŋ/", "noun", false),
            Word(0, userId, "Team", "Đội nhóm", "/tiːm/", "noun", false),
            Word(0, userId, "Solution", "Giải pháp", "/səˈluːʃn/", "noun", false),
            Word(0, userId, "Goal", "Mục tiêu", "/ɡəʊl/", "noun", false)
        )

        db.beginTransaction()
        try {
            for (word in sampleWords) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WORD_USER_ID, word.user_id)
                    put(DatabaseHelper.COLUMN_WORD_WORD, word.word)
                    put(DatabaseHelper.COLUMN_WORD_MEANING, word.meaning)
                    put(DatabaseHelper.COLUMN_WORD_PRONUNCIATION, word.pronunciation)
                    put(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH, word.part_of_speech)
                }
                db.insert(DatabaseHelper.TABLE_WORDS, null, values)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getWordById(id: Int): Word? {
        val db = dbHelper.readableDatabase
        var word: Word? = null

        val projection = arrayOf(
            DatabaseHelper.COLUMN_WORD_ID,
            DatabaseHelper.COLUMN_WORD_USER_ID,
            DatabaseHelper.COLUMN_WORD_WORD,
            DatabaseHelper.COLUMN_WORD_MEANING,
            DatabaseHelper.COLUMN_WORD_PRONUNCIATION,
            DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH
        )

        val selection = "${DatabaseHelper.COLUMN_WORD_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor: Cursor? = db.query(
            DatabaseHelper.TABLE_WORDS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID)
                val userIdColumnIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID)
                val wordColumnIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD)
                val meaningColumnIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING)
                val pronunciationColumnIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PRONUNCIATION)
                val partOfSpeechColumnIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH)

                val wordId = it.getInt(idColumnIndex)
                val userId = it.getInt(userIdColumnIndex)
                val wordText = it.getString(wordColumnIndex)
                val meaning = it.getString(meaningColumnIndex)
                val pronunciation =
                    it.getStringOrNull(pronunciationColumnIndex) // Vẫn giữ để xử lý null an toàn
                val partOfSpeech =
                    it.getStringOrNull(partOfSpeechColumnIndex)   // Vẫn giữ để xử lý null an toàn

                word = Word(
                    id = wordId,
                    user_id = userId,
                    word = wordText,
                    meaning = meaning,
                    pronunciation = pronunciation,
                    part_of_speech = partOfSpeech
                )
            }
        }
        db.close()
        return word
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
}
