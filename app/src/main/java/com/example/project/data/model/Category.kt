package com.example.project.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val color: String = ""
) : Parcelable
