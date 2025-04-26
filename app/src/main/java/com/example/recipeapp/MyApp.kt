package com.example.recipeapp

import android.app.Application
import com.example.recipeapp.api.services.ApiClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(applicationContext)
    }
}