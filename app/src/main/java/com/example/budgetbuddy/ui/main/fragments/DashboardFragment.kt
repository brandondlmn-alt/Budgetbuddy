package com.example.budgetbuddy.ui.main.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentDashboardBinding
import com.example.budgetbuddy.ui.main.viewmodels.DashboardViewModel

class DashboardFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = DashboardFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private val userId by lazy { requireArguments().getInt("USER_ID") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddExpense.setOnClickListener {
            (activity as? com.example.budgetbuddy.ui.main.MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, AddExpenseFragment.newInstance(userId))
                ?.addToBackStack(null)?.commit()
        }

        viewModel.state.observe(viewLifecycleOwner) { data ->
            binding.textMonthGoal.text = "Goal: R ${String.format("%.2f", data.maxBudget)}"
            binding.textTotalSpent.text = "Spent: R ${String.format("%.2f", data.totalSpent)}"
            val progress = if (data.maxBudget > 0) ((data.totalSpent / data.maxBudget) * 100).toInt() else 0
            binding.circularProgress.progress = progress
            binding.textProgressPercent.text = "$progress%"
            binding.circularProgress.setIndicatorColor(
                if (data.totalSpent <= data.maxBudget) resources.getColor(R.color.progress_green)
                else resources.getColor(R.color.progress_red)
            )

            // Pie chart
            binding.pieChart.slices = data.pieSlices

            val adapter = CategoryProgressAdapter(data.categoryProgresses)
            binding.recyclerCategoryProgress.layoutManager = LinearLayoutManager(context)
            binding.recyclerCategoryProgress.adapter = adapter
        }

        viewModel.load(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}