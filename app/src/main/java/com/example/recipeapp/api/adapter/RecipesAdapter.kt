package com.example.recipeapp.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
//import androidx.appcompat.view.menu.MenuView.ItemView
//import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.api.models.Recipes


class RecipesAdapter(private val onClick: (Recipes) -> Unit) :
ListAdapter<Recipes, RecipesAdapter.RecipesViewHolder>(RecipesCallBack){

    class RecipesViewHolder(itemView: View, val onClick: (Recipes) -> Unit) :
        RecyclerView.ViewHolder(itemView){
            private val image: ImageView = itemView.findViewById(R.id.img_recipeView)
            private val title: TextView = itemView.findViewById(R.id.tv_recipeView)
            private val rating: TextView = itemView.findViewById(R.id.tv_rating)
            private val time: TextView = itemView.findViewById(R.id.tv_time)

            private var currentRecipe: Recipes? = null

            init {
                itemView.setOnClickListener {
                    currentRecipe?.let {
                        onClick(it)
                    }
                }
            }
        fun bind(recipes: Recipes) {
            currentRecipe = recipes

            title.text = recipes.title
            Glide.with(itemView).load(recipes.image_url).centerCrop().into(image)
            rating.text = recipes.ratingValue.toString()
            time.text = recipes.time

        }
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return RecipesViewHolder(view, onClick)
    }
}

object RecipesCallBack : DiffUtil.ItemCallback<Recipes>(){

    override fun areContentsTheSame(oldItem: Recipes, newItem: Recipes): Boolean {
        //tetap bendingkan berdasarkan id
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Recipes, newItem: Recipes): Boolean {
        //bandingkan isi penting (judul, gambar) secara eksplisit
        return oldItem.title == newItem.title &&
                oldItem.image_url == newItem.image_url
    }


}