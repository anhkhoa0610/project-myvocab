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
import com.example.project.ui.admin.AdminDashboardActivity
import com.example.project.ui.auth.DashboardActivity
import com.example.project.ui.auth.LoginActivity
import com.example.project.ui.dictionary.DictionaryActivity
import com.example.project.ui.flashcards.StudySetupActivity
import com.example.project.ui.main.MyVocabActivity
import com.example.project.ui.setting.SettingsActivity
import com.example.project.utils.UserSession
import com.example.project.ui.vocabStatus.VocabularyActivity
import com.google.android.material.navigation.NavigationView

abstract class BaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var tvTitle: TextView
    protected lateinit var frameRightAction: FrameLayout

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.container_view)

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

        // Check if user is admin and show/hide admin menu
        val userRole = UserSession.getUserRole(this)
        val adminMenuItem = navView.menu.findItem(R.id.nav_admin_dashboard)
        adminMenuItem?.isVisible = userRole == "admin"

        // Bottom Navigation
        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottom_nav_base
            )

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is DashboardActivity) {
                        startActivity(
                            Intent(this, DashboardActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        finish()
                    }
                    true
                }

                R.id.nav_setting -> {
                    if (this !is SettingsActivity) {
                        startActivity(
                            Intent(this, SettingsActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        finish()
                    }
                    true
                }

                R.id.nav_exit -> {
                    finishAffinity()
                    true
                }

                else -> false
            }
        }
    }

 // update name
    private fun updateUserNameInDrawer(navView: NavigationView? = null) {
        val navigationView =
            navView ?: findViewById(R.id.nav_view_base)

        val headerView = navigationView.getHeaderView(0)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)

        tvUserName.text = UserSession.getUserName(this)
    }

 // lưu ve base update
    override fun onResume() {
        super.onResume()
        updateUserNameInDrawer()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // Home → Dashboard
            R.id.nav_home -> {
                if (this !is DashboardActivity) {
                    startActivity(
                        Intent(this, DashboardActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                    finish()
                }
            }

            R.id.nav_my_vocab -> {
                if (this !is MyVocabActivity) {
                    startActivity(
                        Intent(this, MyVocabActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                    finish()
                }
            }

            // Learning Progress / Word Status
            R.id.nav_status -> {
                if (this !is VocabularyActivity) {
                    val intent = Intent(this, VocabularyActivity::class.java)
                    // Dùng cờ này để đảm bảo back stack hợp lý (giống như MyVocab)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }

            // Trang Flashcard → chuyển sang trang CHUẨN BỊ
            R.id.nav_flashcard -> {
                if (this !is StudySetupActivity) {
                    startActivity(Intent(this, StudySetupActivity::class.java))
                }
            }

            R.id.nav_dictionary -> {
                if (this !is DictionaryActivity) {
                    startActivity(Intent(this, DictionaryActivity::class.java))
                }
            }

            R.id.nav_setting -> {
                if (this !is SettingsActivity) {
                    startActivity(
                        Intent(this, SettingsActivity::class.java).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                    finish()
                }
            }

            R.id.nav_admin_dashboard -> {
                if (this !is AdminDashboardActivity) {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                }
            }

            R.id.nav_logout -> {
                UserSession.clearSession(this)
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }

            R.id.nav_exit -> finishAffinity()
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