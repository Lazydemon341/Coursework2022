package com.example.coursework2022.features.schedules

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

private val defaultModels = arrayListOf(
  ScheduleModel(
    name = "Morning",
    startTime = LocalTime.of(9, 0),
    endTime = LocalTime.of(12, 0),
    daysOfWeek = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
  ),
  ScheduleModel(
    name = "Afternoon",
    startTime = LocalTime.of(14, 0),
    endTime = LocalTime.of(17, 0),
    daysOfWeek = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
  ),
  ScheduleModel(
    name = "Bedtime",
    startTime = LocalTime.of(23, 0),
    endTime = LocalTime.of(7, 0),
    daysOfWeek = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY),
    active = true
  ),
)

private var mySchedulesCount = 0

@Singleton
class ScheduleModelsHolder @Inject constructor() {
  private val _scheduleModelsFlow: MutableStateFlow<List<ScheduleModel>> = MutableStateFlow(defaultModels)
  val scheduleModelsFlow: StateFlow<List<ScheduleModel>> = _scheduleModelsFlow.asStateFlow()

  fun addSchedule(schedule: ScheduleModel) {
    val scheduleInner =
      if (schedule.name.isEmpty()) {
        mySchedulesCount += 1
        schedule.copy(name = "MySchedule$mySchedulesCount")
      } else {
        schedule
      }
    _scheduleModelsFlow.value = _scheduleModelsFlow.value.toMutableList().apply {
      add(scheduleInner)
    }
  }

  fun removeSchedule(schedule: ScheduleModel) {
    _scheduleModelsFlow.value = _scheduleModelsFlow.value.toMutableList().apply {
      removeIf { it.uuid == schedule.uuid }
    }
  }

  fun toggleSchedule(schedule: ScheduleModel, isActive: Boolean) {
    val index = _scheduleModelsFlow.value.indexOfFirst { it.uuid == schedule.uuid }
    if (index < 0) {
      return
    }
    _scheduleModelsFlow.value = _scheduleModelsFlow.value.toMutableList().apply {
      set(index, schedule.copy(active = isActive))
    }
  }
}