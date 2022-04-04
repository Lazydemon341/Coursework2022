package com.example.coursework2022.features.focus_mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework2022.PreferenceStorage
import com.example.coursework2022.features.schedules.ScheduleModel
import com.example.coursework2022.features.schedules.ScheduleModelsHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusModeViewModel @Inject constructor(
  private val preferenceStorage: PreferenceStorage,
  private val focusModeInteractor: FocusModeInteractor,
  private val scheduleModelsHolder: ScheduleModelsHolder
) : ViewModel() {

  private val appsList = focusModeInteractor.getAppsList()

  val schedulesFlow = scheduleModelsHolder.scheduleModelsFlow
    .map { schedules ->
      schedules.filter { it.active }
    }
    .flowOn(Dispatchers.IO)

  val focusModeStatusFlow = preferenceStorage.focusModeStatusFlow

  val blackListAppsFlow: Flow<List<FocusModeAppModel>> =
    preferenceStorage.blackListAppsFlow
      .map {
        it.map(focusModeInteractor::mapFocusModeAppModel)
          .sortedBy { app -> app.appLabel }
      }
      .flowOn(Dispatchers.Default)

  val allowedAppsFlow: Flow<List<FocusModeAppModel>> =
    preferenceStorage.blackListAppsFlow
      .map { blackList ->
        appsList
          .filter { app ->
            !blackList.contains(app.packageName)
          }
          .sortedBy { it.appLabel }
      }
      .flowOn(Dispatchers.Default)

  fun addToBlackList(app: FocusModeAppModel) {
    viewModelScope.launch(Dispatchers.Default) {
      preferenceStorage.addBlackListApp(app.packageName)
    }
  }

  fun removeFromBlackList(app: FocusModeAppModel) {
    viewModelScope.launch(Dispatchers.Default) {
      preferenceStorage.removeBlackListApp(app.packageName)
    }
  }

  fun setFocusModeStatus(focusModeOn: Boolean) {
    viewModelScope.launch(Dispatchers.Default) {
      preferenceStorage.setFocusModeStatus(focusModeOn)
    }
  }

  fun getFocusModeStatus(): Boolean {
    return preferenceStorage.getFocusModeStatus()
  }

  fun toggleSchedule(schedule: ScheduleModel, isActive: Boolean) {
    viewModelScope.launch(Dispatchers.Default) {
      scheduleModelsHolder.toggleSchedule(schedule, isActive)
    }
  }
}