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

    // Lấy userId thông qua UserSession
    private val userId: Int by lazy {
        com.example.project.utils.UserSession.getUserId(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_status)
        setHeaderTitle("Word Status")

        wordProgressDAO = WordProgressDAO(this)
        rvVocabulary = findViewById(R.id.rvVocabularyStatus)
        tabLayout = findViewById(R.id.tabLayoutStatus)
        etSearch = findViewById(R.id.etSearch)

        rvVocabulary.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvVocabulary.addItemDecoration(dividerItemDecoration)

        adapter = VocabularyStatusAdapter(this, emptyList()) { wordId, newStatus ->
            // Sử dụng biến 'userId' ở đây
            wordProgressDAO.updateStatus(userId, wordId, newStatus)
            loadDataForCurrentTab()
        }
        rvVocabulary.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                etSearch.setText("")
                loadDataForCurrentTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        setupSearchFunction()
        loadDataForCurrentTab()
    }

    private fun setupSearchFunction() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        if (query.isEmpty()) {
            adapter.setData(originalList)
        } else {
            val filteredList = originalList.filter { item ->
                item.word.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                        item.meaning.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
            adapter.setData(filteredList)
        }
    }

    private fun loadDataForCurrentTab() {
        val statusToCheck = when (tabLayout.selectedTabPosition) {
            0 -> WordStatus.NEW
            1 -> WordStatus.LEARNING
            2 -> WordStatus.MASTERED
            else -> WordStatus.NEW
        }

        // Sử dụng biến 'userId' ở đây giống file mẫu
        originalList = wordProgressDAO.getVocabularyByStatus(userId, statusToCheck)

        val currentSearchText = etSearch.text.toString()
        if (currentSearchText.isNotEmpty()) {
            filterList(currentSearchText)
        } else {
            adapter.setData(originalList)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDataForCurrentTab()
    }
}