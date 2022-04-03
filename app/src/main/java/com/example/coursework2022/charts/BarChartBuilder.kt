package com.example.coursework2022.charts

import com.example.coursework2022.utils.formatTime
import com.example.coursework2022.utils.getShortWeekdayMinusDaysName
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

private const val CHART_ANIMATION_DURATION = 350
private val COLOR_TEMPLATE = intArrayOf(
  ColorTemplate.rgb("#64dd17")
)

@FragmentScoped
class BarChartBuilder @Inject constructor() {

  fun setupChart(chart: BarChart) {
    chart.description.isEnabled = false
    chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
      override fun onValueSelected(e: Entry?, h: Highlight?) {}

      override fun onNothingSelected() {}
    })

    chart.setDrawGridBackground(false)
    chart.setDrawBarShadow(false)
    chart.setScaleEnabled(false)

    chart.legend.isEnabled = false
    chart.axisRight.isEnabled = false

    val xAxis = chart.xAxis
    xAxis.position = BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        return getShortWeekdayMinusDaysName(6 - value.toInt())
      }
    }

    val leftAxis = chart.axisLeft
    leftAxis.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
        if (value == 0f) {
          return ""
        }
        return formatTime(value.toLong())
      }
    }
    leftAxis.axisMinimum = 0f
  }

  fun updateData(chart: BarChart, weekUsageTimes: List<Long>) {
    val entries = ArrayList<BarEntry>()
    for ((i, usage) in weekUsageTimes.withIndex()) {
      entries.add(BarEntry(i.toFloat(), usage.toFloat()).apply {
        data = i
      })
    }
    val ds = BarDataSet(entries, "")
    ds.setColors(*ColorTemplate.MATERIAL_COLORS)
    ds.setDrawValues(false)
    chart.data = BarData(listOf(ds))
    chart.animateY(CHART_ANIMATION_DURATION, Easing.EaseInCubic)
  }
}