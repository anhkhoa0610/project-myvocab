package com.example.project.ui.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.project.R
import com.example.project.data.local.CategoryDAO
import com.example.project.data.local.DictionaryWordDAO
import com.example.project.data.model.Category
import com.example.project.data.model.DictionaryWord
import com.example.project.ui.base.BaseActivity

class DictionaryManagementActivity : BaseActivity() {

    private var wordList = ArrayList<DictionaryWord>()
    private var filteredList = ArrayList<DictionaryWord>()
    private lateinit var adapter: DictionaryWordAdapter
    private lateinit var listView: ListView
    private lateinit var dictionaryWordDAO: DictionaryWordDAO
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var spinnerCategory: Spinner
    private lateinit var etSearch: EditText
    private var categoryList = ArrayList<Category>()
    private var selectedCategoryId: Int = 0 // 0 means "All Categories"

    private val addWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadDataFromDB()
            Toast.makeText(this, "Dictionary word added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val editWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadDataFromDB()
            Toast.makeText(this, "Dictionary word updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_management)

        setHeaderTitle("Dictionary Management")
        setupAddButton()

        listView = findViewById(R.id.lvDictionaryWords)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etSearch = findViewById(R.id.etSearch)
        
        dictionaryWordDAO = DictionaryWordDAO(this)
        categoryDAO = CategoryDAO(this)

        setupCategorySpinner()
        setupSearchListener()
        loadDataFromDB()
    }

    private fun setupCategorySpinner() {
        // Load categories
        categoryList = categoryDAO.getAllCategories()
        
        // Add "All Categories" option at the beginning
        val allCategory = Category(0, "All Categories", "", "", "")
        val spinnerItems = ArrayList<Category>()
        spinnerItems.add(allCategory)
        spinnerItems.addAll(categoryList)

        // Create adapter for spinner
        val spinnerAdapter = object : ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItems
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = spinnerItems[position].name
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).text = spinnerItems[position].name
                return view
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // Set listener
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryId = spinnerItems[position].id
                filterWords()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupSearchListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterWords()
            }
        })
    }

    private fun loadDataFromDB() {
        wordList = dictionaryWordDAO.getAllWords()
        filterWords()
    }

    private fun filterWords() {
        val searchQuery = etSearch.text.toString().trim().lowercase()
        
        filteredList.clear()
        
        for (word in wordList) {
            // Check category filter
            val matchesCategory = selectedCategoryId == 0 || word.category_id == selectedCategoryId
            
            // Check search query
            val matchesSearch = searchQuery.isEmpty() || 
                    word.word.lowercase().contains(searchQuery) || 
                    word.meaning.lowercase().contains(searchQuery)
            
            if (matchesCategory && matchesSearch) {
                filteredList.add(word)
            }
        }
        
        adapter = DictionaryWordAdapter(this, filteredList, categoryList)
        listView.adapter = adapter
    }

    private fun setupAddButton() {
        val btnAdd = ImageView(this)
        btnAdd.setImageResource(R.drawable.ic_add)
        btnAdd.setPadding(15, 15, 15, 15)

        val outValue = android.util.TypedValue()
        theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        btnAdd.setBackgroundResource(outValue.resourceId)

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddDictionaryWordActivity::class.java)
            addWordLauncher.launch(intent)
        }

        frameRightAction.addView(btnAdd)
    }
}
