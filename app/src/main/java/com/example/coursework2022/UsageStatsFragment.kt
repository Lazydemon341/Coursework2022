package com.example.coursework2022

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bot.box.appusage.contract.UsageContracts
import bot.box.appusage.handler.Monitor
import bot.box.appusage.model.AppData
import bot.box.appusage.utils.DurationRange
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsageStatsFragment : Fragment(), UsageContracts.View {

  private var adapter: UsageStatsAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.usage_stats_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    if (Monitor.hasUsagePermission()) {
      Monitor.scan().getAppLists(this).fetchFor(DurationRange.TODAY)
      init()
    } else {
      Monitor.requestUsagePermission()
    }
  }

  private fun init() {
    val mRecycler = requireView().findViewById<RecyclerView>(R.id.recycler)
    adapter = UsageStatsAdapter()
    val spinner = requireView().findViewById<Spinner>(R.id.spinner)
    spinner.visibility = View.VISIBLE
    val spinnerAdapter = ArrayAdapter(
      requireContext(),
      android.R.layout.simple_list_item_1, resources.getStringArray(R.array.duration)
    )
    spinner.adapter = spinnerAdapter
    spinner.onItemSelectedListener = object: OnItemSelectedListener{
      override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        Monitor.scan().getAppLists(this@UsageStatsFragment).fetchFor(i)
      }

      override fun onNothingSelected(adapterView: AdapterView<*>?) {
      }
    }
    val mLayoutManager = LinearLayoutManager(requireContext())
    mRecycler.layoutManager = mLayoutManager
    mRecycler.adapter = adapter
  }
  override fun showProgress() {}
  override fun hideProgress() {}

  /**
   * @param usageData   list of application that has been within the duration for which query has been made.
   * @param mTotalUsage a sum total of the usage by each and every app with in the request duration.
   * @param duration    the same duration for which query has been made i.e.fetchFor(Duration...)
   */
  override fun getUsageData(usageData: List<AppData>, mTotalUsage: Long, duration: Int) {
    adapter?.updateData(usageData)
  }

  companion object {
    fun newInstance() = UsageStatsFragment()
  }
}
