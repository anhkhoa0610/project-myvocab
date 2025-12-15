package com.example.project.ui.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.project.R
import com.example.project.data.local.UserDAO
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.ui.base.BaseActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class AdminDashboardActivity : BaseActivity() {

    private lateinit var cardManageWords: CardView
    private lateinit var cardManageCategories: CardView
    private lateinit var cardManageUsers: CardView
    private lateinit var lineChartUsers: LineChart
    private lateinit var tvTotalWords: TextView
    private lateinit var tvNewUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Check if user is admin
        val userRole = com.example.project.utils.UserSession.getUserRole(this)
        if (userRole != "admin") {
            Toast.makeText(this, "Bạn không có quyền truy cập trang này", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setHeaderTitle("Admin Dashboard")
        initViews()
        setupClickListeners()
        setupUserLineChart()
        getTotalWords()
        getNewUser()
    }

    private fun initViews() {
        cardManageWords = findViewById(R.id.cardManageWords)
        cardManageCategories = findViewById(R.id.cardManageCategories)
        cardManageUsers = findViewById(R.id.cardManageUsers)
        lineChartUsers = findViewById(R.id.lineChartUsers)
        tvTotalWords = findViewById(R.id.tvTotalWords)
        tvNewUser = findViewById(R.id.tvNewUser)
    }

    private fun setupClickListeners() {
        cardManageWords.setOnClickListener {
            startActivity(Intent(this, DictionaryManagementActivity::class.java))
        }

        cardManageCategories.setOnClickListener {
            startActivity(Intent(this, ManageCategoryActivity::class.java))
        }

        cardManageUsers.setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }
    }

    private fun setupUserLineChart() {
        val dao = UserDAO(this)
        val counts = dao.getUserCountLast7Days()

        val entries = counts.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "User đăng ký (7 ngày)").apply {
            color = Color.BLUE
            valueTextSize = 10f
            circleRadius = 4f
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            setDrawValues(true)
        }

        lineChartUsers.data = LineData(dataSet)

        // X Axis
        val labels = listOf("D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "Today")
        lineChartUsers.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(labels)
        }

        // Y Axis
        lineChartUsers.axisLeft.apply {
            axisMinimum = 0f
            granularity = 1f
            isGranularityEnabled = true
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        lineChartUsers.axisRight.isEnabled = false

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        lineChartUsers.description.isEnabled = false
        lineChartUsers.animateX(1000)
        lineChartUsers.invalidate()
    }

    fun getTotalWords() {
        val dao = DictionaryWordDAO(this)
        val totalWords = dao.getTotalWordCount()
        tvTotalWords.text = "Tổng số từ trong từ điển: $totalWords"
    }

    fun getNewUser() {
        val userDao = UserDAO(this)
        val totalNewUser = userDao.getLatestUserName()
        tvNewUser.text = "User Mới Đăng Ký Hôm Nay: $totalNewUser"
    }
}
