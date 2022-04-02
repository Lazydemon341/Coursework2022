package com.example.coursework2022.usage_stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
  private val usageStatsProvider: UsageStatsProvider
) : ViewModel() {

  private val _usageStatsModelsFlow = MutableStateFlow<List<AppUsageInfo>>(emptyList())
  val usageStatsModelsFlow = _usageStatsModelsFlow.asStateFlow()

  private var fetchUsageStatsJob: Job? = null

  fun getUsageStats(interval: StatsUsageInterval) {
    fetchUsageStatsJob?.cancel()
    fetchUsageStatsJob = viewModelScope.launch(Dispatchers.IO) {
      _usageStatsModelsFlow.value = usageStatsProvider
        .getUsageStats(interval)
        .sortedByDescending {
          it.usageTimeSeconds
        }
    }
  }

  fun getWeekUsage(): List<Long> {
    return usageStatsProvider.getWeekUsageTime()
  }
}