package com.example.coursework2022.features.schedules

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


private val defaultModels = arrayListOf(ScheduleModel(), ScheduleModel(), ScheduleModel())

@Singleton
class ScheduleModelsHolder @Inject constructor() {
  private val scheduleModels: ArrayList<ScheduleModel> = defaultModels

  private val _scheduleModelsFlow: MutableStateFlow<List<ScheduleModel>> = MutableStateFlow(scheduleModels)
  val scheduleModelsFlow = _scheduleModelsFlow.asStateFlow()

  fun getSchedules(): List<ScheduleModel> {
    return scheduleModels
  }

  fun addSchedule(schedule: ScheduleModel) {
    scheduleModels.add(schedule)
    _scheduleModelsFlow.value = scheduleModels
  }

  fun toggleSchedule(schedule: ScheduleModel, isActive: Boolean) {
    val index = scheduleModels.indexOfFirst { it.uuid == schedule.uuid }
    if (index < 0) {
      return
    }
    scheduleModels[index] = schedule.copy(active = isActive)
    _scheduleModelsFlow.value = scheduleModels
  }
}