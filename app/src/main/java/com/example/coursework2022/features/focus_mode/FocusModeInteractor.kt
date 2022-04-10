package com.example.coursework2022.features.focus_mode

import com.example.coursework2022.utils.AppInfosHolder
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class FocusModeInteractor @Inject constructor(
  private val appInfosHolder: AppInfosHolder
) {
  fun mapFocusModeAppModel(packageName: String): FocusModeAppModel {
    return FocusModeAppModel(
      packageName = packageName,
      appLabel = appInfosHolder.getAppLabel(packageName),
      appIcon = appInfosHolder.getAppIcon(packageName)
    )
  }

  fun getAppsList(): List<FocusModeAppModel> {
    return appInfosHolder.getAppsList()
      .map(this::mapFocusModeAppModel)
  }
}