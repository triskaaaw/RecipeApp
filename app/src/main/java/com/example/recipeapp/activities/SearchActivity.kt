package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.api.adapter.RecipesAdapter
import com.example.recipeapp.api.models.Recipes
import com.example.recipeapp.api.models.RecipesResponse
import com.example.recipeapp.api.models.SearchRequest
import com.example.recipeapp.api.services.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.rv_search_recipe)

        // Ambil query dari MainActivity
        val keyword = intent.getStringExtra("query") ?: ""
        // Tampilkan ke SearchView dan langsung jalankan pencarian
        searchView.setQuery(keyword, false)
        // Jalankan fetch search
        searchRecipes(keyword)

        recipesAdapter = RecipesAdapter { recipes -> recipeOnClick(recipes) }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipesAdapter

//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                searchRecipes(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchRecipes(query: String) {
        val ingredients = query.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val request = SearchRequest(ingredients)

        val call = ApiClient.getApiService().getRecommendCBF_context(request)
        call.enqueue(object : Callback<RecipesResponse> {
            override fun onResponse(
                call: Call<RecipesResponse>,
                response: Response<RecipesResponse>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.recommendations ?: emptyList()
                    recipesAdapter.submitList(recipes)
                } else {
                    Log.e("SearchActivity", "Search failed: ${response.code()}")
                    Toast.makeText(this@SearchActivity, "Search failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                Log.e("SearchActivity", "Error: ${t.message}", t)
                Toast.makeText(this@SearchActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun recipeOnClick(recipes: Recipes){
        val intent = Intent(this, DetailRecipeActivity::class.java)
        intent.putExtra("idRecipe", recipes.idRecipe)
        startActivity(intent)
    }
}
