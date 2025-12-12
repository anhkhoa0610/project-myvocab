package com.example.project.ui.dictionary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.model.DictionaryWord
import com.example.project.ui.base.BaseActivity

class DictionaryActivity : BaseActivity() {

    private lateinit var etSearch: EditText
    private lateinit var tvTabAll: TextView
    private lateinit var tvTabFavorites: TextView
    private lateinit var lvDictionaryWords: ListView
    private lateinit var tvEmptyState: TextView
    
    private lateinit var dictionaryDAO: DictionaryWordDAO
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var adapter: DictionaryAdapter
    
    private var allWords = ArrayList<DictionaryWord>()
    private var currentTab = Tab.ALL

    private enum class Tab {
        ALL, FAVORITES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        setHeaderTitle("Dictionary")
        
        initViews()
        initData()
        setupListeners()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        tvTabAll = findViewById(R.id.tvTabAll)
        tvTabFavorites = findViewById(R.id.tvTabFavorites)
        lvDictionaryWords = findViewById(R.id.lvDictionaryWords)
        tvEmptyState = findViewById(R.id.tvEmptyState)
    }

    private fun initData() {
        dictionaryDAO = DictionaryWordDAO(this)
        categoryDAO = CategoryDAO(this)
        
        // Load all words (data đã được seed ở LoginActivity)
        loadWords()
    }

    private fun setupListeners() {
        // Search listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    loadWords()
                } else {
                    searchWords(query)
                }
            }
        })

        // Tab listeners
        tvTabAll.setOnClickListener {
            switchTab(Tab.ALL)
        }

        tvTabFavorites.setOnClickListener {
            switchTab(Tab.FAVORITES)
        }
    }

    private fun loadWords() {
        allWords = when (currentTab) {
            Tab.ALL -> dictionaryDAO.getAllWords()
            Tab.FAVORITES -> dictionaryDAO.getFavoriteWords()
        }
        
        updateUI()
    }

    private fun searchWords(query: String) {
        val searchResults = dictionaryDAO.searchWords(query)
        
        // Filter by current tab
        allWords = if (currentTab == Tab.FAVORITES) {
            ArrayList(searchResults.filter { it.is_favorite })
        } else {
            searchResults
        }
        
        updateUI()
    }

    private fun switchTab(tab: Tab) {
        currentTab = tab
        
        // Update tab UI
        when (tab) {
            Tab.ALL -> {
                tvTabAll.setTextColor(resources.getColor(android.R.color.black, null))
                tvTabAll.textSize = 16f
                tvTabAll.setTypeface(null, android.graphics.Typeface.BOLD)
                
                tvTabFavorites.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                tvTabFavorites.textSize = 16f
                tvTabFavorites.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            Tab.FAVORITES -> {
                tvTabFavorites.setTextColor(resources.getColor(android.R.color.black, null))
                tvTabFavorites.textSize = 16f
                tvTabFavorites.setTypeface(null, android.graphics.Typeface.BOLD)
                
                tvTabAll.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                tvTabAll.textSize = 16f
                tvTabAll.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
        
        // Clear search and reload
        etSearch.text.clear()
        loadWords()
    }

    private fun updateUI() {
        if (allWords.isEmpty()) {
            lvDictionaryWords.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            
            tvEmptyState.text = when (currentTab) {
                Tab.ALL -> "No words found.\nTry searching for something!"
                Tab.FAVORITES -> "No favorite words yet.\nTap ⭐ to add favorites!"
            }
        } else {
            lvDictionaryWords.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
            
            adapter = DictionaryAdapter(this, allWords) { word ->
                toggleFavorite(word)
            }
            lvDictionaryWords.adapter = adapter
        }
    }

    private fun toggleFavorite(word: DictionaryWord) {
        val success = dictionaryDAO.toggleFavorite(word.id)
        if (success) {
            word.is_favorite = !word.is_favorite
            adapter.notifyDataSetChanged()
            
            val message = if (word.is_favorite) {
                "Added to favorites!"
            } else {
                "Removed from favorites!"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            
            // Reload nếu đang ở tab Favorites
            if (currentTab == Tab.FAVORITES) {
                loadWords()
            }
        }
    }
}
