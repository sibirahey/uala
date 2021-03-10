package com.sibi.uala.model

data class MealUiModel(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String?
)

data class DetailMealUiModel(
    val name: String,
    val category: String,
    val imageUrl: String?,
    val ingredients: List<String>,
    val videoUrl: String
)