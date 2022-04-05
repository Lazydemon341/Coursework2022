package com.example.coursework2022.features.usage_stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework2022.features.usage_stats.UsageStatsInterval
import com.example.coursework2022.features.usage_stats.UsageStatsModel
import com.example.coursework2022.features.usage_stats.UsageStatsProvider
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

  private val _usageStatsModelsFlow = MutableStateFlow<List<UsageStatsModel>>(emptyList())
  val usageStatsModelsFlow = _usageStatsModelsFlow.asStateFlow()

  private var fetchUsageStatsJob: Job? = null

  fun getUsageStats(interval: UsageStatsInterval) {
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