package com.example.budgetbuddy.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.data.converter.ExchangeRates
import com.example.budgetbuddy.databinding.FragmentCurrencyConverterBinding

class CurrencyConverterFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int) = CurrencyConverterFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currencies = arrayOf("ZAR", "USD", "EUR", "GBP", "JPY")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrom.adapter = adapter
        binding.spinnerTo.adapter = adapter

        binding.btnConvert.setOnClickListener {
            val amount = binding.editAmount.text.toString().toDoubleOrNull() ?: 0.0
            val from = binding.spinnerFrom.selectedItem as String
            val to = binding.spinnerTo.selectedItem as String
            val result = ExchangeRates.convert(amount, from, to)
            binding.textResult.text = "Result: ${String.format("%.2f", result)} $to"
        }
        binding.textLastUpdated.text = "Rates as of: ${ExchangeRates.LAST_UPDATED}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}