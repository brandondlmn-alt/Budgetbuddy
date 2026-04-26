package com.example.budgetbuddy.ui.main.fragments

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.databinding.FragmentExpenseListBinding
import com.example.budgetbuddy.ui.main.viewmodels.ExpenseListViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseListFragment : Fragment() {

    companion object {
        fun newInstance(userId: Int) = ExpenseListFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpenseListViewModel by viewModels()
    private val userId by lazy { requireArguments().getInt("USER_ID") }
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                exportToPdf()
            } else {
                Toast.makeText(requireContext(), "Storage permission required to export PDF", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val today = LocalDate.now()
        binding.editStartDate.setText(today.withDayOfMonth(1).format(formatter))
        binding.editEndDate.setText(today.format(formatter))

        binding.editStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val date = LocalDate.of(y, m + 1, d)
                binding.editStartDate.setText(date.format(formatter))
            }, today.year, today.monthValue - 1, today.dayOfMonth).show()
        }

        binding.editEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val date = LocalDate.of(y, m + 1, d)
                binding.editEndDate.setText(date.format(formatter))
            }, today.year, today.monthValue - 1, today.dayOfMonth).show()
        }

        binding.btnFilter.setOnClickListener {
            val start = binding.editStartDate.text.toString()
            val end = binding.editEndDate.text.toString()
            viewModel.loadExpenses(userId, start, end)
        }

        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            val adapter = ExpenseAdapter(expenses) { expense ->
                if (!expense.photoPath.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Photo: ${expense.photoPath}", Toast.LENGTH_SHORT).show()
                }
            }
            binding.recyclerExpenses.layoutManager = LinearLayoutManager(context)
            binding.recyclerExpenses.adapter = adapter
        }

        binding.btnExportPDF.setOnClickListener {
            // Check storage permission before exporting
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                exportToPdf()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.btnFilter.performClick()
    }

    private fun exportToPdf() {
        val adapter = binding.recyclerExpenses.adapter as? ExpenseAdapter ?: return
        val expenses = adapter.items

        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 11f
        var y = 40f

        canvas.drawText("Date          Description          Amount", 10f, y, paint)
        y += 20f
        for (e in expenses) {
            val line = String.format(Locale.ROOT, "%s  %s  R%.2f", e.date, e.description, e.amount)
            canvas.drawText(line, 10f, y, paint)
            y += 15f
            if (y > 800f) {
                pdf.finishPage(page)
                break
            }
        }
        pdf.finishPage(page)

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "expenses_${System.currentTimeMillis()}.pdf"
        )
        FileOutputStream(file).use { output ->
            pdf.writeTo(output)
        }
        pdf.close()
        Toast.makeText(requireContext(), "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}