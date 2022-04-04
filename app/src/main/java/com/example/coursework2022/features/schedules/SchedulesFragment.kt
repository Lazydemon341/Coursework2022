package com.example.coursework2022.features.schedules

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.timePicker
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.example.coursework2022.R
import com.example.coursework2022.utils.getShortWeekdayNames
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Calendar

@AndroidEntryPoint
class SchedulesFragment : Fragment(R.layout.fragment_schedules) {

  private lateinit var addScheduleButton: FloatingActionButton
  private lateinit var schedulesList: RecyclerView
  private lateinit var schedulesAdapter: SchedulesAdapter

  private val viewModel: SchedulesViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    schedulesList = view.findViewById(R.id.schedules_list)
    schedulesList.adapter = SchedulesAdapter().also {
      it.setOnSwitchClickListener { schedule, active ->
        viewModel.toggleSchedule(schedule, active)
      }
      it.setOnScheduleClickListener(::showDialogOnScheduleClick)
      schedulesAdapter = it
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.schedulesFlow.collect {
          schedulesAdapter.submitList(it)
        }
      }
    }

    addScheduleButton = view.findViewById(R.id.add_schedule_button)
    addScheduleButton.setOnClickListener {
      viewModel.currentModel = ScheduleModel(active = true)
      pickDaysOfWeek()
    }
  }

  @SuppressLint("CheckResult")
  private fun pickDaysOfWeek() {
    MaterialDialog(requireContext()).show {
      title(text = "Select days of week")
      listItemsMultiChoice(items = getShortWeekdayNames(), allowEmptySelection = false) { _, indexes, _ ->
        viewModel.currentModel = viewModel.currentModel.copy(daysOfWeek = indexes.map { DayOfWeek.of(it + 1) })
      }
      positiveButton(text = "Next") {
        pickStartTime()
      }
    }
  }

  private fun pickStartTime() {
    MaterialDialog(requireContext()).show {
      title(text = "Select start time")
      timePicker(show24HoursView = true) { _, calendar ->
        viewModel.currentModel = viewModel.currentModel.copy(
          startTime = LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
          )
        )
        pickEndTime(calendar)
      }
      positiveButton(text = "Next")
    }
  }

  private fun pickEndTime(calendar: Calendar) {
    MaterialDialog(requireContext()).show {
      title(text = "Select end time")
      timePicker(currentTime = calendar, requireFutureTime = true, show24HoursView = true) { _, calendar ->
        viewModel.currentModel = viewModel.currentModel.copy(
          endTime = LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
          )
        )
      }
      positiveButton(text = "Save") {
        viewModel.saveCurrentSchedule()
      }
    }
  }

  @SuppressLint("CheckResult")
  private fun showDialogOnScheduleClick(schedule: ScheduleModel) {
    MaterialDialog(requireContext()).show {
      title(text = "Schedule \"${schedule.name}\"")
      listItems(items = listOf("Edit", "Delete")) { _, index, _ ->
        when (index) {
          1 -> {
            viewModel.removeSchedule(schedule)
          }
          else -> {
            // no-op
          }
        }
      }
    }
  }

  companion object {
    fun newInstance() = SchedulesFragment()
  }
}