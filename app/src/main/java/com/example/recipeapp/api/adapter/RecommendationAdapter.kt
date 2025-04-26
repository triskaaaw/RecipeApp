package com.example.recipeapp.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.api.models.Recipes

class RecommendationAdapter(
    private val recommendations: List<Recipes>,
    private val onItemClick: (Recipes) -> Unit
) :
    RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    inner class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_recipeView)
        val title: TextView = itemView.findViewById(R.id.tv_recipeView)
        private val rating: TextView = itemView.findViewById(R.id.tv_rating)
        private val time: TextView = itemView.findViewById(R.id.tv_time)

        fun bind(recommendation: Recipes){
            title.text = recommendation.title
            Glide.with(itemView.context).load(recommendation.image_url).into(image)
            rating.text = recommendation.ratingValue.toString()
            time.text = recommendation.time

            itemView.setOnClickListener {
                onItemClick(recommendation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_layout, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
//        val item = recommendations[position]
//        holder.title.text = item.title
//        Glide.with(holder.itemView.context).load(item.image_url).into(holder.image)
        holder.bind(recommendations[position])
    }

    override fun getItemCount(): Int = recommendations.size
}
