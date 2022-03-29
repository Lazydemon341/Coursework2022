package com.example.coursework2022.usage_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsageStatsFragment : Fragment() {
  private lateinit var mUsageListAdapter: UsageStatsAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: RecyclerView.LayoutManager
  private lateinit var toggleButton: MaterialButtonToggleGroup

  private val viewModel: UsageStatsViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

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
        }
      }
    }
    toggleButton.check(R.id.button_daily)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.usageStatsModelsFlow.collect {
          mUsageListAdapter.submitList(it)
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toggleButton.clearOnButtonCheckedListeners()
  }

  companion object {
    const val NAME = "Usage stats"
    fun newInstance(): UsageStatsFragment {
      return UsageStatsFragment()
    }
  }
}