package com.example.project.ui.statistics

import android.graphics.Color
import android.os.Bundle
import com.example.project.R
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.WordStatus
import com.example.project.utils.UserSession
import com.example.project.data.local.WordProgressDAO
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticsActivity : BaseActivity() {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)

        setupCharts()
    }

    private fun setupCharts() {
        val userId = UserSession.getUserId(this)
        val dao = WordProgressDAO(this)

        val stats = dao.getWordCountByStatus(userId)

        val newCount = stats[WordStatus.NEW] ?: 0
        val learningCount = stats[WordStatus.LEARNING] ?: 0
        val masteredCount = stats[WordStatus.MASTERED] ?: 0

        setupBarChart(newCount, learningCount, masteredCount)
        setupPieChart(newCount, learningCount, masteredCount)
    }

    // ================= BAR CHART =================
    private fun setupBarChart(newCount: Int, learningCount: Int, masteredCount: Int) {
        val entries = listOf(
            BarEntry(0f, newCount.toFloat()),
            BarEntry(1f, learningCount.toFloat()),
            BarEntry(2f, masteredCount.toFloat())
        )

        val dataSet = BarDataSet(entries, "Tiến độ học từ")
        dataSet.colors = listOf(
            Color.parseColor("#03A9F4"), // NEW
            Color.parseColor("#FFC107"), // LEARNING
            Color.parseColor("#4CAF50")  // MASTERED
        )
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueFormatter = DefaultValueFormatter(0)

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData

        val labels = listOf("Mới", "Đang học", "Thành thạo")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.granularity = 1f
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }

    // ================= PIE CHART =================
    private fun setupPieChart(newCount: Int, learningCount: Int, masteredCount: Int) {
        val entries = listOf(
            PieEntry(newCount.toFloat(), "Mới"),
            PieEntry(learningCount.toFloat(), "Đang học"),
            PieEntry(masteredCount.toFloat(), "Thành thạo")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#03A9F4"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#4CAF50")
        )
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueFormatter = DefaultValueFormatter(0)

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 45f
        pieChart.transparentCircleRadius = 50f
        pieChart.centerText = "Tổng từ\n${newCount + learningCount + masteredCount}"
        pieChart.setCenterTextSize(14f)

        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(false)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
