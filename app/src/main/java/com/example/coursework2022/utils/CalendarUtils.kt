package com.example.coursework2022.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

private fun getLastMidnightUTCInternal(): ZonedDateTime {
  // The timezones we'll need
  val utc = ZoneId.of("UTC")
  val defaultZone = ZoneId.systemDefault()

  // Set the starting and ending times to be midnight in UTC time
  return LocalDate.now().atStartOfDay(defaultZone).withZoneSameInstant(utc)
}

fun getLastMidnightUTC(): Long {
  // The timezones we'll need
  val utc = ZoneId.of("UTC")
  val defaultZone = ZoneId.systemDefault()

  // Set the starting and ending times to be midnight in UTC time
  return LocalDate.now().atStartOfDay(defaultZone).withZoneSameInstant(utc).toInstant().toEpochMilli()
}

fun getNextMidnightUTC(): Long {
  // The timezones we'll need
  val utc = ZoneId.of("UTC")
  val defaultZone = ZoneId.systemDefault()

  // Set the starting and ending times to be midnight in UTC time
  return LocalDate.now().atStartOfDay(defaultZone).withZoneSameInstant(utc).plusDays(1).toInstant().toEpochMilli()
}