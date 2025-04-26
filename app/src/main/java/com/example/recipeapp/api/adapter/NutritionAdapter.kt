package com.example.recipeapp.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R

class NutritionAdapter(private val nutritionList: List<Pair<String, String>>) :
    RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder>() {

    inner class NutritionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.findViewById(R.id.nutritionLabel)
        val value: TextView = itemView.findViewById(R.id.nutritionValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nutrition_layout, parent, false)
        return NutritionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val (label, value) = nutritionList[position]
        holder.label.text = label
        holder.value.text = value
    }

    override fun getItemCount() = nutritionList.size
}
