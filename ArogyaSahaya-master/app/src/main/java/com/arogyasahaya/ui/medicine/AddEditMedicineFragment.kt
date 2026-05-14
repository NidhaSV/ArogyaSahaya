package com.arogyasahaya.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arogyasahaya.data.entity.MedicineEntity
import com.arogyasahaya.databinding.FragmentAddEditMedicineBinding
import com.arogyasahaya.viewmodel.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Add a new medicine or edit an existing one.
 * Pass medicineId in Bundle to enter edit mode.
 */
@AndroidEntryPoint
class AddEditMedicineFragment : Fragment() {

    private var _binding: FragmentAddEditMedicineBinding? = null
    private val binding get() = _binding!!

    private val medicineViewModel: MedicineViewModel by viewModels()

    // null = add mode, non-null = edit mode
    private var existingMedicine: MedicineEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If editing, pre-fill the form
        val medicineId = arguments?.getInt("medicineId", -1) ?: -1
        if (medicineId != -1) {
            binding.tvTitle.text = "Edit Medicine"
            medicineViewModel.getMedicineById(medicineId) { medicine ->
                medicine?.let {
                    existingMedicine = it
                    binding.etMedicineName.setText(it.name)
                    binding.etDosage.setText(it.dosage)
                    binding.cbMorning.isChecked   = it.isMorning
                    binding.cbAfternoon.isChecked = it.isAfternoon
                    binding.cbNight.isChecked     = it.isNight
                    binding.etMorningTime.setText(it.morningTime)
                    binding.etAfternoonTime.setText(it.afternoonTime)
                    binding.etNightTime.setText(it.nightTime)
                    binding.etNotes.setText(it.notes)
                }
            }
        }

        // Show/hide time fields based on checkbox state
        binding.cbMorning.setOnCheckedChangeListener { _, checked ->
            binding.layoutMorningTime.visibility = if (checked) View.VISIBLE else View.GONE
        }
        binding.cbAfternoon.setOnCheckedChangeListener { _, checked ->
            binding.layoutAfternoonTime.visibility = if (checked) View.VISIBLE else View.GONE
        }
        binding.cbNight.setOnCheckedChangeListener { _, checked ->
            binding.layoutNightTime.visibility = if (checked) View.VISIBLE else View.GONE
        }

        binding.btnSaveMedicine.setOnClickListener { saveMedicine() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun saveMedicine() {
        val name = binding.etMedicineName.text.toString().trim()
        val dosage = binding.etDosage.text.toString().trim()

        if (name.isEmpty()) {
            binding.etMedicineName.error = "Please enter medicine name"
            return
        }
        if (!binding.cbMorning.isChecked && !binding.cbAfternoon.isChecked && !binding.cbNight.isChecked) {
            Toast.makeText(requireContext(), "Please select at least one time slot", Toast.LENGTH_SHORT).show()
            return
        }

        val medicine = MedicineEntity(
            id           = existingMedicine?.id ?: 0,
            name         = name,
            dosage       = dosage.ifEmpty { "as prescribed" },
            isMorning    = binding.cbMorning.isChecked,
            isAfternoon  = binding.cbAfternoon.isChecked,
            isNight      = binding.cbNight.isChecked,
            morningTime  = binding.etMorningTime.text.toString().ifEmpty { "08:00" },
            afternoonTime = binding.etAfternoonTime.text.toString().ifEmpty { "13:00" },
            nightTime    = binding.etNightTime.text.toString().ifEmpty { "21:00" },
            notes        = binding.etNotes.text.toString().trim()
        )

        if (existingMedicine == null) {
            medicineViewModel.addMedicine(medicine)
            Toast.makeText(requireContext(), "Medicine added! Reminder set.", Toast.LENGTH_SHORT).show()
        } else {
            medicineViewModel.updateMedicine(medicine)
            Toast.makeText(requireContext(), "Medicine updated.", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
