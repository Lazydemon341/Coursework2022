package com.example.coursework2022.usage_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.coursework2022.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsageStatsFragment : Fragment() {
  private lateinit var mUsageListAdapter: UsageStatsAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: RecyclerView.LayoutManager
  private lateinit var mSpinner: Spinner

  private val viewModel: UsageStatsViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false)
  }

  override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
    super.onViewCreated(rootView, savedInstanceState)

    mRecyclerView = rootView.findViewById<View>(R.id.recyclerview_app_usage) as RecyclerView
    mRecyclerView.layoutManager = LinearLayoutManager(requireContext()).also { mLayoutManager = it }
    mRecyclerView.scrollToPosition(0)
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

    mSpinner = rootView.findViewById<View>(R.id.spinner_time_span) as Spinner
    val spinnerAdapter: SpinnerAdapter = ArrayAdapter.createFromResource(
      requireActivity(),
      R.array.action_list,
      android.R.layout.simple_spinner_dropdown_item
    )
    mSpinner.adapter = spinnerAdapter
    mSpinner.onItemSelectedListener = getOnSpinnerItemSelectedListener()

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.usageStatsModelsFlow.collect {
          mUsageListAdapter.submitList(it)
        }
      }
    }
  }

  private fun getOnSpinnerItemSelectedListener(): OnItemSelectedListener {
    return object : OnItemSelectedListener {
      var strings: Array<String> = resources.getStringArray(R.array.action_list)

      override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        StatsUsageInterval.getValue(strings[position])?.let { viewModel.getUsageStats(it) }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
  }

  companion object {
    fun newInstance(): UsageStatsFragment {
      return UsageStatsFragment()
    }
  }
}