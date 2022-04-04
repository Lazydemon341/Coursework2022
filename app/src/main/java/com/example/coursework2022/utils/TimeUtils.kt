package com.example.coursework2022.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

fun getMidnightUtcMinusDays(minusDays: Int): Long {
  // The timezones we'll need
  val utc = ZoneId.of("UTC")
  val defaultZone = ZoneId.systemDefault()

  // Set the starting and ending times to be midnight in UTC time
  return LocalDate.now()
    .atStartOfDay(defaultZone)
    .withZoneSameInstant(utc)
    .minusDays(minusDays.toLong())
    .toInstant()
    .toEpochMilli()
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

fun getShortWeekdayNames(): List<String> {
  return (1..7).map {
    getShortWeekdayName(it)
  }
}

fun getShortWeekdayName(day: Int): String {
  val c = Calendar.getInstance()
  c.set(Calendar.DAY_OF_WEEK, day)
  return String.format("%ta", c)
}

fun getShortWeekdayMinusDaysName(minusDays: Int): String {
  val c = Calendar.getInstance()
  c.add(Calendar.DAY_OF_YEAR, -minusDays)
  return String.format("%ta", c)
}

fun LocalTime.format(): String {
  val hours = if (hour >= 10) hour else "0$hour"
  val minutes = if (minute >= 10) minute else "0$minute"
  return "$hours:$minutes"
}

fun formatTime(seconds: Long): String {
  val minutes = seconds / 60
  val hours = minutes / 60

  var res = ""

  if (hours > 0) {
    res += "$hours hrs"
  }

  if (minutes > 0) {
    res += if (res.isNotEmpty()) ", ${minutes % 60} min" else "$minutes min"
  }

  if (res.isEmpty() && seconds > 0) {
    res = "$seconds sec"
  }

  if (res.isEmpty()) {
    res = "0 sec"
  }

  return res
}