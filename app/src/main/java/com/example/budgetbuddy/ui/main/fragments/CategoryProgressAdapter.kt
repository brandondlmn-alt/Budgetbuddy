package com.example.budgetbuddy.ui.main.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.ui.main.viewmodels.DashboardViewModel

class CategoryProgressAdapter(
    private val items: List<DashboardViewModel.CategoryProgress>
) : RecyclerView.Adapter<CategoryProgressAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textCategoryName)
        val progress: ProgressBar = view.findViewById(R.id.progressCategory)
        val spent: TextView = view.findViewById(R.id.textCategorySpent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category_progress, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.categoryName
        holder.spent.text = "R ${String.format("%.2f", item.spent)} (${item.percentage}%)"
        holder.progress.max = 100
        holder.progress.progress = item.percentage.coerceIn(0, 100)
        // Tint the progress bar with the category's own colour
        holder.progress.progressDrawable.setTint(item.color)
    }

    override fun getItemCount() = items.size
}