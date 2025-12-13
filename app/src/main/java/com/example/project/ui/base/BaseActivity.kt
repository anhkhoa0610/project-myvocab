package com.example.project.ui.base

import android.content.Intent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project.R
import com.example.project.ui.main.MyVocabActivity
import com.example.project.ui.flashcards.StudySetupActivity
import com.example.project.ui.setting.SettingsActivity
import com.google.android.material.navigation.NavigationView

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var tvTitle: TextView
    protected lateinit var frameRightAction: FrameLayout

    override fun setContentView(layoutResID: Int) {
        // Gắn layout cha + layout con
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.container_view)

        // Gắn layout của activity con vào container
        layoutInflater.inflate(layoutResID, activityContainer, true)

        super.setContentView(fullView)
        initBaseControls()
    }

    private fun initBaseControls() {
        drawerLayout = findViewById(R.id.drawer_layout)
        tvTitle = findViewById(R.id.tvTitleBase)
        frameRightAction = findViewById(R.id.frameRightAction)

        val navView = findViewById<NavigationView>(R.id.nav_view_base)
        val btnMenu = findViewById<ImageView>(R.id.btnMenuBase)

        btnMenu.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        navView.setNavigationItemSelectedListener(this)

        // Bottom Navigation
        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav_base)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is com.example.project.ui.auth.DashboardActivity) {
                        val intent =
                            Intent(this, com.example.project.ui.auth.DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.nav_setting -> {
                    if (this !is com.example.project.ui.setting.SettingsActivity) {
                        val intent = Intent(
                            this,
                            com.example.project.ui.setting.SettingsActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.nav_profile -> {
                    // Exit
                    finishAffinity()
                    true
                }

                else -> false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            // Home → Dashboard
            R.id.nav_home -> {
                if (this !is com.example.project.ui.auth.DashboardActivity) {
                    val intent =
                        Intent(this, com.example.project.ui.auth.DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }

            // My Vocabularies
            R.id.nav_my_vocab -> {
                if (this !is MyVocabActivity) {
                    val intent = Intent(this, MyVocabActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }

            // Trang Flashcard → chuyển sang trang CHUẨN BỊ
            R.id.nav_flashcard -> {
                if (this !is StudySetupActivity) {
                    val intent = Intent(this, StudySetupActivity::class.java)
                    startActivity(intent)
                }
            }

            // Dictionary
            R.id.nav_dictionary -> {
                if (this !is com.example.project.ui.dictionary.DictionaryActivity) {
                    val intent = Intent(
                        this,
                        com.example.project.ui.dictionary.DictionaryActivity::class.java
                    )
                    startActivity(intent)
                }
            }
            //setting
            R.id.nav_setting -> {
                if (this !is com.example.project.ui.setting.SettingsActivity) {
                    val intent =
                        Intent(this, com.example.project.ui.setting.SettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                true
            }

            // Logout
            R.id.nav_logout -> {
                com.example.project.utils.UserSession.clearSession(this)
                val intent = Intent(this, com.example.project.ui.auth.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            // Thoát app
            R.id.nav_exit -> {
                finishAffinity()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun setHeaderTitle(title: String) {
        tvTitle.text = title
    }

    override fun onBackPressed() {
        if (::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
