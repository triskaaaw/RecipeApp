package com.example.recipeapp.api.models

data class UserLoginResponse (
    val sessionId: String,
    val user_id: Int,
    val username: String
)