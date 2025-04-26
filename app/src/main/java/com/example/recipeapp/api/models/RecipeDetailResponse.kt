package com.example.recipeapp.api.models

data class RecipeDetailResponse (
    val authorName: String,
    val category: List<String>,
    val description: String,
    val idRecipe: Int,
    val image_url: String,
    val ingredients: List<String>,
    val methods: List<String>,
    val nutrition: Nutrition,
    val ratingValue: Double,
    val time: String,
    val title: String,
)