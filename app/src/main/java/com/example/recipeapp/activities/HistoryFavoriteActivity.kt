package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.api.adapter.RecipesAdapter
import com.example.recipeapp.api.models.LikedRecipeResponse
import com.example.recipeapp.api.models.Recipes
import com.example.recipeapp.api.services.ApiClient
import com.example.recipeapp.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFavoriteActivity : AppCompatActivity() {

    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var recyclerView: RecyclerView
    private val favoriteList = mutableListOf<Recipes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_favorite)

        recyclerView = findViewById(R.id.recycler_fav)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recipesAdapter = RecipesAdapter { selectedRecipe ->
            val intent = Intent(this, DetailRecipeActivity::class.java)
            intent.putExtra("idRecipe", selectedRecipe.idRecipe)
            startActivity(intent)
        }

        recyclerView.adapter = recipesAdapter

        fetchFevoritesAPI()
    }

    private fun fetchFevoritesAPI() {
        val idUser = SharedPrefManager.getInstance(this).getUsername()
        Log.d("LikedRecipes", "ID user yang dikirim: $idUser")
        if (idUser.isNullOrEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.getApiService()
        val call = apiService.getLikedRecipes(idUser)

        call.enqueue(object : Callback<LikedRecipeResponse> {
            override fun onResponse(
                call: Call<LikedRecipeResponse>,
                response: Response<LikedRecipeResponse>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.liked_recipes ?: emptyList()
                    Log.d("LikedRecipes", "Jumlah resep yang didapat: ${recipes.size}")
                    recipesAdapter.submitList(response.body()?.liked_recipes)
                } else {
                    Toast.makeText(this@HistoryFavoriteActivity, "Failed to load recipe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LikedRecipeResponse>, t: Throwable) {
                Toast.makeText(this@HistoryFavoriteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}