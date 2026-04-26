package com.example.budgetbuddy.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.R
import com.example.budgetbuddy.data.entity.Category
import com.example.budgetbuddy.databinding.FragmentCategoryManagerBinding
import kotlinx.coroutines.launch

class CategoryManagerFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = CategoryManagerFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentCategoryManagerBinding? = null
    private val binding get() = _binding!!
    private val userId by lazy { requireArguments().getInt("USER_ID") }
    private val db get() = (requireActivity().application as BudgetBuddyApplication).database

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategories()

        binding.btnAdd.setOnClickListener {
            val edit = EditText(requireContext())
            AlertDialog.Builder(requireContext())
                .setTitle("New Category Name")
                .setView(edit)
                .setPositiveButton("Add") { _, _ ->
                    val name = edit.text.toString().trim()
                    if (name.isNotEmpty()) {
                        lifecycleScope.launch {
                            val randomColor = "#" + Integer.toHexString((Math.random() * 0xFFFFFF).toInt() and 0xFFFFFF)
                            db.categoryDao().insertCategory(
                                Category(name = name, userId = userId, colorCode = randomColor, iconText = name.first().uppercase())
                            )
                            loadCategories()
                        }
                    }
                }
                .show()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val cats = db.categoryDao().getCategoriesByUser(userId)
            val adapter = object : ArrayAdapter<Category>(requireContext(), R.layout.item_category_spinner, cats) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category_spinner, parent, false)
                    val cat = getItem(position)!!
                    val icon = view.findViewById<TextView>(R.id.iconText)
                    val name = view.findViewById<TextView>(R.id.categoryName)
                    icon.text = cat.iconText
                    val bg = ContextCompat.getDrawable(context, R.drawable.category_icon_bg)?.mutate()
                    bg?.setTint(Color.parseColor(cat.colorCode))
                    icon.background = bg
                    name.text = cat.name
                    return view
                }
            }
            binding.listCategories.adapter = adapter
            binding.listCategories.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
                val cat = cats[pos]
                showEditDeleteDialog(cat)
            }
        }
    }

    private fun showEditDeleteDialog(cat: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle(cat.name)
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                if (which == 0) {
                    val edit = EditText(requireContext())
                    edit.setText(cat.name)
                    AlertDialog.Builder(requireContext())
                        .setTitle("Edit Category")
                        .setView(edit)
                        .setPositiveButton("Save") { _, _ ->
                            val newName = edit.text.toString().trim()
                            if (newName.isNotEmpty()) {
                                lifecycleScope.launch {
                                    db.categoryDao().updateCategory(cat.copy(name = newName))
                                    loadCategories()
                                }
                            }
                        }
                        .show()
                } else {
                    lifecycleScope.launch {
                        db.categoryDao().deleteCategory(cat)
                        loadCategories()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}