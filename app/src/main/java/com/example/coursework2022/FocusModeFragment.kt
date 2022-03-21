package com.example.coursework2022

import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FocusModeFragment : Fragment() {

  private val viewModel: FocusModeViewModel by viewModels()

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  private lateinit var button: Button

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.focus_mode_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    button = view.findViewById(R.id.button)
    button.setOnClickListener {
      if (isAccessibilitySettingsOn(requireContext())) {
        preferenceStorage.setFocusModeStatus(true)
        Toast.makeText(requireContext(), "FocusMode has been started!", Toast.LENGTH_SHORT).show()
      } else {
        openAccessibilityService()
      }
    }
  }

  private fun isAccessibilitySettingsOn(context: Context): Boolean {
    var accessibilityEnabled = 0
    try {
      accessibilityEnabled = Secure.getInt(
        context.contentResolver,
        Secure.ACCESSIBILITY_ENABLED
      )
    } catch (e: Throwable) {
    }
    if (accessibilityEnabled == 1) {
      val services = Secure.getString(
        context.contentResolver,
        Secure.ENABLED_ACCESSIBILITY_SERVICES
      )
      if (services != null) {
        return services.contains(context.packageName, ignoreCase = true)
      }
    }
    return false
  }

  private fun openAccessibilityService() {
    if (!isAccessibilitySettingsOn(requireContext())) {
      val dialog = Builder(requireContext())
        .setTitle("Accessibility service")
        .setMessage("Please turn on accessibility service")
        .setPositiveButton("OK") { _, _ ->
          Toast.makeText(
            requireContext(),
            "Please click on service/Coursework2022",
            Toast.LENGTH_SHORT
          ).show()
          val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
      dialog.show()
    }
  }

  companion object {
    fun newInstance() = FocusModeFragment()
  }
}