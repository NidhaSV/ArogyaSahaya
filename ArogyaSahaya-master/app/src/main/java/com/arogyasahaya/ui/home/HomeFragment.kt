package com.arogyasahaya.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arogyasahaya.databinding.FragmentHomeBinding
import com.arogyasahaya.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Home screen — shows:
 *   • Greeting with user's name
 *   • Today's medicine summary (how many taken / total)
 *   • Large RED SOS emergency button
 *   • Quick links to other screens
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show user's name in greeting
        homeViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.tvGreeting.text = "Namaste, ${profile.name}!"
                binding.tvEmergencyContact.text = "Emergency: ${profile.emergencyContactName} · ${profile.emergencyContactPhone}"
            }
        }

        // Show how many medicines are active today
        homeViewModel.medicines.observe(viewLifecycleOwner) { medicines ->
            binding.tvMedicineCount.text = "${medicines.size} medicine(s) active today"
        }

        // Show adherence rate
        homeViewModel.loadAdherenceRate { rate ->
            activity?.runOnUiThread {
                binding.tvAdherenceRate.text = "7-day adherence: ${rate.toInt()}%"
            }
        }

        // ── SOS BUTTON ──────────────────────────────────────────────────────────
        binding.btnSos.setOnClickListener {
            triggerSOS()
        }
    }

    /**
     * Simulates an emergency SOS: shows the number and tries to dial it.
     */
    private fun triggerSOS() {
        val profile = homeViewModel.userProfile.value
        val phone = profile?.emergencyContactPhone

        if (phone.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "⚠ No emergency contact set! Please update your profile.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Show the SOS dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("🚨 Emergency SOS")
            .setMessage("Calling ${profile.emergencyContactName}\n$phone")
            .setPositiveButton("📞 Call Now") { _, _ ->
                // Try to make a phone call (requires CALL_PHONE permission)
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    startActivity(callIntent)
                } else {
                    // Fallback: open dialler with number pre-filled
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    startActivity(dialIntent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
