package com.example.project.ui.static_activity

import android.os.Bundle
import android.widget.TextView
import com.example.project.R
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.ui.base.BaseActivity

// Thêm các thư viện cần thiết cho Biểu đồ
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis // Cần cho XAxis.XAxisPosition
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StatisticsActivity : BaseActivity() {

    private lateinit var dictDAO: DictionaryWordDAO
    private lateinit var barChart: BarChart // Khai báo biểu đồ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        dictDAO = DictionaryWordDAO(this)
        barChart = findViewById(R.id.barChart) // Liên kết với layout

        // --- LẤY DỮ LIỆU THỐNG KÊ (TEXT VIEW) ---
        val totalWords = dictDAO.getTotalWordsAdded()
        val masteredWords = dictDAO.getTotalWordsMastered(masteryLevel = 5)

        findViewById<TextView>(R.id.tv_total_words).apply {
            text = "Tổng số từ đã thêm: $totalWords"
        }

//        findViewById<TextView>(R.id.tv_mastered_words).apply {
//            text = "Số từ đã học (Giả định level >= 5): $masteredWords"
//        }

        // --- LẤY VÀ HIỂN THỊ DỮ LIỆU BIỂU ĐỒ ---
        val levelData = dictDAO.getWordCountByLevel()
        setupBarChart(levelData)
    }

    /**
     * Thiết lập và vẽ Biểu đồ Cột.
     */
    private fun setupBarChart(levelData: Map<String, Int>) {
        if (levelData.isEmpty()) {
            barChart.setNoDataText("Chưa có dữ liệu từ vựng theo cấp độ.")
            barChart.invalidate()
            return
        }

        // Sắp xếp các cấp độ (A1, A2, B1,...) để hiển thị trên trục X đúng thứ tự
        val levels = levelData.keys.toList().sorted()
        val entries = ArrayList<BarEntry>()

        // Tạo các BarEntry (x là vị trí, y là giá trị)
        for ((index, level) in levels.withIndex()) {
            // Kiểm tra null an toàn hơn
            val count = levelData[level] ?: 0
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Số từ theo cấp độ").apply {
            color = Color.rgb(75, 120, 150) // Một màu xanh đẹp
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        barChart.data = barData

        // Cấu hình trục X
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(levels) // Đặt nhãn (A1, A2...)
            granularity = 1f
            isGranularityEnabled = true
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM // Vị trí nhãn ở dưới
        }

        // Cấu hình tổng thể
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1200)
        barChart.invalidate() // Vẽ lại biểu đồ
    }
}