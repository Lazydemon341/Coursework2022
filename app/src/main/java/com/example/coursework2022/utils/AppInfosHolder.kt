package com.example.coursework2022.utils

import android.content.Context
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfosHolder @Inject constructor(
  @ApplicationContext
  private val context: Context
) {
  private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val appsList: MutableList<String> = mutableListOf()
  private val appIconByPackageName: MutableMap<String, Drawable?> = mutableMapOf()
  private val appLabelByPackageName: MutableMap<String, String> = mutableMapOf()

  fun init() {
    scope.launch {
      getAppsList()
        .forEach {
          appIconByPackageName[it] = appIcon(it)
          appLabelByPackageName[it] = appLabel(context, it)
        }
    }
  }

  fun getAppLabel(packageName: String): String {
    return appLabelByPackageName[packageName] ?: appLabel(context, packageName).also {
      appLabelByPackageName[packageName] = it
    }
  }

  fun getAppIcon(packageName: String): Drawable? {
    return appIconByPackageName[packageName] ?: appIcon(packageName)
  }

  fun getAppsList(): List<String> {
    if (appsList.isNotEmpty()) {
      return appsList
    }
    return context.getAppsList()
      .map { it.activityInfo.packageName }
      .also {
        appsList.clear()
        appsList.addAll(it)
      }
  }
}