package com.example.coursework2022.features.usage_stats

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.coursework2022.R
import com.example.coursework2022.ViewPagerFragment
import com.example.coursework2022.charts.BarChartBuilder
import com.example.coursework2022.charts.PieChartBuilder
import com.example.coursework2022.features.usage_stats.StatsUsageInterval.DAILY
import com.example.coursework2022.features.usage_stats.StatsUsageInterval.WEEKLY
import com.example.coursework2022.utils.formatTime
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsageStatsFragment : Fragment(R.layout.fragment_app_usage_statistics) {

  @Inject
  lateinit var pieChartBuilder: PieChartBuilder

  @Inject
  lateinit var barChartBuilder: BarChartBuilder

  private val viewModel: UsageStatsViewModel by viewModels()

  private lateinit var viewPagerFragment: ViewPagerFragment
  private lateinit var mUsageListAdapter: UsageStatsAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: RecyclerView.LayoutManager
  private lateinit var toggleButton: MaterialButtonToggleGroup
  private lateinit var scrollView: NestedScrollView
  private lateinit var scrollIndicator: ImageView
  private lateinit var content: View
  private lateinit var weeklyUsageTime: TextView
  private lateinit var barChart: BarChart
  private lateinit var pieChart: PieChart

  @SuppressLint("SetTextI18n")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewPagerFragment = parentFragment as ViewPagerFragment

    setupToggleButton(view)
    setupList(view)

    content = view.findViewById(R.id.content)
    scrollView = view.findViewById(R.id.scroll_view)
    scrollIndicator = view.findViewById(R.id.scroll_indicator)

    updateScrollIndicator()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toggleButton.clearOnButtonCheckedListeners()
  }

  override fun onResume() {
    super.onResume()
    content.viewTreeObserver.addOnScrollChangedListener(this::updateScrollIndicator)
  }

  override fun onPause() {
    super.onPause()
    content.viewTreeObserver.removeOnScrollChangedListener(this::updateScrollIndicator)
  }

  private fun setupList(view: View) {
    mRecyclerView = view.findViewById<View>(R.id.recyclerview_app_usage) as RecyclerView
    mRecyclerView.layoutManager = LinearLayoutManager(requireContext()).also { mLayoutManager = it }
    mUsageListAdapter = UsageStatsAdapter().apply {
      setHasStableIds(true)
      registerAdapterDataObserver(object : AdapterDataObserver() {
        override fun onChanged() {
          mRecyclerView.scrollToPosition(0)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
          mRecyclerView.scrollToPosition(0)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
          mRecyclerView.scrollToPosition(0)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
          mRecyclerView.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
          mRecyclerView.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
          mRecyclerView.scrollToPosition(0)
        }
      })
    }
    mRecyclerView.adapter = mUsageListAdapter
    mRecyclerView.itemAnimator = object : DefaultItemAnimator() {
      override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
      }
    }
    mRecyclerView.scrollToPosition(0)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.usageStatsModelsFlow.collect {
          mUsageListAdapter.submitList(it)
          pieChartBuilder.updateData(requireContext().resources, pieChart, it)
        }
      }
    }
  }

  private fun setupToggleButton(view: View) {
    weeklyUsageTime = view.findViewById(R.id.weekly_usage_time)

    pieChart = view.findViewById(R.id.pie_chart)
    pieChartBuilder.setupChart(pieChart)

    barChart = view.findViewById(R.id.bar_chart)
    barChartBuilder.setupChart(barChart)

    toggleButton = view.findViewById(R.id.toggle_button)
    toggleButton.addOnButtonCheckedListener { _, idRes, checked ->
      if (checked) {
        val button = view.findViewById<Button>(idRes)
        StatsUsageInterval.getValue(requireContext(), button.text.toString())?.let {
          viewModel.getUsageStats(it)
          when (it) {
            DAILY -> {
              pieChart.isVisible = true
              barChart.isVisible = false
            }
            WEEKLY -> {
              pieChart.isVisible = false
              barChart.isVisible = true

              val weekUsage = viewModel.getWeekUsage()
              barChartBuilder.updateData(barChart, weekUsage)

              val text = "${formatTime(weekUsage.sum())} this week"
              val i = text.indexOf("this week")
              val spannableString = SpannableString(text)
              spannableString.setSpan(RelativeSizeSpan(1.2f), 0, i, 0)
              spannableString.setSpan(ForegroundColorSpan(Color.GRAY), i, spannableString.length, 0)
              weeklyUsageTime.text = spannableString
            }
          }
          weeklyUsageTime.isVisible = barChart.isVisible
        }
      }
    }
    toggleButton.check(R.id.button_daily)
  }

  private fun updateScrollIndicator() {
    scrollIndicator.isVisible = scrollView.canScrollVertically(1)
    viewPagerFragment.updateTabLayoutShadow(scrollView.canScrollVertically(-1))
  }

  companion object {
    const val NAME = "Usage stats"
    fun newInstance(): UsageStatsFragment {
      return UsageStatsFragment()
    }
  }
}