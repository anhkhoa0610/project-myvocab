package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.Level

class LevelDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Thêm level mới
    fun addLevel(level: Level): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LEVEL_NAME, level.name)
            put(DatabaseHelper.COLUMN_LEVEL_COLOR, level.color)
        }
        val result = db.insert(DatabaseHelper.TABLE_LEVELS, null, values)
        db.close()
        return result
    }

    // Lấy tất cả levels
    fun getAllLevels(): ArrayList<Level> {
        val levelList = ArrayList<Level>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_LEVELS} ORDER BY ${DatabaseHelper.COLUMN_LEVEL_ID}", null)

        if (cursor.moveToFirst()) {
            do {
                val level = Level(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_COLOR)) ?: ""
                )
                levelList.add(level)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return levelList
    }

    // Lấy level theo ID
    fun getLevelById(levelId: Int): Level? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_LEVELS} WHERE ${DatabaseHelper.COLUMN_LEVEL_ID} = ?",
            arrayOf(levelId.toString())
        )

        var level: Level? = null
        if (cursor.moveToFirst()) {
            level = Level(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_NAME)),
                color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL_COLOR)) ?: ""
            )
        }
        cursor.close()
        db.close()
        return level
    }

    // Lấy level name theo ID (helper method)
    fun getLevelName(levelId: Int): String {
        return getLevelById(levelId)?.name ?: "Unknown"
    }

    // Update level
    fun updateLevel(level: Level): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LEVEL_NAME, level.name)
            put(DatabaseHelper.COLUMN_LEVEL_COLOR, level.color)
        }
        val result = db.update(
            DatabaseHelper.TABLE_LEVELS,
            values,
            "${DatabaseHelper.COLUMN_LEVEL_ID} = ?",
            arrayOf(level.id.toString())
        )
        db.close()
        return result
    }

    // Delete level
    fun deleteLevel(levelId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_LEVELS,
            "${DatabaseHelper.COLUMN_LEVEL_ID} = ?",
            arrayOf(levelId.toString())
        )
        db.close()
        return result
    }
}
