package com.example.project.ui.statistics

import android.graphics.Color
import com.example.project.data.local.WordDAO
import com.example.project.data.local.WordProgressDAO
import com.example.project.utils.WordStatus
import com.github.mikephil.charting.charts.BarChart
import com.example.project.R
import android.os.Bundle
import com.example.project.ui.base.BaseActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatisticsActivity : BaseActivity() {


    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)


        barChart = findViewById(R.id.barChart)


        setupBarChart()
    }


    private fun setupBarChart() {
        val userId = com.example.project.utils.UserSession.getUserId(this)
        val dao = WordProgressDAO(this)


        val stats = dao.getWordCountByStatus(userId)


        val newCount = stats[WordStatus.NEW] ?: 0
        val learningCount = stats[WordStatus.LEARNING] ?: 0
        val masteredCount = stats[WordStatus.MASTERED] ?: 0


        val entries = listOf(
            BarEntry(0f, newCount.toFloat()),
            BarEntry(1f, learningCount.toFloat()),
            BarEntry(2f, masteredCount.toFloat())
        )


        val dataSet = BarDataSet(entries, "Tiến độ học từ")
        dataSet.colors = listOf(
            Color.parseColor("#03A9F4"), // NEW
            Color.parseColor("#FFC107"), // LEARNING
            Color.parseColor("#4CAF50") // MASTER
        )
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        dataSet.valueFormatter = DefaultValueFormatter(0)
        val barData = BarData(dataSet)
        barData.barWidth = 0.6f


        barChart.data = barData


// X Axis
        val labels = listOf("Mới", "Đang học", "Thành thạo")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)


// Y Axis
        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f      // Không cho số âm
        yAxis.granularity = 1f      // Bước nhảy 1 đơn vị
        yAxis.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false


// Chart config
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }
}