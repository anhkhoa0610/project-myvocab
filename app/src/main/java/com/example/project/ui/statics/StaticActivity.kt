package com.example.project.ui.statics

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project.data.MockWordProgress
import com.example.project.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.example.project.ui.base.BaseActivity
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StaticActivity : BaseActivity() {


    private lateinit var barChart: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statics)


        barChart = findViewById(R.id.barChart)
        setupBarChart()
    }


    private fun setupBarChart() {
        val data = MockWordProgress.getMockData()


// Thống kê số từ theo trạng thái
        val newCount = data.count { it.status == "NEW" }
        val learningCount = data.count { it.status == "LEARNING" }
        val masteredCount = data.count { it.status == "MASTERED" }


        val entries = listOf(
            BarEntry(0f, newCount.toFloat()),
            BarEntry(1f, learningCount.toFloat()),
            BarEntry(2f, masteredCount.toFloat())
        )


        val dataSet = BarDataSet(entries, "Số lượng từ")
        dataSet.colors = listOf(
            Color.parseColor("#03A9F4"), // Mới
            Color.parseColor("#FF9800"), // Đang học
            Color.parseColor("#4CAF50")  // Thành thạo
        )

        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        val barData = BarData(dataSet)
        barData.barWidth = 0.6f


        barChart.data = barData


// X Axis
        val labels = listOf("Mới", "Đang học", "Thành thạo")
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)


        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}