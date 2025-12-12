package com.example.project.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val role: String = "user",  // "user" hoặc "admin"
    val created_at: String = ""
) : Parcelable {
    fun isAdmin(): Boolean = role == "admin"
    fun isUser(): Boolean = role == "user"
}
