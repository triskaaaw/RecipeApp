package com.example.recipeapp.api.services

import android.media.session.MediaSession.Token
import com.example.recipeapp.api.models.CheckLikeResponse
import com.example.recipeapp.api.models.LikeRequest
import com.example.recipeapp.api.models.LikedRecipeResponse
import com.example.recipeapp.api.models.RecipeDetailResponse
import com.example.recipeapp.api.models.RecipesResponse
import com.example.recipeapp.api.models.SearchRequest
import com.example.recipeapp.api.models.UserLoginRequest
import com.example.recipeapp.api.models.UserLoginResponse
import com.example.recipeapp.api.models.UserRegisterRequest
import com.example.recipeapp.api.models.UserRegisterResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

//    @FormUrlEncoded
    @POST("register")
//    @POST("api/user/register")
    fun postUserRegister(
//        @Field("name") name:String,
//        @Field("username") username:String,
//        @Field("password") password:String
        @Body request: UserRegisterRequest
    ): Call<UserRegisterResponse>

    @POST("login")
    fun userLogin(
        @Body request: UserLoginRequest
    ): Call<UserLoginResponse>

    @POST("recommend_ncf")
//    @Headers("Accept: application/json")
    fun getRecommendNCF(
        @Header("Authorization") token: String
        //@Header("Authorization") authHeader: String
    ): Call<RecipesResponse>

    @POST("logout")
    fun logout(): Call<Void>

    @GET("recipe_detail")
    fun getRecipeDetail(
        @Query("idRecipe") idRecipe:Int
    ): Call<RecipeDetailResponse>

    @POST("/recommend_cbf_by_recipe")
    fun getRecommendCBF_byID(
        @Body request: Map<String, Int>
    ): Call<RecipesResponse>

    @POST("/recommend_cbf_context")
    fun getRecommendCBF_context(
        @Body request: SearchRequest
    ): Call<RecipesResponse>

    @GET("/check_like")
    fun chechkLike(
        @Query("idUser") username: String,
        @Query("idRecipe") idRecipe: Int
    ): Call<CheckLikeResponse>

    @POST("/like_recipe")
    fun likeRecipe(
        @Body body: LikeRequest
    ): Call<ResponseBody>

    @POST("/unlike_recipe")
    fun unlikeRecipe(
        @Body body: LikeRequest
    ): Call<ResponseBody>

    @GET("/liked_recipes")
    fun getLikedRecipes(
        @Query("idUser") idUser: String
    ): Call<LikedRecipeResponse>

    @GET("/top_recipes")
    fun getTopRecipes(): Call<RecipesResponse>
}