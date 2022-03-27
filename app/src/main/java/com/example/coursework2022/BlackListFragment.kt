package com.example.coursework2022

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.example.coursework2022.R.string
import com.example.coursework2022.utils.getAppsList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BlackListFragment : PreferenceFragmentCompat() {

  private val viewModel: BlackListViewModel by viewModels()

  @Inject
  lateinit var preferenceStorage: PreferenceStorage

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.pref_black_list_apps)

    (findPreference(getString(string.pref_black_list_key)) as PreferenceCategory?)?.let {
      setUpAllApps(it)
    }
  }

  @SuppressLint("QueryPermissionsNeeded")
  private fun setUpAllApps(preferenceCategory: PreferenceCategory) {
    val packageManager = requireActivity().packageManager
    val main = Intent(Intent.ACTION_MAIN, null)
    main.addCategory(Intent.CATEGORY_LAUNCHER);
    val apps = requireActivity().getAppsList().sortedBy {
      it.activityInfo.loadLabel(packageManager).toString()
    }
    for (app in apps) {
      val label = app.activityInfo.loadLabel(packageManager)
      val icon: Drawable = app.activityInfo.loadIcon(packageManager)
      val packageName = app.activityInfo.packageName
      val checkBoxPref = CheckBoxPreference(requireContext())
      checkBoxPref.title = label
      checkBoxPref.key = packageName
      checkBoxPref.icon = icon
      checkBoxPref.isChecked = false
      checkBoxPref.onPreferenceClickListener = OnPreferenceClickListener { preference ->
        if (preference is CheckBoxPreference) {
          changeBlacklistApps(preference.key, preference.isChecked)
          return@OnPreferenceClickListener true
        }
        return@OnPreferenceClickListener false
      }
      preferenceCategory.addPreference(checkBoxPref)
    }
  }

  private fun changeBlacklistApps(packageName: String, inBlacklist: Boolean) {
    if (inBlacklist) {
      preferenceStorage.addBlackListApp(packageName)
    } else {
      preferenceStorage.removeBlackListApp(packageName)
    }
  }

  companion object {
    fun newInstance() = BlackListFragment()
  }
}
