package com.example.coursework2022.charts

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.graphics.drawable.toBitmap
import com.example.coursework2022.features.usage_stats.AppUsageInfo
import com.example.coursework2022.utils.formatTime
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val CHART_ANIMATION_DURATION = 350
private val COLOR_TEMPLATE = intArrayOf(
  ColorTemplate.rgb("#2ecc71"),
  ColorTemplate.rgb("#f1c40f"),
  ColorTemplate.rgb("#e74c3c"),
  ColorTemplate.rgb("#3498db"),
  ColorTemplate.rgb("#f1c40f"),
  ColorTemplate.rgb("#e74c3c"),
  ColorTemplate.rgb("#3498db")
)

@FragmentScoped
class PieChartBuilder @Inject constructor() {

  fun setupChart(chart: PieChart) {
    chart.description.isEnabled = false

    chart.setCenterTextSize(13f)

    // radius of the center hole in percent of maximum radius
    chart.holeRadius = 44f
    chart.transparentCircleRadius = 50f

    chart.setEntryLabelColor(Color.BLACK)

    chart.legend.isEnabled = false
  }

  private fun generateCenterText(appUsageInfos: List<AppUsageInfo>): SpannableString {
    val totalSeconds = appUsageInfos.sumOf { it.usageTimeSeconds }
    val subtitle = "this day"
    val spanSource = "${formatTime(totalSeconds)}\n$subtitle"
    val i = spanSource.indexOf(subtitle)
    val s = SpannableString(spanSource)
    s.setSpan(RelativeSizeSpan(1.2f), 0, i, 0)
    s.setSpan(ForegroundColorSpan(Color.GRAY), i, s.length, 0)
    return s
  }

  fun updateData(resources: Resources, chart: PieChart, appUsageInfos: List<AppUsageInfo>) {
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
      val entries1 = ArrayList<PieEntry>()
      val other = ArrayList<AppUsageInfo>()

      for ((i, appUsageInfo) in appUsageInfos.withIndex()) {
        if (i < 4) {
          entries1.add(PieEntry(appUsageInfo.usageTimeSeconds.toFloat()).apply {
            data = appUsageInfo
            label = ""

            val bitmap = (appUsageInfo.appIcon)?.toBitmap(108, 108)
            icon = BitmapDrawable(resources, bitmap)
          })
        } else {
          other.add(appUsageInfo)
        }
      }

      val otherTime = other.sumOf { it.usageTimeSeconds }.toFloat()
      entries1.add(PieEntry(otherTime).apply {
        label = "Other"
      })

      val ds1 = PieDataSet(entries1, "")
      ds1.setColors(*COLOR_TEMPLATE)
      ds1.sliceSpace = 4f
      ds1.setDrawValues(false)

      chart.data = PieData(ds1)
      chart.centerText = generateCenterText(appUsageInfos)

      launch(Dispatchers.Main) {
        chart.animateY(CHART_ANIMATION_DURATION, Easing.EaseInCirc)
      }
    }
  }
}