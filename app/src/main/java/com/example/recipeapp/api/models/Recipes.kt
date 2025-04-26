package com.example.recipeapp.api.models

data class Recipes (
    val idRecipe: Int,
    val image_url: String,
    val title: String,
    val ratingValue: Double,
    val time: String
)