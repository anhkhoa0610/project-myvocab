package com.example.project.ui.vocabStatus

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StatusSpinnerAdapter(
    context: Context,
    resource: Int,
    objects: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    // Màu sắc định nghĩa sẵn (Bạn có thể thay đổi mã Hex theo ý thích)
    private val colorNew = Color.parseColor("#757575")      // Màu xám đậm
    private val colorLearning = Color.parseColor("#FF9800") // Màu cam
    private val colorMastered = Color.parseColor("#4CAF50") // Màu xanh lá

    // 1. Hàm này xử lý hiển thị khi Spinner ĐÓNG (View hiển thị trên dòng item)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        setColor(view, position)
        return view
    }

    // 2. Hàm này xử lý hiển thị khi Spinner MỞ (Danh sách dropdown)
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        setColor(view, position)
        return view
    }

    // Hàm chung để set màu
    private fun setColor(view: View, position: Int) {
        val textView = view as TextView
        val itemText = getItem(position)

        when (itemText) {
            "New" -> {
                textView.setTextColor(colorNew)
                // textView.setTypeface(null, Typeface.NORMAL) // Nếu muốn chỉnh font
            }
            "Learning" -> {
                textView.setTextColor(colorLearning)
                // textView.setTypeface(null, Typeface.BOLD) // Ví dụ: in đậm
            }
            "Mastered" -> {
                textView.setTextColor(colorMastered)
                // textView.setTypeface(null, Typeface.BOLD)
            }
            else -> textView.setTextColor(Color.BLACK)
        }
    }
}