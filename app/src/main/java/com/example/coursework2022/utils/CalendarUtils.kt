package com.example.coursework2022.utils

import java.util.Calendar
import java.util.GregorianCalendar

fun getLastMidnightMillis(): Long =
  getLastMidnight().timeInMillis

fun getLastMidnight(): Calendar {
  val midnight = GregorianCalendar().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
  }
  return midnight
}

fun getNextMidnight(): Calendar {
  val midnight = GregorianCalendar().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    add(Calendar.DAY_OF_MONTH, 1)
  }
  return midnight
}

fun getNextMidnightMillis(): Long =
  getNextMidnight().timeInMillis