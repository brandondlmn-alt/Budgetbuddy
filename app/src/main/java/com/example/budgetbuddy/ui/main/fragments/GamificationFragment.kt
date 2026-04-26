package com.example.budgetbuddy.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy.BudgetBuddyApplication
import com.example.budgetbuddy.databinding.FragmentGamificationBinding
import kotlinx.coroutines.launch

class GamificationFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = GamificationFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentGamificationBinding? = null
    private val binding get() = _binding!!
    private val userId by lazy { requireArguments().getInt("USER_ID") }
    private val db get() = (requireActivity().application as BudgetBuddyApplication).database

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGamificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val totalExp = db.expenseDao().getTotalExpenseCount(userId)
            val receiptCount = db.expenseDao().getReceiptCount(userId)
            binding.txtBadge1.text = "Budget Beginner: $totalExp/10 expenses logged"
            binding.progress1.progress = (totalExp * 10).coerceAtMost(100)
            binding.txtBadge2.text = "Receipt Rockstar: $receiptCount/5 receipts attached"
            binding.progress2.progress = (receiptCount * 20).coerceAtMost(100)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}