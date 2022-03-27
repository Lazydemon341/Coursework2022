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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
import com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FocusModeFragment : Fragment() {

  private val viewModel: FocusModeViewModel by viewModels()

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  private lateinit var focusModeSwitch: SwitchMaterial

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
    setupChart(view)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupSwitch(view: View) {
    focusModeSwitch = view.findViewById(R.id.focus_mode_switch)
    focusModeSwitch.isChecked = preferenceStorage.getFocusModeStatus() && isAccessibilitySettingsOn(requireContext())
    focusModeSwitch.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        if (!(v as SwitchMaterial).isChecked && !isAccessibilitySettingsOn(requireContext())) {
          openAccessibilityService()
          return@setOnTouchListener true
        }
      }
      return@setOnTouchListener false
    }
    focusModeSwitch.setOnCheckedChangeListener { _, checked ->
      preferenceStorage.setFocusModeStatus(checked)
      val msg = if (checked) "FocusMode has been started!" else "FocusMode has been disabled"
      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
  }

  private fun setupChart(view: View) {
    val chart: PieChart = view.findViewById(R.id.pie_chart)

    chart.description.isEnabled = false

    chart.centerText = generateCenterText()
    chart.setCenterTextSize(10f)

    // radius of the center hole in percent of maximum radius

    // radius of the center hole in percent of maximum radius
    chart.holeRadius = 45f
    chart.transparentCircleRadius = 50f

    val l = chart.legend
    l.verticalAlignment = TOP
    l.horizontalAlignment = RIGHT
    l.orientation = VERTICAL
    l.setDrawInside(false)

    chart.data = generatePieData()
  }

  private fun generateCenterText(): SpannableString {
    val s = SpannableString("Revenues\nQuarters 2015")
    s.setSpan(RelativeSizeSpan(2f), 0, 8, 0)
    s.setSpan(ForegroundColorSpan(Color.GRAY), 8, s.length, 0)
    return s
  }

  private fun generatePieData(): PieData {
    val count = 4
    val entries1 = ArrayList<PieEntry>()
    for (i in 0 until count) {
      entries1.add(PieEntry((Math.random() * 60 + 40).toFloat(), "Quarter " + (i + 1)).apply {
        icon = drawable(R.drawable.ic_baseline_apps_24)
        //data = TODO()
      })
    }
    val ds1 = PieDataSet(entries1, "Quarterly Revenues 2015")
    ds1.setColors(*ColorTemplate.MATERIAL_COLORS)
    ds1.sliceSpace = 2f
    ds1.valueTextColor = Color.WHITE
    ds1.valueTextSize = 12f
    return PieData(ds1)
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
    fun newInstance() = FocusModeFragment()
  }
}