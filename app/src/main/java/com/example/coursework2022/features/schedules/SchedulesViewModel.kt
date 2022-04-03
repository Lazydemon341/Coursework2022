package com.example.coursework2022.features.schedules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulesViewModel @Inject constructor(
  private val scheduleModelsHolder: ScheduleModelsHolder
) : ViewModel() {
  val schedulesFlow = scheduleModelsHolder.scheduleModelsFlow
  var currentModel = ScheduleModel()

  fun saveCurrentSchedule() {
    viewModelScope.launch(Dispatchers.Default) {
      scheduleModelsHolder.addSchedule(currentModel)
    }
  }

  fun toggleSchedule(schedule: ScheduleModel, isActive: Boolean) {
    viewModelScope.launch(Dispatchers.Default) {
      scheduleModelsHolder.toggleSchedule(schedule, isActive)
    }
  }
}