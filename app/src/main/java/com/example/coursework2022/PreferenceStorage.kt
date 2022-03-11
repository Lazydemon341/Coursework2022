package com.example.coursework2022

import android.app.Activity
import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

const val BLACKLIST_PREFS_NAME = "blacklist_prefs"

const val KEY_BLACKLIST = "key_apps_blacklist"

@Singleton
class PreferenceStorage @Inject constructor(
  @ApplicationContext
  context: Context
) {

  private val prefs = context.getSharedPreferences(BLACKLIST_PREFS_NAME, Context.MODE_PRIVATE)

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
  }

  fun removeBlackListApp(packageName: String) {
    val blackList = getBlackListSet()
    blackList.remove(packageName)
    prefs.edit()
      .putStringSet(KEY_BLACKLIST, blackList)
      .apply()
  }

  private fun getBlackListSet(): MutableSet<String> {
    val set = prefs.getStringSet(KEY_BLACKLIST, emptySet())
    return set?.toMutableSet() ?: mutableSetOf()
  }
}