package com.example.coursework2022.usage_stats

import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.coursework2022.utils.appIcon
import com.example.coursework2022.utils.appLabel
import com.example.coursework2022.utils.getAppsList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UsageStatsProvider @Inject constructor(
  @ApplicationContext
  private val context: Context
) {

  private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
  private val mainPackageNames = context.getAppsList().map { it.activityInfo.packageName }
  private val usageStatsByInterval: MutableMap<StatsUsageInterval, List<AppUsageInfo>> = mutableMapOf()
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  init {
    scope.launch {
      StatsUsageInterval.values().forEach {
        getAndSaveUsageStats(it)
      }
    }
  }

  suspend fun getUsageStats(interval: StatsUsageInterval): List<AppUsageInfo> {
    val savedUsageStats = usageStatsByInterval[interval]
    return if (savedUsageStats.isNullOrEmpty()) {
      getAndSaveUsageStats(interval)
    } else {
      savedUsageStats
    }
  }

  private suspend fun getAndSaveUsageStats(interval: StatsUsageInterval): List<AppUsageInfo> {
    return withContext(Dispatchers.Default) {
      getUsageStatsInternal(interval.getStartTime(), interval.getEndTime())
        .filter {
          it.usageTimeSeconds > 0 || it.launchesCount > 0
        }
        .also {
          usageStatsByInterval[interval] = it
        }
    }
  }

  private fun getUsageStatsInternal(date: LocalDate = LocalDate.now()): List<AppUsageInfo> {
    // The timezones we'll need
    val utc = ZoneId.of("UTC")
    val defaultZone = ZoneId.systemDefault()

    // Set the starting and ending times to be midnight in UTC time
    val startDate = date.atStartOfDay(defaultZone).withZoneSameInstant(utc)
    val startTime = startDate.toInstant().toEpochMilli()
    val endTime = startDate.plusDays(1).toInstant().toEpochMilli()

    var currentEvent: Event
    val usageEvents: MutableList<Event> = ArrayList()
    val appUsageMap: MutableMap<String, UsageTimeAndLaunches> = mutableMapOf()
    val query = usageStatsManager.queryEvents(startTime, endTime) ?: return emptyList()

    while (query.hasNextEvent()) {
      currentEvent = Event()
      query.getNextEvent(currentEvent)
      if (mainPackageNames.contains(currentEvent.packageName)
        && (currentEvent.eventType == Event.ACTIVITY_RESUMED
            || currentEvent.eventType == Event.ACTIVITY_PAUSED)
      ) {
        usageEvents.add(currentEvent)
        val key = currentEvent.packageName
        if (appUsageMap[key] == null) appUsageMap[key] = UsageTimeAndLaunches()
      }
    }

    for (i in 0 until usageEvents.size - 1) {
      currentEvent = usageEvents[i]
      val nextEvent = usageEvents[i + 1]
      var prevValue = appUsageMap[currentEvent.packageName] ?: UsageTimeAndLaunches()

      if (currentEvent.eventType == Event.ACTIVITY_RESUMED) {
        // increment launches
        prevValue = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount + 1,
          usageTimeSeconds = prevValue.usageTimeSeconds
        )
      }
      if (currentEvent.className == nextEvent.className
        && nextEvent.eventType == Event.ACTIVITY_PAUSED
      ) {
        // add usage time
        val diffSeconds = (nextEvent.timeStamp - currentEvent.timeStamp).toInt() / 1000
        appUsageMap[currentEvent.packageName] = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount,
          usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds
        )
      }
    }

    val lastEvent = usageEvents[usageEvents.size - 1]
    if (lastEvent.eventType == Event.ACTIVITY_RESUMED) {
      val diffSeconds = System.currentTimeMillis().toInt() - lastEvent.timeStamp.toInt() / 1000
      val prevValue = appUsageMap[lastEvent.packageName] ?: UsageTimeAndLaunches()
      appUsageMap[lastEvent.packageName] = UsageTimeAndLaunches(
        launchesCount = prevValue.launchesCount + 1,
        usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds
      )
    }

    return appUsageMap.map {
      val value = it.value
      AppUsageInfo(
        packageName = it.key,
        usageTimeSeconds = value.usageTimeSeconds,
        launchesCount = value.launchesCount,
        appLabel = context.appLabel(it.key),
        appIcon = appIcon(it.key)
      )
    }
  }


  private fun getUsageStatsInternal(startTime: Long, endTime: Long): List<AppUsageInfo> {
    var currentEvent: Event
    val usageEvents: MutableList<Event> = ArrayList()
    val appUsageMap: MutableMap<String, UsageTimeAndLaunches> = mutableMapOf()
    val query = usageStatsManager.queryEvents(startTime, endTime) ?: return emptyList()

    while (query.hasNextEvent()) {
      currentEvent = Event()
      query.getNextEvent(currentEvent)
      if (mainPackageNames.contains(currentEvent.packageName)
        && (currentEvent.eventType == Event.ACTIVITY_RESUMED
            || currentEvent.eventType == Event.ACTIVITY_PAUSED)
      ) {
        usageEvents.add(currentEvent)
        val key = currentEvent.packageName
        if (appUsageMap[key] == null) appUsageMap[key] = UsageTimeAndLaunches()
      }
    }

    for (i in 0 until usageEvents.size - 1) {
      currentEvent = usageEvents[i]
      val nextEvent = usageEvents[i + 1]
      var prevValue = appUsageMap[currentEvent.packageName] ?: UsageTimeAndLaunches()

      if (currentEvent.eventType == Event.ACTIVITY_RESUMED) {
        // increment launches
        prevValue = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount + 1,
          usageTimeSeconds = prevValue.usageTimeSeconds,
          lastTimeUsed = currentEvent.timeStamp
        )
      }
      if (currentEvent.className == nextEvent.className
        && nextEvent.eventType == Event.ACTIVITY_PAUSED
      ) {
        // add usage time
        val diffSeconds = (nextEvent.timeStamp - currentEvent.timeStamp).toInt() / 1000
        appUsageMap[currentEvent.packageName] = UsageTimeAndLaunches(
          launchesCount = prevValue.launchesCount,
          usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds,
          lastTimeUsed = prevValue.lastTimeUsed
        )
      }
    }

    val lastEvent = usageEvents[usageEvents.size - 1]
    if (lastEvent.eventType == Event.ACTIVITY_RESUMED) {
      val diffSeconds = System.currentTimeMillis().toInt() - lastEvent.timeStamp.toInt() / 1000
      val prevValue = appUsageMap[lastEvent.packageName] ?: UsageTimeAndLaunches()
      appUsageMap[lastEvent.packageName] = UsageTimeAndLaunches(
        launchesCount = prevValue.launchesCount + 1,
        usageTimeSeconds = prevValue.usageTimeSeconds + diffSeconds,
        lastTimeUsed = lastEvent.timeStamp
      )
    }

    return appUsageMap.map {
      val value = it.value
      AppUsageInfo(
        packageName = it.key,
        usageTimeSeconds = value.usageTimeSeconds,
        lastTimeUsedMillis = value.lastTimeUsed,
        launchesCount = value.launchesCount,
        appLabel = context.appLabel(it.key),
        appIcon = appIcon(it.key)
      )
    }
  }
}

private class UsageTimeAndLaunches(
  var launchesCount: Int = 0,
  var usageTimeSeconds: Int = 0,
  var lastTimeUsed: Long? = null
)