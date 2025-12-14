package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.project.data.model.Word

class WordDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * THÊM TỪ MỚI
     * Logic: Thêm vào bảng WORDS -> Lấy ID -> Thêm tiếp vào bảng WORD_PROGRESS
     */
    fun addWord(word: Word): Long {
        val db = dbHelper.writableDatabase
        var result: Long = -1

        db.beginTransaction() // Bắt đầu giao dịch
        try {
            // 1. Insert vào bảng WORDS
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_WORD_USER_ID, word.user_id)
                put(DatabaseHelper.COLUMN_WORD_WORD, word.word)
                put(DatabaseHelper.COLUMN_WORD_MEANING, word.meaning)
                put(DatabaseHelper.COLUMN_WORD_PRONUNCIATION, word.pronunciation)
                put(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH, word.part_of_speech)
            }

            result = db.insert(DatabaseHelper.TABLE_WORDS, null, values)

            // 2. Nếu thêm word thành công (result là ID mới tạo), thêm tiếp vào WORD_PROGRESS
            if (result > -1) {
                val progressValues = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WP_USER_ID, word.user_id)
                    put(DatabaseHelper.COLUMN_WP_WORD_ID, result.toInt())
                    put(DatabaseHelper.COLUMN_WP_STATUS, "new") // Mặc định trạng thái new
                    put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 0)
                }
                db.insert(DatabaseHelper.TABLE_WORD_PROGRESS, null, progressValues)
            }

            db.setTransactionSuccessful() // Đánh dấu giao dịch thành công
        } catch (e: Exception) {
            e.printStackTrace()
            result = -1 // Nếu lỗi thì trả về -1
        } finally {
            db.endTransaction() // Kết thúc giao dịch
            db.close()
        }

        return result
    }

    /**
     * XÓA TỪ
     * Logic: Xóa bên bảng WORD_PROGRESS trước -> Xóa bên bảng WORDS sau
     */
    fun deleteWord(wordId: Int): Int {
        val db = dbHelper.writableDatabase
        var result = 0

        db.beginTransaction()
        try {
            // 1. Xóa dữ liệu tiến độ (bảng con) trước
            db.delete(
                DatabaseHelper.TABLE_WORD_PROGRESS,
                "${DatabaseHelper.COLUMN_WP_WORD_ID} = ?",
                arrayOf(wordId.toString())
            )

            // 2. Xóa từ vựng (bảng cha) sau
            result = db.delete(
                DatabaseHelper.TABLE_WORDS,
                "${DatabaseHelper.COLUMN_WORD_ID} = ?",
                arrayOf(wordId.toString())
            )

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return result
    }

    /**
     * UPDATE TỪ (Chỉ update nội dung chữ, nghĩa... không ảnh hưởng progress)
     */
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

    /**
     * TẠO DỮ LIỆU MẪU
     * Cập nhật: Thêm word xong cũng thêm luôn progress cho các từ mẫu này
     */
    fun seedDefaultWordsForUser(userId: Int) {
        val db = dbHelper.writableDatabase

        // 1. Kiểm tra xem user này đã có dữ liệu chưa
        val cursor = db.rawQuery(
            "SELECT count(*) FROM ${DatabaseHelper.TABLE_WORDS} WHERE ${DatabaseHelper.COLUMN_WORD_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

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
                // Insert Word
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_WORD_USER_ID, word.user_id)
                    put(DatabaseHelper.COLUMN_WORD_WORD, word.word)
                    put(DatabaseHelper.COLUMN_WORD_MEANING, word.meaning)
                    put(DatabaseHelper.COLUMN_WORD_PRONUNCIATION, word.pronunciation)
                    put(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH, word.part_of_speech)
                }
                val newWordId = db.insert(DatabaseHelper.TABLE_WORDS, null, values)

                // Insert Progress ngay lập tức cho từ mẫu này
                if (newWordId > -1) {
                    val progressValues = ContentValues().apply {
                        put(DatabaseHelper.COLUMN_WP_USER_ID, word.user_id)
                        put(DatabaseHelper.COLUMN_WP_WORD_ID, newWordId)
                        put(DatabaseHelper.COLUMN_WP_STATUS, "new")
                        put(DatabaseHelper.COLUMN_WP_REVIEW_COUNT, 0)
                    }
                    db.insert(DatabaseHelper.TABLE_WORD_PROGRESS, null, progressValues)
                }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // --- CÁC HÀM GET KHÔNG THAY ĐỔI ---

    fun getAllWords(): ArrayList<Word> {
        val listWords = ArrayList<Word>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_WORDS}"
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                listWords.add(parseWord(cursor))
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
                listWords.add(parseWord(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listWords
    }

    fun getWordById(id: Int): Word? {
        val db = dbHelper.readableDatabase
        var word: Word? = null
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_WORDS} WHERE ${DatabaseHelper.COLUMN_WORD_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            word = parseWord(cursor)
        }
        cursor.close()
        db.close()
        return word
    }

    // Hàm helper để parse cursor ra Word object cho gọn code
    private fun parseWord(cursor: Cursor): Word {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_ID))
        val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_USER_ID))
        val wordText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_WORD))
        val meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORD_MEANING))

        // Xử lý null an toàn cho các cột optional
        val pronunIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_PRONUNCIATION)
        val pronunciation = if (pronunIndex != -1 && !cursor.isNull(pronunIndex))
            cursor.getString(pronunIndex) else null

        val posIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WORD_PART_OF_SPEECH)
        val partOfSpeech = if (posIndex != -1 && !cursor.isNull(posIndex))
            cursor.getString(posIndex) else null

        return Word(
            id = id,
            user_id = userId,
            word = wordText,
            meaning = meaning,
            pronunciation = pronunciation,
            part_of_speech = partOfSpeech,
            isSelected = false
        )
    }
}