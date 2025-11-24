package com.example.project.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat // Cần thêm thư viện này
import androidx.drawerlayout.widget.DrawerLayout // Cần thêm thư viện này
import com.example.project.R
import com.example.project.data.model.Word
import com.example.project.ui.add_edit_word.AddNewActivity
import com.google.android.material.navigation.NavigationView // Cần thêm thư viện này

class MainActivity : AppCompatActivity() {

    private val wordList = mutableListOf<Word>()

    // Khai báo biến giao diện cũ
    private lateinit var btnAdd: ImageView
    private lateinit var adapter: WordAdapter
    private lateinit var listView: ListView

    // --- KHAI BÁO THÊM BIẾN CHO MENU ---
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnMenu: ImageView
    private lateinit var navigationView: NavigationView
    // -----------------------------------

    private val addWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val newWord = result.data?.getParcelableExtra<Word>("new_word")
            newWord?.let {
                wordList.add(it)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val editWordLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedWord = result.data?.getParcelableExtra<Word>("updated_word")
            val position = result.data?.getIntExtra("position", -1) ?: -1

            if (updatedWord != null && position != -1) {
                wordList[position] = updatedWord
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã cập nhật từ!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Dữ liệu mẫu
        if (wordList.isEmpty()) {
            wordList.addAll(
                listOf(
                    Word("Cat", "Con mèo", "/kæt/", "Noun"),
                    Word("Dog", "Con chó", "/dɒɡ/", "Noun"),
                    Word("Run", "Chạy", "/rʌn/", "Verb")
                )
            )
        }

        // 2. Khởi tạo và gán sự kiện
        setControl()
        setEvent()

        // 3. Setup ListView
        adapter = WordAdapter(this, wordList)
        listView.adapter = adapter
    }

    private fun setControl() {
        listView = findViewById(R.id.lvWords)
        btnAdd = findViewById(R.id.btnAdd)

        // --- ÁNH XẠ CÁC VIEW CỦA MENU ---
        // (Đảm bảo file xml của bạn đã sửa thành DrawerLayout nhé)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Nút 3 gạch (thay vì nút Add, bạn cần chắc chắn trong XML có nút này)
        btnMenu = findViewById(R.id.btnMenu)
    }

    private fun setEvent() {
        // Sự kiện nút Thêm từ (Giữ nguyên)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddNewActivity::class.java)
            addWordLauncher.launch(intent)
        }

        // --- SỰ KIỆN MỞ MENU (Bấm vào nút 3 gạch) ---
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // --- SỰ KIỆN CHỌN ITEM TRONG MENU ---
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Đang ở Home rồi thì chỉ cần đóng menu
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                // KHI BẤM VÀO FLASHCARD -> Sang màn hình Chọn Bài
                R.id.nav_flashcard -> {
                    val intent = Intent(this, StudySetupActivity::class.java)
                    // Truyền danh sách từ sang để bên kia có cái mà chọn
                    intent.putParcelableArrayListExtra("all_words", ArrayList(wordList))
                    startActivity(intent)
                }

                R.id.nav_exit -> {
                    finishAffinity() // Thoát app
                }
            }
            // Đóng menu sau khi chọn xong
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}