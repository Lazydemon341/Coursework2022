package com.example.coursework2022

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.coursework2022.utils.drawable
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
import com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
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
    setupSwitch(view)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupSwitch(view: View) {
    focusModeButton = view.findViewById(R.id.focus_mode_button)
    focusModeButton.isEnabled = isAccessibilitySettingsOn(requireContext())
    focusModeButton.setOnTouchDisabledListener {
      if (!isAccessibilitySettingsOn(requireContext())) {
        openAccessibilityService()
      }
    }
    focusModeButton.setOnClickListener {
      preferenceStorage.setFocusModeStatus(true)
      val msg = if (true) "FocusMode has been started!" else "FocusMode has been disabled"
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