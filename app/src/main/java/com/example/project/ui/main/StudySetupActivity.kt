package com.example.project.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project.R
import com.example.project.data.model.Word
import com.google.android.material.navigation.NavigationView

class StudySetupActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var lvSelection: ListView
    private lateinit var btnStart: Button

    private var allWords = ArrayList<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_setup)
        initControls()
        setupMenu()
        loadData()
        setupEventStart()
    }

    private fun initControls() {
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu = findViewById(R.id.btnMenu)
        navigationView = findViewById(R.id.nav_view)
        lvSelection = findViewById(R.id.lvWordSelection)
        btnStart = findViewById(R.id.btnStartFlashcard)
    }

    private fun setupMenu() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                R.id.nav_exit -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadData() {
        allWords = intent.getParcelableArrayListExtra("all_words") ?: ArrayList()

        val adapter = object : ArrayAdapter<Word>(this, R.layout.item_selection, allWords) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_selection, parent, false)
                val word = getItem(position)!!

                val tvWord = view.findViewById<TextView>(R.id.tvWordSel)
                val tvMeaning = view.findViewById<TextView>(R.id.tvMeaningSel)
                val checkBox = view.findViewById<CheckBox>(R.id.cbSelect)

                tvWord.text = word.word
                tvMeaning.text = word.meaning
                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = word.isSelected
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    word.isSelected = isChecked
                }
                view.setOnClickListener {
                    checkBox.isChecked = !checkBox.isChecked
                }
                return view
            }
        }
        lvSelection.adapter = adapter
    }

    private fun setupEventStart() {
        btnStart.setOnClickListener {
            val selectedList = allWords.filter { it.isSelected } as ArrayList<Word>

            if (selectedList.isEmpty()) {
                Toast.makeText(this, "Bạn chưa chọn từ nào!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, FlashCardActivity::class.java)
                intent.putParcelableArrayListExtra("list_word", selectedList)
                startActivity(intent)
            }
        }
    }
}