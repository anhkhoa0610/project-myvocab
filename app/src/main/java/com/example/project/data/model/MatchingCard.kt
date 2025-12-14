package com.example.project.data.model
enum class CardType {
    EN,
    VI
}

data class MatchingCard(
    val id: Int,
    val content: String,
    val type: CardType,
    var isSelected: Boolean = false,
    var isVisible: Boolean = true
)
