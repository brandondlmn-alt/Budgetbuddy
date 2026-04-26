package com.example.budgetbuddy.ui.main.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetbuddy.R
import com.example.budgetbuddy.data.converter.ExchangeRates
import com.example.budgetbuddy.data.entity.Category
import com.example.budgetbuddy.databinding.FragmentAddExpenseBinding
import com.example.budgetbuddy.ui.main.viewmodels.AddExpenseViewModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime

class AddExpenseFragment : Fragment() {

    companion object {
        fun newInstance(userId: Int) = AddExpenseFragment().apply {
            arguments = Bundle().apply { putInt("USER_ID", userId) }
        }
    }

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddExpenseViewModel by viewModels()
    private val userId by lazy { requireArguments().getInt("USER_ID") }
    private var photoUri: Uri? = null

    // Permission launchers
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) launchCamera()
            else Toast.makeText(requireContext(), "Camera permission needed for photo", Toast.LENGTH_SHORT).show()
        }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            binding.imagePreview.setImageURI(photoUri)
            binding.imagePreview.visibility = View.VISIBLE
            viewModel.photoPath = getRealPathFromURI(photoUri!!)
        }
    }

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.imagePreview.setImageURI(it)
            binding.imagePreview.visibility = View.VISIBLE
            viewModel.photoPath = getRealPathFromURI(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadCategories(userId)
        binding.btnSave.isEnabled = false

        viewModel.categories.observe(viewLifecycleOwner) { cats ->
            if (cats.isEmpty()) {
                Toast.makeText(requireContext(), "Add categories first!", Toast.LENGTH_SHORT).show()
                return@observe
            }
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
                override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return getView(position, convertView, parent)
                }
            }
            binding.spinnerCategory.adapter = adapter
            binding.btnSave.isEnabled = true
        }

        // Date picker
        binding.editDate.setOnClickListener {
            val now = LocalDate.now()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                binding.editDate.setText("$y-${String.format("%02d", m+1)}-${String.format("%02d", d)}")
            }, now.year, now.monthValue - 1, now.dayOfMonth).show()
        }

        // Time pickers
        binding.editStartTime.setOnClickListener {
            val now = LocalTime.now()
            TimePickerDialog(requireContext(), { _, h, min ->
                binding.editStartTime.setText("${String.format("%02d", h)}:${String.format("%02d", min)}")
            }, now.hour, now.minute, true).show()
        }
        binding.editEndTime.setOnClickListener {
            val now = LocalTime.now()
            TimePickerDialog(requireContext(), { _, h, min ->
                binding.editEndTime.setText("${String.format("%02d", h)}:${String.format("%02d", min)}")
            }, now.hour, now.minute, true).show()
        }

        // Attach photo
        binding.btnAttachPhoto.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            AlertDialog.Builder(requireContext())
                .setItems(options) { _, which ->
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                            launchCamera()
                        } else {
                            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    } else {
                        pickPhoto.launch("image/*")
                    }
                }
                .show()
        }

        // Foreign currency
        binding.switchForeign.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutForeign.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.btnConvert.setOnClickListener {
            val from = binding.spinnerCurrency.selectedItem.toString()
            val amount = binding.editForeignAmount.text.toString().toDoubleOrNull() ?: 0.0
            val home = ExchangeRates.convert(amount, from, "ZAR")
            binding.textConvertedAmount.text = "Home: R ${String.format("%.2f", home)}"
            binding.editAmount.setText(String.format("%.2f", home))
        }

        // Save expense
        binding.btnSave.setOnClickListener {
            val amount = binding.editAmount.text.toString().toDoubleOrNull()
            val date = binding.editDate.text.toString()
            val start = binding.editStartTime.text.toString()
            val end = binding.editEndTime.text.toString()
            val desc = binding.editDescription.text.toString()
            val categoryPos = binding.spinnerCategory.selectedItemPosition
            val categories = viewModel.categories.value

            if (categories == null || categories.isEmpty()) {
                Toast.makeText(requireContext(), "No categories available. Add one first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (categoryPos == AdapterView.INVALID_POSITION || categoryPos >= categories.size || amount == null || date.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val categoryId = categories[categoryPos].id
            val foreignCurrency = if (binding.switchForeign.isChecked) binding.spinnerCurrency.selectedItem.toString() else null
            viewModel.saveExpense(userId, categoryId, amount, date, start, end, desc, foreignCurrency, amount)
            Toast.makeText(requireContext(), "Expense saved", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun launchCamera() {
        val photoFile = File(requireActivity().externalCacheDir, "receipt_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        takePicture.launch(photoUri!!)
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        return if (uri.scheme == "content") {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        } else if (uri.scheme == "file") {
            uri.path
        } else {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}