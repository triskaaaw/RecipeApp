package com.example.recipeapp.api.models

import com.google.gson.annotations.SerializedName

data class RecipesResponse (
    @SerializedName("recommendations")
    val recommendations: List<Recipes>
)