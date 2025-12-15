package com.example.project.data.local

import android.content.ContentValues
import android.content.Context
import com.example.project.data.model.Category

class CategoryDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Thêm category mới
    fun addCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CAT_NAME, category.name)
            put(DatabaseHelper.COLUMN_CAT_DESCRIPTION, category.description)
            put(DatabaseHelper.COLUMN_CAT_ICON, category.icon)
            put(DatabaseHelper.COLUMN_CAT_COLOR, category.color)
        }
        val result = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values)
        db.close()
        return result
    }

    // Lấy tất cả categories
    fun getAllCategories(): ArrayList<Category> {
        val categoryList = ArrayList<Category>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORIES}", null)

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_DESCRIPTION)) ?: "",
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ICON)) ?: "",
                    color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_COLOR)) ?: ""
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categoryList
    }

    // Lấy category theo ID
    fun getCategoryById(id: Int): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CATEGORIES} WHERE ${DatabaseHelper.COLUMN_CAT_ID} = ?",
            arrayOf(id.toString())
        )

        var category: Category? = null
        if (cursor.moveToFirst()) {
            category = Category(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_DESCRIPTION)) ?: "",
                icon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ICON)) ?: "",
                color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_COLOR)) ?: ""
            )
        }
        cursor.close()
        db.close()
        return category
    }

    // Cập nhật category
    fun updateCategory(category: Category): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CAT_NAME, category.name)
            put(DatabaseHelper.COLUMN_CAT_DESCRIPTION, category.description)
            put(DatabaseHelper.COLUMN_CAT_ICON, category.icon)
            put(DatabaseHelper.COLUMN_CAT_COLOR, category.color)
        }

        // Trả về số dòng bị ảnh hưởng (thường là 1 nếu thành công)
        val result = db.update(
            DatabaseHelper.TABLE_CATEGORIES,
            values,
            "${DatabaseHelper.COLUMN_CAT_ID} = ?",
            arrayOf(category.id.toString())
        )
        db.close()
        return result
    }

    // Xóa category theo ID
    fun deleteCategory(id: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_CATEGORIES,
            "${DatabaseHelper.COLUMN_CAT_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result
    }
}
