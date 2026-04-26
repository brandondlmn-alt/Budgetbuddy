package com.example.budgetbuddy.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetbuddy.databinding.FragmentGoalsBinding
import com.example.budgetbuddy.ui.main.viewmodels.GoalsViewModel

class GoalsFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = GoalsFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()
    private val userId by lazy { requireArguments().getInt("USER_ID") }
    private var existingGoal: com.example.budgetbuddy.data.entity.Goal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadGoal(userId) { goal ->
            existingGoal = goal
            binding.editMin.setText(goal?.minAmount?.toString() ?: "")
            binding.editMax.setText(goal?.maxAmount?.toString() ?: "")
        }

        binding.btnSave.setOnClickListener {
            val min = binding.editMin.text.toString().toDoubleOrNull()
            val max = binding.editMax.text.toString().toDoubleOrNull()
            if (min == null || max == null || min < 0 || max <= min) {
                Toast.makeText(requireContext(), "Min must be >= 0 and Max > Min", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveGoal(userId, min, max, existingGoal)
            Toast.makeText(requireContext(), "Goal saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}