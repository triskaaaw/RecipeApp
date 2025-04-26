package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.api.adapter.RecipesAdapter
import com.example.recipeapp.api.models.Recipes
import com.example.recipeapp.api.models.RecipesResponse
import com.example.recipeapp.api.services.ApiClient
import com.example.recipeapp.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var rvRecipe: RecyclerView
    lateinit var tvUsername: TextView
    lateinit var btnLogout: ImageView
    //lateinit var searchBar: LinearLayout
    lateinit var searchBar: SearchView
    lateinit var fabFavorite: FloatingActionButton
    lateinit var rvTop5: RecyclerView

    private lateinit var call:Call<RecipesResponse>
    private lateinit var recipedAdapter: RecipesAdapter
    private lateinit var top5Adapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inisialisasi ApiClient dengan context
        ApiClient.init(applicationContext)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        rvRecipe = findViewById(R.id.recipes_recycler_view)
        rvRecipe.setHasFixedSize(true)
        tvUsername = findViewById(R.id.username)
        btnLogout = findViewById(R.id.btn_logout)
        searchBar = findViewById(R.id.search_bar)
        fabFavorite = findViewById(R.id.fab_favorite)
        rvTop5 = findViewById(R.id.recipes_top5_recycler_view)

        recipedAdapter = RecipesAdapter { recipes ->  recipeOnClick(recipes)}
        rvRecipe.adapter = recipedAdapter
        rvRecipe.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        top5Adapter = RecipesAdapter { recipes -> recipeOnClick(recipes) }
        rvTop5.adapter = top5Adapter
        rvTop5.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        //ambil data username dari SharePreferences
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        //val username = sharedPref.getString("username", null)
        val username = SharedPrefManager.getInstance(this).getUsername()

        if (username != null){
            tvUsername.text = "Hi, $username!"
        }else{
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }

//        searchBar.setOnClickListener {
//            val intent = Intent(this@MainActivity, SearchActivity::class.java)
//            startActivity(intent)
//        }
//        searchBar.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
//
//                val keyword = searchBar.text.toString().trim()
//                if (keyword.isNotEmpty()) {
//                    val intent = Intent(this, SearchActivity::class.java)
//                    intent.putExtra("query", keyword)
//                    startActivity(intent)
//                }
//                true
//            } else {
//                false
//            }
//        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.putExtra("query", query)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        btnLogout.setOnClickListener {
            //cek kalau user benar-benar sudah login
            if (SharedPrefManager.getInstance(this).isLoggedIn){
                SharedPrefManager.getInstance(this).clear()
                ApiClient.cookieJar.clearCookies()
                logoutSession()
            }else{
                Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("MainActivity", "Username: $username")
        val shouldRefresh = intent.getBooleanExtra("TRIGGER_REFRESH", false)
        if (shouldRefresh) {
            Log.d("MainActivity", "TRIGGER_REFRESH dari LoginActivity - panggil getListRecipes()")
            getListRecipes()
        }

        fabFavorite.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryFavoriteActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume dipanggil - Refresh rekomendasi")
        getListRecipes()

        Log.d("MainActivity", "onResume dipanggil - Refresh top 5 recipes")
        getTop5Recipes()
    }

    private fun getTop5Recipes() {
        val apiService = ApiClient.getApiService()
        val sessionId = SharedPrefManager.getInstance(this).getSessionId() ?: ""

        if (sessionId.isEmpty()) {
            Toast.makeText(applicationContext, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            return
        }

        val bearerToken = "Bearer $sessionId"

        apiService.getTopRecipes()
            .enqueue(object : Callback<RecipesResponse> {
                override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<RecipesResponse>, response: Response<RecipesResponse>) {
                    if (response.isSuccessful) {
                        val top5Recipes = response.body()?.recommendations ?: emptyList()
                        Log.d("API_RESPONSE", "Top 5 recipes size: ${top5Recipes.size}")

                        // Update RecyclerView dengan data top 5
                        top5Adapter.submitList(top5Recipes)
                    } else {
                        Log.e("API_RESPONSE", "Failed to get top 5 recipes: ${response.code()}")
                        Toast.makeText(applicationContext, "Failed to get data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }


    private fun recipeOnClick(recipes: Recipes){
        val intent = Intent(this, DetailRecipeActivity::class.java)
        intent.putExtra("idRecipe", recipes.idRecipe)
        startActivity(intent)
        //Toast.makeText(applicationContext, recipes.title, Toast.LENGTH_SHORT).show()
    }

    private fun getListRecipes(){
        val apiService = ApiClient.getApiService()
        //val token = SharedPrefManager.getInstance(this).getToken()
//        Log.d("CEK_TOKEN", "Token dari SharedPref: $token")
//
//        val bearerToken = "Bearer $token"
//
//        Log.d("TOKEN_CHECK", "Bearer $token")

//        val token = SharedPrefManager.getInstance(this).getToken() ?: ""
//        val bearerToken = "Bearer $token"

        val sessionId = SharedPrefManager.getInstance(this).getSessionId() ?:""

//        Log.d("TOKEN_CEK", "Token: '$token'")
//        Log.d("HEADER_CEK", "Authorization: $bearerToken")

        //Jika sessionId kosong, berarti tidak ada sesi yang aktif
        if (sessionId.isEmpty()) {
            Toast.makeText(applicationContext, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            return
        }

        val bearerToken = "Bearer $sessionId"

        Log.d("SESSION_ID", "SessionId: '$sessionId'")
        Log.d("HEADER_CEK", "Authorization: $bearerToken")


        apiService.getRecommendNCF(bearerToken)
            .enqueue(object : Callback<RecipesResponse>{
                override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<RecipesResponse>,
                    response: Response<RecipesResponse>
                ) {
                    if (response.isSuccessful) {
                        val recipes = response.body()?.recommendations ?: emptyList()
                        Log.d("API_RESPONSE", "Jumlah resep diterima: ${recipes.size}")

                        //control visibility recvylerview dan textview based on data
                        if (recipes.isEmpty()) {
                            Log.d("MainActivity", "No recommendations found, showing empty message")
                            rvRecipe.visibility = View.GONE
                            findViewById<TextView>(R.id.empty_message).visibility = View.VISIBLE
                        }else {
                            Log.d("MainActivity", "Recommendations found, displaying RecyclerView")
                            rvRecipe.visibility = View.VISIBLE
                            findViewById<TextView>(R.id.empty_message).visibility = View.GONE
                            recipedAdapter.submitList(recipes)

//                            //paksa refresh adapter untuk update data
//                            recipedAdapter.submitList(null) //clear the list first
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                recipedAdapter.submitList(recipes)
//                            }, 100)
                        }

//                        //paksa adapter refresh agar data benar-benar diperbaharuin
//                        recipedAdapter.submitList(null) //kosongin dulu listnya
//
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            recipedAdapter.submitList(recipes)
//                        }, 100) //delay 100ms agar RecyclerView sempat reset
//
                    }else{
                        //untuk tangani error 404
                        rvRecipe.visibility = View.GONE
                        val errorMessageTextView = findViewById<TextView>(R.id.empty_message)
                        errorMessageTextView.text = "Recommendations are not yet available for new users. We will update the data soon, please use the search feature."
                        errorMessageTextView.visibility = View.VISIBLE
                        Log.e("API_RESPONSE", "Gagal response: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(applicationContext, "Failed to get data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }

    private fun logoutSession(){
        val apiService = ApiClient.getApiService()

        apiService.logout()
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Logout", "Network error: ${t.message}")
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        //Session di server sudah dibersihkan
                        //Sekarang clear local cookie atau pindah ke halaman login
                        val intent = Intent(this@MainActivity, SplashScreenActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }else {
                        Log.e("Logout", "Failed to logout: ${response.code()}")
                    }
                }

            })
    }

    override fun onStart() {
        super.onStart()

        if (!SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}