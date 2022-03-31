package com.example.coursework2022.usage_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import androidx.recyclerview.widget.RecyclerView.INVISIBLE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.coursework2022.R
import com.example.coursework2022.charts.BarChartBuilder
import com.example.coursework2022.charts.PieChartBuilder
import com.example.coursework2022.usage_stats.StatsUsageInterval.DAILY
import com.example.coursework2022.usage_stats.StatsUsageInterval.WEEKLY
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsageStatsFragment : Fragment() {

  @Inject
  lateinit var pieChartBuilder: PieChartBuilder

  @Inject
  lateinit var barChartBuilder: BarChartBuilder

  private val viewModel: UsageStatsViewModel by viewModels()

  private lateinit var mUsageListAdapter: UsageStatsAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: RecyclerView.LayoutManager
  private lateinit var toggleButton: MaterialButtonToggleGroup
  private lateinit var scrollView: NestedScrollView
  private lateinit var scrollIndicator: ImageView
  private lateinit var content: View

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val pieChart = view.findViewById<PieChart>(R.id.pie_chart)
    pieChartBuilder.setupChart(pieChart)

    val barchart = view.findViewById<BarChart>(R.id.bar_chart)
    barChartBuilder.setupChart(barchart, requireContext())

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

    toggleButton = view.findViewById(R.id.toggle_button)
    toggleButton.addOnButtonCheckedListener { _, idRes, checked ->
      if (checked) {
        val button = view.findViewById<Button>(idRes)
        StatsUsageInterval.getValue(requireContext(), button.text.toString())?.let {
          viewModel.getUsageStats(it)
          when (it) {
            DAILY -> {
              pieChart.visibility = VISIBLE
              barchart.visibility = INVISIBLE
            }
            WEEKLY -> {
              pieChart.visibility = INVISIBLE
              barchart.visibility = VISIBLE
              barChartBuilder.updateData(barchart, viewModel.getWeekUsage())
            }
          }
        }
      }
    }
    toggleButton.check(R.id.button_daily)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.usageStatsModelsFlow.collect {
          mUsageListAdapter.submitList(it)
          pieChartBuilder.updateData(pieChart, it)
        }
      }
    }

    content = view.findViewById(R.id.content)
    scrollView = view.findViewById(R.id.scroll_view)
    scrollIndicator = view.findViewById(R.id.scroll_indicator)

    updateScrollIndicator()
    content.viewTreeObserver.addOnScrollChangedListener(this::updateScrollIndicator)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toggleButton.clearOnButtonCheckedListeners()
    content.viewTreeObserver.removeOnScrollChangedListener(this::updateScrollIndicator)
  }

  private fun updateScrollIndicator() {
    scrollIndicator.isVisible = scrollView.canScrollVertically(1)
  }

  companion object {
    const val NAME = "Usage stats"
    fun newInstance(): UsageStatsFragment {
      return UsageStatsFragment()
    }
  }
}