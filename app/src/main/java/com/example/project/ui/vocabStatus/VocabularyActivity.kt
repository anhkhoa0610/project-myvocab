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

        // --- BƯỚC 1: THÊM TAB "ALL" VÀO GIAO DIỆN ---
        // Nếu trong XML bạn chưa thêm TabItem thứ 4, bạn có thể bỏ comment dòng dưới để thêm bằng code:
        // tabLayout.addTab(tabLayout.newTab().setText("All"))

        rvVocabulary.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvVocabulary.addItemDecoration(dividerItemDecoration)

        // --- BƯỚC 2: SỬA LỖI KHỞI TẠO ADAPTER ---
        // (Lỗi cũ: bạn gọi nhầm tên hàm getAllWordsWithStatus ở đây)
        adapter = VocabularyStatusAdapter(this, emptyList()) { wordId, newStatus ->
            wordProgressDAO.updateStatus(userId, wordId, newStatus)

            // Logic UX: Nếu đang ở Tab "All" hoặc "Search" thì không cần reload
            // Nhưng để đơn giản, ta cứ reload lại list để cập nhật đúng vị trí
            loadDataForCurrentTab()
        }
        rvVocabulary.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                etSearch.setText("") // Xóa text tìm kiếm khi chuyển tab
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

    // --- BƯỚC 3: TÍCH HỢP HÀM getAllWordsWithStatus ---
    private fun loadDataForCurrentTab() {
        val currentTabPosition = tabLayout.selectedTabPosition

        // Giả sử thứ tự Tab là: 0:New, 1:Learning, 2:Mastered, 3:ALL
        if (currentTabPosition == 3) {
            // Đây là lúc sử dụng hàm "thần thánh" bạn đã viết trong DAO
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

        // Logic giữ lại kết quả tìm kiếm (nếu có)
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