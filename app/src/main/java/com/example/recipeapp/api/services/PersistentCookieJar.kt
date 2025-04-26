package com.example.recipeapp.api.services

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar(private val context: Context) : CookieJar {
    //private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
    private val cookieStore: ConcurrentHashMap<String, MutableList<Cookie>> = ConcurrentHashMap()
    private val prefs: SharedPreferences = context.getSharedPreferences("cookies_prefs", Context.MODE_PRIVATE)

    init {
        loadAllCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies.toMutableList()
        saveCookies(url.host, cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host] ?: emptyList()
    }

    fun clearCookies() {
        cookieStore.clear()
        prefs.edit().clear().apply()
    }

    private fun saveCookies(host: String, cookies: List<Cookie>) {
        val serializedCookies = cookies.joinToString(";") { it.toString() }
        prefs.edit().putString(host, serializedCookies).apply()
    }

    private fun loadAllCookies() {
        prefs.all.forEach { entry ->
            val host = entry.key
            val serialized = entry.value as String
            //val cookies = serialized.split(";").mapNotNull { Cookie.parse(HttpUrl.get("http://$host/"), it) }
            val httpUrl = HttpUrl.Builder()
                .scheme("http")
                .host(host)
                .addPathSegment("") //ini penting biar url valid
                .build()
            val cookies = serialized.split(";").mapNotNull { Cookie.parse(httpUrl, it) }
            cookieStore[host] = cookies.toMutableList()
        }
    }

}
