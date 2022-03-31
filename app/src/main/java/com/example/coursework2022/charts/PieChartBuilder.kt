package com.example.coursework2022.charts

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.example.coursework2022.usage_stats.AppUsageInfo
import com.example.coursework2022.utils.formatTime
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
import com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

private const val CHART_ANIMATION_DURATION = 400
private const val FAST_CHART_ANIMATION_DURATION = 300

@FragmentScoped
class PieChartBuilder @Inject constructor() {

  private var animatedOnce = false

  fun setupChart(chart: PieChart) {
    chart.description.isEnabled = false

    chart.setCenterTextSize(10f)

    // radius of the center hole in percent of maximum radius
    chart.holeRadius = 44f
    chart.transparentCircleRadius = 50f

    chart.legend.apply {
      verticalAlignment = TOP
      horizontalAlignment = RIGHT
      orientation = VERTICAL
      textSize = 13f
      setDrawInside(false)
    }
  }

  private fun generateCenterText(appUsageInfos: List<AppUsageInfo>): SpannableString {
    val totalSeconds = appUsageInfos.sumOf { it.usageTimeSeconds }
    val subtitle = "this day"
    val spanSource = "${formatTime(totalSeconds)}\n$subtitle"
    val i = spanSource.indexOf(subtitle)
    val s = SpannableString(spanSource)
    s.setSpan(RelativeSizeSpan(1.5f), 0, i, 0)
    s.setSpan(ForegroundColorSpan(Color.GRAY), i, s.length, 0)
    return s
  }

  fun updateData(chart: PieChart, appUsageInfos: List<AppUsageInfo>) {
    val entries1 = ArrayList<PieEntry>()
    val other = ArrayList<AppUsageInfo>()
    val totalTime = appUsageInfos.sumOf { it.usageTimeSeconds }.toFloat()

    for (appUsageInfo in appUsageInfos) {
      if (appUsageInfo.usageTimeSeconds.toFloat() / totalTime >= 0.15f) {
        entries1.add(PieEntry(appUsageInfo.usageTimeSeconds.toFloat()).apply {
          data = appUsageInfo
          label = appUsageInfo.appLabel
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
    ds1.setColors(*ColorTemplate.MATERIAL_COLORS)
    ds1.sliceSpace = 4f
    ds1.valueTextColor = Color.WHITE
    ds1.valueTextSize = 12f
    ds1.setDrawValues(false)

    chart.data = PieData(ds1)
    chart.centerText = generateCenterText(appUsageInfos)
    if (!animatedOnce) {
      chart.animateY(CHART_ANIMATION_DURATION, Easing.EaseInCirc)
      animatedOnce = true
    } else {
      chart.animateY(FAST_CHART_ANIMATION_DURATION, Easing.EaseInCirc)
    }
  }
}