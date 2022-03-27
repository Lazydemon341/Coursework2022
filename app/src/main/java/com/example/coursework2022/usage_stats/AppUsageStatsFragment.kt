package com.example.coursework2022.usage_stats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R
import com.example.coursework2022.appLabel
import com.example.coursework2022.getAppsList
import java.util.Calendar

class AppUsageStatisticsFragment : Fragment() {
  private lateinit var mUsageStatsManager: UsageStatsManager
  private lateinit var mUsageListAdapter: AppUsageStatsAdapter
  private lateinit var mRecyclerView: RecyclerView
  private lateinit var mLayoutManager: RecyclerView.LayoutManager
  private lateinit var mOpenUsageSettingButton: Button
  private lateinit var mSpinner: Spinner

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mUsageStatsManager =
      requireActivity().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false)
  }

  override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
    super.onViewCreated(rootView, savedInstanceState)
    mUsageListAdapter = AppUsageStatsAdapter()
    mRecyclerView = rootView.findViewById<View>(R.id.recyclerview_app_usage) as RecyclerView
    mRecyclerView.layoutManager = LinearLayoutManager(requireContext()).also {
      mLayoutManager = it
    }
    mRecyclerView.scrollToPosition(0)
    mRecyclerView.adapter = mUsageListAdapter
    mOpenUsageSettingButton = rootView.findViewById<View>(R.id.button_open_usage_setting) as Button
    mSpinner = rootView.findViewById<View>(R.id.spinner_time_span) as Spinner
    val spinnerAdapter: SpinnerAdapter = ArrayAdapter.createFromResource(
      requireActivity(),
      R.array.action_list,
      android.R.layout.simple_spinner_dropdown_item
    )
    mSpinner.adapter = spinnerAdapter
    mSpinner.onItemSelectedListener = object : OnItemSelectedListener {
      var strings: Array<String> = resources.getStringArray(R.array.action_list)
      override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val statsUsageInterval = StatsUsageInterval.getValue(strings[position])
        if (statsUsageInterval != null) {
          val usageStatsList = getUsageStatistics(statsUsageInterval.mInterval).sortedByDescending {
            it.totalTimeInForeground
          }
          updateAppsList(usageStatsList)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
  }

  /**
   * Returns the [.mRecyclerView] including the time span specified by the
   * intervalType argument.
   *
   * @param intervalType The time interval by which the stats are aggregated.
   * Corresponding to the value of [UsageStatsManager].
   * E.g. [UsageStatsManager.INTERVAL_DAILY], [                     ][UsageStatsManager.INTERVAL_WEEKLY],
   *
   * @return A list of [android.app.usage.UsageStats].
   */
  fun getUsageStatistics(intervalType: Int): List<UsageStats> {
    // Get the app statistics since one year ago from the current time.
    val cal = Calendar.getInstance()
    cal.add(Calendar.YEAR, -1)
    val mainPackageNames = requireActivity().getAppsList().map { it.activityInfo.packageName }
    val queryUsageStats = mUsageStatsManager
      .queryUsageStats(intervalType, cal.timeInMillis, System.currentTimeMillis())
      .filter {
        it.packageName != context?.packageName
            && mainPackageNames.contains(it.packageName)
      }
    if (queryUsageStats.isEmpty()) {
      Log.i(TAG, "The user may not allow the access to apps usage. ")
      Toast.makeText(
        requireActivity(),
        getString(R.string.explanation_access_to_appusage_is_not_enabled),
        Toast.LENGTH_LONG
      ).show()
      mOpenUsageSettingButton.visibility = View.VISIBLE
      mOpenUsageSettingButton.setOnClickListener { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
    }
    return queryUsageStats
  }

  /**
   * Updates the [.mRecyclerView] with the list of [UsageStats] passed as an argument.
   *
   * @param usageStatsList A list of [UsageStats] from which update the
   * [.mRecyclerView].
   */
  private fun updateAppsList(usageStatsList: List<UsageStats>) {
    val appUsageStatsList: MutableList<AppUsageStatsModel> = ArrayList()
    for (i in usageStatsList.indices) {
      val usageStats = usageStatsList[i]
      val packageName = usageStats.packageName
      appUsageStatsList.add(
        AppUsageStatsModel(
          usageStats,
          appLabel(packageName),
          com.example.coursework2022.appLabel(packageName)
        )
      )
    }
    mUsageListAdapter.updateItems(appUsageStatsList)
    mRecyclerView.scrollToPosition(0)
  }

  companion object {
    private val TAG = AppUsageStatisticsFragment::class.java.simpleName
    fun newInstance(): AppUsageStatisticsFragment {
      return AppUsageStatisticsFragment()
    }
  }
}