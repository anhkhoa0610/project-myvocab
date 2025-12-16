package com.example.project.ui.vocabStatus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.local.WordProgressDAO
import com.example.project.ui.base.BaseActivity
import com.example.project.utils.WordStatus
import com.google.android.material.tabs.TabLayout
import java.util.Locale

class VocabularyActivity : BaseActivity() {

    private lateinit var wordProgressDAO: WordProgressDAO
    private lateinit var adapter: VocabularyStatusAdapter
    private lateinit var rvVocabulary: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var etSearch: EditText

    private var originalList: List<Vocabulary> = emptyList()

    private val userId: Int by lazy {
        com.example.project.utils.UserSession.getUserId(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_status)

        setControl()
        initData()
        setEvent()
    }

    private fun setControl() {
        setHeaderTitle("Word Status \n Minh Nhựt - Nhóm 2")
        rvVocabulary = findViewById(R.id.rvVocabularyStatus)
        tabLayout = findViewById(R.id.tabLayoutStatus)
        etSearch = findViewById(R.id.etSearch)
        rvVocabulary.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvVocabulary.addItemDecoration(dividerItemDecoration)
    }

    private fun initData() {
        wordProgressDAO = WordProgressDAO(this)
        adapter = VocabularyStatusAdapter(this, emptyList()) { wordId, newStatus ->
            handleStatusUpdate(wordId, newStatus)
        }
        rvVocabulary.adapter = adapter

        loadDataForCurrentTab()
    }

    private fun setEvent() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                etSearch.setText("")
                loadDataForCurrentTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleStatusUpdate(wordId: Int, newStatus: String) {
        wordProgressDAO.updateStatus(userId, wordId, newStatus)
        loadDataForCurrentTab()
    }

    private fun loadDataForCurrentTab() {
        val currentTabPosition = tabLayout.selectedTabPosition

        if (currentTabPosition == 3) {
            originalList = wordProgressDAO.getAllWordsWithStatus(userId)
        } else {
            val statusToCheck = when (currentTabPosition) {
                0 -> WordStatus.NEW
                1 -> WordStatus.LEARNING
                2 -> WordStatus.MASTERED
                else -> WordStatus.NEW
            }
            originalList = wordProgressDAO.getVocabularyByStatus(userId, statusToCheck)
        }

        val currentSearchText = etSearch.text.toString()
        if (currentSearchText.isNotEmpty()) {
            filterList(currentSearchText)
        } else {
            bindData(originalList)
        }
    }

    private fun bindData(list: List<Vocabulary>) {
        adapter.setData(list)
    }

    private fun filterList(query: String) {
        if (query.isEmpty()) {
            bindData(originalList)
        } else {
            val filteredList = originalList.filter { item ->
                item.word.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                        item.meaning.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
            bindData(filteredList)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDataForCurrentTab()
    }
}