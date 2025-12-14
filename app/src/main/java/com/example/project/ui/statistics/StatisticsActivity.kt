package com.example.project.ui.statistics

import android.graphics.Color
import android.os.Bundle
import com.example.project.R
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.WordStatus
import com.example.project.utils.UserSession
import com.example.project.data.local.WordProgressDAO
import com.example.project.data.local.StudySessionDAO
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : BaseActivity() {

    private lateinit var barChartWords: BarChart
    private lateinit var barChartSessions: BarChart

    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        barChartWords = findViewById(R.id.barChartWords)
        barChartSessions = findViewById(R.id.barChartSessions)

        setupCharts()
    }

    private fun setupCharts() {
        val userId = UserSession.getUserId(this)

        setupWordProgressChart(userId)
        setupStudySessionChart(userId)
    }

    // ================= BAR CHART 1 =================
    // Tiến độ học từ (NEW / LEARNING / MASTERED)
    private fun setupWordProgressChart(userId: Int) {
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
            Color.parseColor("#03A9F4"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#4CAF50")
        )
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueFormatter = DefaultValueFormatter(0)

        barChartWords.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChartWords.xAxis.apply {
            valueFormatter =
                IndexAxisValueFormatter(listOf("Mới", "Đang học", "Thành thạo"))
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }

        barChartWords.axisLeft.axisMinimum = 0f
        barChartWords.axisRight.isEnabled = false
        barChartWords.description.isEnabled = false
        barChartWords.animateY(1000)
        barChartWords.invalidate()
    }

    // ================= BAR CHART 2 =================
    // Thống kê StudySession (số phiên + số từ theo ngày)
    private fun setupStudySessionChart(userId: Int) {
        val dao = StudySessionDAO(this)
        val stats = dao.getDailyStatsAsSessions(userId)

        if (stats.isEmpty()) {
            barChartSessions.clear()
            return
        }

        val entries = stats.mapIndexed { index, session ->
            BarEntry(index.toFloat(), session.wordsCount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Số từ học theo ngày")
        dataSet.color = Color.parseColor("#673AB7")
        dataSet.valueTextSize = 10f
        dataSet.valueFormatter = DefaultValueFormatter(0)

        barChartSessions.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        val labels = stats.map {
            dateFormat.format(it.date)
        }

        barChartSessions.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelRotationAngle = -30f
            setDrawGridLines(false)
        }

        barChartSessions.axisLeft.axisMinimum = 0f
        barChartSessions.axisRight.isEnabled = false
        barChartSessions.description.isEnabled = false
        barChartSessions.animateY(1000)
        barChartSessions.invalidate()
    }
}
