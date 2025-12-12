package com.example.project.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Level(
    val id: Int = 0,
    val name: String = "",  // A1, A2, B1, B2, C1, C2
    val color: String = ""  // #4CAF50, #2196F3...
) : Parcelable
