package com.example.recipeapp.api.services

import android.content.Context
import android.util.Log
import com.example.recipeapp.utils.SharedPrefManager
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:5000/"

    lateinit var cookieJar: PersistentCookieJar

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(context)

        okHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiService(): Api {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ApiClient not initialized. Call ApiClient.init(context) first.")
        }
        return retrofit.create(Api::class.java)
    }

//    fun clearCookies() {
//        cookieJar.clearCookies()
//    }

    fun clearCookies() {
        if (::cookieJar.isInitialized) {
            cookieJar.clearCookies()
        }
    }
}
