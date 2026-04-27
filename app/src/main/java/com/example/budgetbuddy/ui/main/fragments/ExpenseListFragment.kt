package com.example.budgetbuddy.ui.main.fragments

import android.app.DatePickerDialog
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbuddy.databinding.FragmentExpenseListBinding
import com.example.budgetbuddy.ui.main.viewmodels.ExpenseListViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
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
            exportToPdf()
        }

        binding.btnFilter.performClick()
    }

    private fun exportToPdf() {
        val adapter = binding.recyclerExpenses.adapter as? ExpenseAdapter ?: return
        val expenses = adapter.items
        if (expenses.isEmpty()) {
            Toast.makeText(requireContext(), "No expenses to export", Toast.LENGTH_SHORT).show()
            return
        }

        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        var canvas = currentPage.canvas
        var y = 40f

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            isFakeBoldText = true
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f
        }
        val lineHeight = 16f
        val marginLeft = 10f
        val usableWidth = (pageWidth - marginLeft * 2).toFloat()

        // Header
        canvas.drawText("Date       Category       Description       Amount", marginLeft, y, titlePaint)
        y += 24f

        for (e in expenses) {
            if (y > pageHeight - 50f) {
                pdf.finishPage(currentPage)
                currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
                canvas = currentPage.canvas
                y = 40f
                canvas.drawText("Date       Category       Description       Amount", marginLeft, y, titlePaint)
                y += 24f
            }

            val line = String.format(Locale.ROOT, "%s  %s  %s  R%.2f", e.date, e.description, e.description, e.amount)
            canvas.drawText(line, marginLeft, y, textPaint)
            y += lineHeight

            // Receipt image
            if (!e.photoPath.isNullOrEmpty()) {
                val photoFile = File(e.photoPath)
                if (photoFile.exists()) {
                    try {
                        val originalBitmap = BitmapFactory.decodeFile(e.photoPath)
                        if (originalBitmap != null) {
                            var scaledWidth = originalBitmap.width.toFloat()
                            var scaledHeight = originalBitmap.height.toFloat()
                            val maxImageWidth = usableWidth
                            val maxImageHeight = 150f

                            if (scaledWidth > maxImageWidth) {
                                val ratio = maxImageWidth / scaledWidth
                                scaledWidth = maxImageWidth
                                scaledHeight *= ratio
                            }
                            if (scaledHeight > maxImageHeight) {
                                val ratio = maxImageHeight / scaledHeight
                                scaledHeight = maxImageHeight
                                scaledWidth *= ratio
                            }

                            if (y + scaledHeight > pageHeight - 40f) {
                                pdf.finishPage(currentPage)
                                currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
                                canvas = currentPage.canvas
                                y = 40f
                            }

                            canvas.drawBitmap(originalBitmap, null,
                                Rect(marginLeft.toInt(), y.toInt(), (marginLeft + scaledWidth).toInt(), (y + scaledHeight).toInt()), null)
                            y += scaledHeight + 8f
                            originalBitmap.recycle()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
            y += 4f
        }

        pdf.finishPage(currentPage)

        // Save the PDF
        val fileName = "expenses_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29+ – use MediaStore.Downloads (no permission needed)
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = requireContext().contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
                )
                uri?.let {
                    val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(it)
                    outputStream?.use { pdf.writeTo(it) }
                    Toast.makeText(requireContext(), "PDF saved to Downloads", Toast.LENGTH_LONG).show()
                } ?: Toast.makeText(requireContext(), "Failed to create PDF", Toast.LENGTH_SHORT).show()
            } else {
                // Older devices – fallback to app-specific directory
                val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: requireContext().filesDir
                val file = File(dir, fileName)
                FileOutputStream(file).use { pdf.writeTo(it) }
                Toast.makeText(requireContext(), "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdf.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}