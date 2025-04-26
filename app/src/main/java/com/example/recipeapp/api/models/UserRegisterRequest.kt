package com.example.recipeapp.api.models

data class UserRegisterRequest (
    val name: String,
    val username: String,
    val password: String
)