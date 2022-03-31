package com.example.coursework2022

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

  private lateinit var focusModeButton: FocusModeButton

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.focus_mode_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupFocusButton(view)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupFocusButton(view: View) {
    focusModeButton = view.findViewById(R.id.focus_mode_button)
    focusModeButton.isEnabled = isAccessibilitySettingsOn(requireContext())
    focusModeButton.text =
      if (preferenceStorage.getFocusModeStatus() && isAccessibilitySettingsOn(requireContext()))
        "Stop FocusMode"
      else
        "Start FocusMode"
    focusModeButton.setOnTouchDisabledListener {
      if (!isAccessibilitySettingsOn(requireContext())) {
        openAccessibilityService()
      }
    }
    focusModeButton.setOnClickListener {
      val focusModeOn = !preferenceStorage.getFocusModeStatus()
      preferenceStorage.setFocusModeStatus(focusModeOn)
      focusModeButton.text =
        if (focusModeOn)
          "Stop FocusMode"
        else
          "Start FocusMode"
      val msg = if (focusModeOn) "FocusMode has been started!" else "FocusMode has been stopped"
      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
  }

  private fun isAccessibilitySettingsOn(context: Context): Boolean {
    try {
      val accessibilityEnabled = Secure.getInt(
        context.contentResolver,
        Secure.ACCESSIBILITY_ENABLED
      )
      if (accessibilityEnabled == 1) {
        val services = Secure.getString(
          context.contentResolver,
          Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (services != null) {
          return services.contains(context.packageName, ignoreCase = true)
        }
      }
    } catch (e: Throwable) {
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
    const val NAME = "Focus mode"
    fun newInstance() = FocusModeFragment()
  }
}