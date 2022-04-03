package com.example.coursework2022.features.schedules

import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

data class ScheduleModel(
  val uuid: UUID = UUID.randomUUID(),
  val startTime: LocalTime = LocalTime.MAX,
  val endTime: LocalTime = LocalTime.MIDNIGHT,
  val daysOfWeek: List<DayOfWeek> = emptyList(),
  val active: Boolean = false
)

fun ScheduleModel.isGoingNow(): Boolean {
  if (!active) {
    return false
  }
  val now = LocalTime.now()
  return now.isAfter(startTime) && now.isBefore(endTime)
}
