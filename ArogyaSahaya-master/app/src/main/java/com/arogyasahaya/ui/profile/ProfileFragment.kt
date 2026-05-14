package com.arogyasahaya.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arogyasahaya.databinding.FragmentProfileBinding
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Medical Profile screen — view and edit:
 *   name, age, gender, chronic conditions, emergency contact, blood group
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load and display current profile
        profileViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.etName.setText(it.name)
                binding.etAge.setText(it.age.toString())
                binding.etConditions.setText(it.chronicConditions)
                binding.etEmergencyName.setText(it.emergencyContactName)
                binding.etEmergencyPhone.setText(it.emergencyContactPhone)
                binding.tvBloodGroup.text = "Blood Group: ${it.bloodGroup}"
            }
        }

        // Edit / Save toggle
        binding.btnEditSave.setOnClickListener {
            if (!isEditing) {
                // Switch to edit mode
                isEditing = true
                binding.btnEditSave.text = "💾  Save Profile"
                enableEditing(true)
            } else {
                // Save
                saveProfile()
            }
        }

        // Start in view-only mode
        enableEditing(false)
    }

    private fun enableEditing(enabled: Boolean) {
        binding.etName.isEnabled           = enabled
        binding.etAge.isEnabled            = enabled
        binding.etConditions.isEnabled     = enabled
        binding.etEmergencyName.isEnabled  = enabled
        binding.etEmergencyPhone.isEnabled = enabled
    }

    private fun saveProfile() {
        val name  = binding.etName.text.toString().trim()
        val age   = binding.etAge.text.toString().trim()
        val phone = binding.etEmergencyPhone.text.toString().trim()

        if (name.isEmpty()) { binding.etName.error = "Name required"; return }
        if (age.isEmpty())  { binding.etAge.error  = "Age required";  return }
        if (phone.isEmpty()) { binding.etEmergencyPhone.error = "Emergency contact required"; return }

        profileViewModel.saveProfile(
            name                  = name,
            age                   = age.toIntOrNull() ?: 0,
            gender                = "",
            chronicConditions     = binding.etConditions.text.toString().trim(),
            emergencyContactName  = binding.etEmergencyName.text.toString().trim(),
            emergencyContactPhone = phone,
            bloodGroup            = ""
        )

        isEditing = false
        binding.btnEditSave.text = "✏️  Edit Profile"
        enableEditing(false)
        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
