package com.example.budgetbuddy.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.R
import com.example.budgetbuddy.databinding.FragmentMoreBinding
import com.example.budgetbuddy.ui.auth.LoginActivity

class MoreFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = MoreFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private val userId by lazy { requireArguments().getInt("USER_ID") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoals.setOnClickListener {
            (activity as? com.example.budgetbuddy.ui.main.MainActivity)
                ?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, GoalsFragment.newInstance(userId))
                ?.addToBackStack(null)?.commit()
        }

        binding.btnCategories.setOnClickListener {
            (activity as? com.example.budgetbuddy.ui.main.MainActivity)
                ?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, CategoryManagerFragment.newInstance(userId))
                ?.addToBackStack(null)?.commit()
        }

        binding.btnCurrencyConverter.setOnClickListener {
            (activity as? com.example.budgetbuddy.ui.main.MainActivity)
                ?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, CurrencyConverterFragment.newInstance(userId))
                ?.addToBackStack(null)?.commit()
        }

        binding.btnAchievements.setOnClickListener {
            (activity as? com.example.budgetbuddy.ui.main.MainActivity)
                ?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, GamificationFragment.newInstance(userId))
                ?.addToBackStack(null)?.commit()
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            // Clear saved "remember me" username
            activity?.getSharedPreferences("budget_prefs", android.content.Context.MODE_PRIVATE)
                ?.edit()
                ?.clear()
                ?.apply()

            // Go to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}