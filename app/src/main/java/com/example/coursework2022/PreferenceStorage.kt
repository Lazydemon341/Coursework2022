package com.example.coursework2022

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

const val BLACKLIST_PREFS_NAME = "blacklist_prefs"

const val KEY_BLACKLIST = "key_apps_blacklist"
const val KEY_FOCUS_MODE = "key_focus_mode"

@Singleton
class PreferenceStorage @Inject constructor(
  @ApplicationContext
  context: Context
) {

  private val prefs = context.getSharedPreferences(BLACKLIST_PREFS_NAME, Context.MODE_PRIVATE)

  private val _focusModeStatusFlow: MutableStateFlow<Boolean> = MutableStateFlow(getFocusModeStatus())
  val focusModeStatusFlow: StateFlow<Boolean> = _focusModeStatusFlow.asStateFlow()

  private val _blackListAppsFlow: MutableStateFlow<Set<String>> = MutableStateFlow(getBlackListSet())
  val blackListAppsFlow: StateFlow<Set<String>> = _blackListAppsFlow.asStateFlow()

  fun setFocusModeStatus(on: Boolean) {
    prefs.edit()
      .putBoolean(KEY_FOCUS_MODE, on)
      .apply()
    _focusModeStatusFlow.value = on
  }

  fun getFocusModeStatus(): Boolean {
    return prefs.getBoolean(KEY_FOCUS_MODE, false)
  }

  fun isBlackListApp(packageName: String): Boolean {
    return getBlackListSet().find {
      it == packageName
    } != null
  }

  fun addBlackListApp(packageName: String) {
    val blackList = getBlackListSet()
    blackList.add(packageName)
    prefs.edit()
      .putStringSet(KEY_BLACKLIST, blackList)
      .apply()
    _blackListAppsFlow.value = blackList
  }

  fun getBlackListApps(): Set<String> {
    val set = prefs.getStringSet(KEY_BLACKLIST, emptySet())
    return set ?: emptySet()
  }

  fun removeBlackListApp(packageName: String) {
    val blackList = getBlackListSet()
    blackList.remove(packageName)
    prefs.edit()
      .putStringSet(KEY_BLACKLIST, blackList)
      .apply()
    _blackListAppsFlow.value = blackList
  }

  private fun getBlackListSet(): MutableSet<String> {
    val set = prefs.getStringSet(KEY_BLACKLIST, emptySet())
    return set?.toMutableSet() ?: mutableSetOf()
  }
}