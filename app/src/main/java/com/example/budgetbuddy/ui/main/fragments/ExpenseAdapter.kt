package com.example.budgetbuddy.ui.main.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.R
import com.example.budgetbuddy.data.entity.Expense

class ExpenseAdapter(
    val items: List<Expense>,
    private val onPhotoClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSummary: TextView = view.findViewById(R.id.textExpenseSummary)
        val textCategory: TextView = view.findViewById(R.id.textCategory)
        val textAmount: TextView = view.findViewById(R.id.textAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val e = items[position]
        holder.textSummary.text = "${e.date}  ${e.description}"
        holder.textCategory.text = "Category ID: ${e.categoryId}"  // you could resolve name if needed
        holder.textAmount.text = "R ${String.format("%.2f", e.amount)}"
        if (!e.photoPath.isNullOrEmpty()) {
            holder.textAmount.append(" 📷")
        }
        holder.itemView.setOnLongClickListener {
            onPhotoClick(e)
            true
        }
    }

    override fun getItemCount() = items.size
}