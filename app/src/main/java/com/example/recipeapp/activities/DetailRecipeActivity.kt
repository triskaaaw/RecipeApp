package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.api.adapter.CategoryAdapter
import com.example.recipeapp.api.adapter.NutritionAdapter
import com.example.recipeapp.api.adapter.RecipesAdapter
import com.example.recipeapp.api.adapter.RecommendationAdapter
import com.example.recipeapp.api.models.CheckLikeResponse
import com.example.recipeapp.api.models.LikeRequest
import com.example.recipeapp.api.models.RecipeDetailResponse
import com.example.recipeapp.api.models.RecipesResponse
import com.example.recipeapp.api.services.ApiClient
import com.example.recipeapp.utils.SharedPrefManager
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRecipeActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeAuthor: TextView
    private lateinit var recipeRating: TextView
    private lateinit var recipeCookingTime: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDirections: TextView
    private lateinit var categoryContainer: RecyclerView
    private lateinit var recipeNutrition: RecyclerView
    private lateinit var recommendationRecyclerView: RecyclerView
    private lateinit var likeButton: ImageButton
    private var isLiked = false
    private var idRecipe: Int = -1
    private lateinit var username: String

    private val apiService by lazy { ApiClient.getApiService() }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var nutritionAdapter: NutritionAdapter

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_recipe)

        recipeImage = findViewById(R.id.recipeImage)
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeAuthor = findViewById(R.id.recipeAuthor)
        recipeRating = findViewById(R.id.recipeRating)
        recipeCookingTime = findViewById(R.id.recipeCookingTime)
        recipeDescription = findViewById(R.id.recipeDescription)
        recipeIngredients = findViewById(R.id.ingredients)
        recipeDirections = findViewById(R.id.directions)
        categoryContainer = findViewById(R.id.categoryRecyclerView)
        recipeNutrition = findViewById(R.id.nutritionRecyclerView)
        recommendationRecyclerView = findViewById(R.id.recommendationRecyclerView)

        idRecipe = intent.getIntExtra("idRecipe", -1)
        username = SharedPrefManager.getInstance(this).getUsername().toString()

        likeButton = findViewById<ImageButton>(R.id.likeButton)

        checkIfLiked()

        likeButton.setOnClickListener {
            if (isLiked) {
                unlikeRecipe()
            }else{
                likeRecipe()
            }
        }


        //dapat idRecipe dari Intent
        val idRecipe = intent.getIntExtra("idRecipe", -1)
        if (idRecipe != -1) {
            fetchRecipeDetail(idRecipe)
            fetchRecommendation(idRecipe)
        }else{
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchRecipeDetail(idRecipe: Int) {
        val apiService = ApiClient.getApiService()

        apiService.getRecipeDetail(idRecipe)
            .enqueue(object : Callback<RecipeDetailResponse> {
                override fun onFailure(call: Call<RecipeDetailResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<RecipeDetailResponse>,
                    response: Response<RecipeDetailResponse>
                ) {
                    val recipe = response.body()


                    recipe?.let {

                        Glide.with(this@DetailRecipeActivity)
                            .load(it.image_url)
                            .into(recipeImage)

                        recipeTitle.text = it.title
                        recipeAuthor.text = getString(R.string.by_author, it.authorName)
                        recipeRating.text = it.ratingValue.toString()
                        recipeCookingTime.text = it.time
                        recipeDescription.text = it.description
                        recipeIngredients.text = it.ingredients.joinToString("\n")
                        recipeDirections.text = it.methods.joinToString("\n")

                        categoryAdapter = CategoryAdapter(it.category)
                        categoryContainer.layoutManager = LinearLayoutManager(this@DetailRecipeActivity, LinearLayoutManager.HORIZONTAL, false)
                        categoryContainer.adapter = categoryAdapter

                        val nutritionList = listOf(
                            "Carbs" to it.nutrition.carbs,
                            "Fat" to it.nutrition.fat,
                            "Fibre" to it.nutrition.fibre,
                            "kcal" to it.nutrition.kcal,
                            "Protein" to it.nutrition.protein,
                            "Salt" to it.nutrition.salt,
                            "Saturates" to it.nutrition.saturates,
                            "Sugars" to it.nutrition.sugars
                        )
                        nutritionAdapter = NutritionAdapter(nutritionList)
                        recipeNutrition.layoutManager = LinearLayoutManager(this@DetailRecipeActivity, LinearLayoutManager.HORIZONTAL, false)
                        recipeNutrition.adapter = nutritionAdapter
                    }
                }

            })
    }

    private fun fetchRecommendation(idRecipe: Int) {
        val apiService = ApiClient.getApiService()
        val body = mapOf("idRecipe" to idRecipe)

        apiService.getRecommendCBF_byID(body)
            .enqueue(object : Callback<RecipesResponse>{
                override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<RecipesResponse>,
                    response: Response<RecipesResponse>
                ) {
                    if (response.isSuccessful) {
                        val recommendations = response.body()?.recommendations ?: emptyList()
                        val adapter = RecommendationAdapter(recommendations) {selectedRecipe ->
                            val intent = Intent(this@DetailRecipeActivity, DetailRecipeActivity::class.java)
                            intent.putExtra("idRecipe", selectedRecipe.idRecipe)
                            startActivity(intent)
                        }
                        recommendationRecyclerView.layoutManager = LinearLayoutManager(this@DetailRecipeActivity, LinearLayoutManager.VERTICAL, false)
                        recommendationRecyclerView.adapter = adapter
                    }else{
                        Toast.makeText(applicationContext, "Failed to get recommendation", Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }

    private fun checkIfLiked() {
        apiService.chechkLike(username, idRecipe).enqueue(object : retrofit2.Callback<CheckLikeResponse> {
            override fun onResponse(
                call: Call<CheckLikeResponse>,
                response: retrofit2.Response<CheckLikeResponse>
            ) {
                if (response.isSuccessful) {
                    isLiked = response.body()?.liked ?: false
                    updateLikeIcon()
                }
            }

            override fun onFailure(call: Call<CheckLikeResponse>, t: Throwable) {
                Toast.makeText(this@DetailRecipeActivity, "Failed to check like", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun likeRecipe() {
        val body = LikeRequest(username, idRecipe)
        apiService.likeRecipe(body).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    isLiked = true
                    updateLikeIcon()
                    Toast.makeText(this@DetailRecipeActivity, "Recipe liked", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@DetailRecipeActivity, "Failed to like", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unlikeRecipe() {
        val body = LikeRequest(username, idRecipe)
        apiService.unlikeRecipe(body).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    isLiked = false
                    updateLikeIcon()
                    Toast.makeText(this@DetailRecipeActivity, "Like canceled", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@DetailRecipeActivity, "Failed to unlike", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateLikeIcon() {
        likeButton.setImageResource(
            if (isLiked) R.drawable.ic_favorite_red
            else R.drawable.ic_favorite_shadow
        )
    }
}