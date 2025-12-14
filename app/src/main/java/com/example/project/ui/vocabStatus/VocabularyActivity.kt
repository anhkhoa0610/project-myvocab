package com.example.project.ui.vocabStatus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var etSearch: EditText // Khai báo biến ô tìm kiếm

    // Biến lưu danh sách gốc lấy từ DB (để restore lại khi xóa chữ tìm kiếm)
    private var originalList: List<Vocabulary> = emptyList()

    // Lấy ID user hiện tại (Sửa lại theo cách lấy session của bạn)
    private val currentUserId: Int
        get() = com.example.project.utils.UserSession.getUserId(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_status)
        setHeaderTitle("Word Status")

        // 1. Khởi tạo & Ánh xạ View (Khớp với XML của bạn)
        wordProgressDAO = WordProgressDAO(this)
        rvVocabulary = findViewById(R.id.rvVocabularyStatus)
        tabLayout = findViewById(R.id.tabLayoutStatus)
        etSearch = findViewById(R.id.etSearch) // ID này phải khớp với XML (@+id/etSearch)

        // 2. Setup RecyclerView
        rvVocabulary.layoutManager = LinearLayoutManager(this)
        // Thêm đường kẻ ngang ngăn cách item
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvVocabulary.addItemDecoration(dividerItemDecoration)

        // Khởi tạo Adapter
        adapter = VocabularyStatusAdapter(this, emptyList()) { wordId, newStatus ->
            // Update DB khi chọn Spinner
            wordProgressDAO.updateStatus(currentUserId, wordId, newStatus)

            // Reload lại list để cập nhật giao diện (ví dụ từ New -> Learning thì biến mất khỏi tab New)
            loadDataForCurrentTab()
        }
        rvVocabulary.adapter = adapter

        // 3. Setup TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Khi chuyển tab, nên xóa chữ tìm kiếm cũ để tránh rối
                etSearch.setText("")
                loadDataForCurrentTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 4. [QUAN TRỌNG] Setup chức năng tìm kiếm
        setupSearchFunction()

        // 5. Load dữ liệu ban đầu
        loadDataForCurrentTab()
    }

    /**
     * Hàm lắng nghe sự kiện gõ phím
     */
    private fun setupSearchFunction() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Gọi hàm lọc mỗi khi nội dung thay đổi
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Logic lọc danh sách từ originalList
     */
    private fun filterList(query: String) {
        if (query.isEmpty()) {
            // Nếu ô tìm kiếm rỗng -> Hiển thị lại toàn bộ danh sách gốc
            adapter.setData(originalList)
        } else {
            // Lọc danh sách: Tìm từ hoặc nghĩa chứa từ khóa (không phân biệt hoa thường)
            val filteredList = originalList.filter { item ->
                item.word.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                        item.meaning.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
            // Cập nhật Adapter với danh sách đã lọc
            adapter.setData(filteredList)
        }
    }

    /**
     * Hàm tải dữ liệu từ DB dựa trên Tab đang chọn
     */
    private fun loadDataForCurrentTab() {
        val statusToCheck = when (tabLayout.selectedTabPosition) {
            0 -> WordStatus.NEW
            1 -> WordStatus.LEARNING
            2 -> WordStatus.MASTERED
            else -> WordStatus.NEW
        }

        // 1. Lấy dữ liệu từ DB lưu vào danh sách gốc
        originalList = wordProgressDAO.getVocabularyByStatus(currentUserId, statusToCheck)

        // 2. Kiểm tra xem đang có từ khóa tìm kiếm không?
        val currentSearchText = etSearch.text.toString()
        if (currentSearchText.isNotEmpty()) {
            // Nếu đang tìm kiếm, lọc luôn trên danh sách mới tải về
            filterList(currentSearchText)
        } else {
            // Nếu không tìm kiếm, hiển thị tất cả
            adapter.setData(originalList)
        }
    }

    override fun onResume() {
        super.onResume()
        // Load lại khi quay lại màn hình
        loadDataForCurrentTab()
    }
}