package com.example.coursework2022.features.focus_mode

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.MainActivity
import com.example.coursework2022.R
import com.example.coursework2022.R.layout
import com.example.coursework2022.ViewPagerFragment
import com.example.coursework2022.features.schedules.SchedulesAdapter
import com.example.coursework2022.utils.isAccessibilitySettingsOn
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FocusModeFragment : Fragment(layout.focus_mode_fragment) {

  private val viewModel: FocusModeViewModel by viewModels()

  private lateinit var viewPagerFragment: ViewPagerFragment
  private lateinit var scrollView: NestedScrollView
  private lateinit var content: View
  private lateinit var scrollIndicator: ImageView
  private lateinit var focusModeButton: Button
  private lateinit var blacklistTitle: TextView
  private lateinit var blacklistApps: RecyclerView
  private lateinit var allowedTitle: TextView
  private lateinit var allowedApps: RecyclerView
  private lateinit var schedulesButton: Button

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupScrollView(view)
    setupFocusButton(view)
    setupBlacklistApps(view)
    setupAllowedApps(view)
    setupSchedules(view)
  }

  override fun onResume() {
    super.onResume()
    content.viewTreeObserver.addOnScrollChangedListener(this::updateScrollIndicator)
  }

  override fun onPause() {
    super.onPause()
    content.viewTreeObserver.removeOnScrollChangedListener(this::updateScrollIndicator)
  }

  private fun setupScrollView(view: View) {
    viewPagerFragment = parentFragment as ViewPagerFragment
    content = view.findViewById(R.id.content)
    scrollView = view.findViewById(R.id.scroll_view)
    scrollIndicator = view.findViewById(R.id.scroll_indicator)
  }

  private fun updateScrollIndicator() {
    viewPagerFragment.updateTabLayoutShadow(scrollView.canScrollVertically(-1))
    scrollIndicator.isVisible = scrollView.canScrollVertically(1)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupFocusButton(view: View) {
    focusModeButton = view.findViewById(R.id.focus_mode_button)
    focusModeButton.setOnClickListener {
      if (!requireContext().isAccessibilitySettingsOn()) {
        openAccessibilityService()
        return@setOnClickListener
      }
      val focusModeOn = !viewModel.getFocusModeStatus()
      viewModel.setFocusModeStatus(focusModeOn)
      val msg = if (focusModeOn) "FocusMode has been started!" else "FocusMode has been stopped"
      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.blackListAppsFlow.combine(viewModel.focusModeStatusFlow) { blacklist, focusModeOn ->
          focusModeButton.isEnabled = if (focusModeOn && requireContext().isAccessibilitySettingsOn()) {
            true
          } else {
            blacklist.isNotEmpty()
          }
          focusModeButton.text =
            if (focusModeOn && requireContext().isAccessibilitySettingsOn())
              "Stop FocusMode"
            else
              "Start FocusMode"
        }
          .flowOn(Dispatchers.Main.immediate)
          .collect {}
      }
    }
  }

  private fun setupBlacklistApps(view: View) {
    blacklistTitle = view.findViewById(R.id.blacklist_title)
    allowedTitle = view.findViewById(R.id.allowed_title)
    blacklistApps = view.findViewById(R.id.blacklist_apps)
    val adapter = FocusModeAppsAdapter()
    adapter.setOnAppClickListener(viewModel::removeFromBlackList)
    blacklistApps.adapter = adapter
    blacklistApps.itemAnimator = FadeInUpAnimator()

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.blackListAppsFlow.collect {
          blacklistTitle.text = if (it.isEmpty()) "Select apps for blacklist:" else "Your blacklist:"
          allowedTitle.isVisible = it.isNotEmpty()
          adapter.submitList(it)
        }
      }
    }
  }

  private fun setupAllowedApps(view: View) {
    allowedApps = view.findViewById(R.id.allowed_apps)
    val adapter = FocusModeAppsAdapter()
    adapter.setOnAppClickListener(viewModel::addToBlackList)
    allowedApps.adapter = adapter
    allowedApps.itemAnimator = FadeInDownAnimator()

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.allowedAppsFlow.collect {
          adapter.submitList(it)
        }
      }
    }
  }

  private fun setupSchedules(view: View) {
    schedulesButton = view.findViewById(R.id.schedules_button)
    schedulesButton.setOnClickListener {
      (requireActivity() as MainActivity).openSchedules()
    }

    val schedulesList = view.findViewById<RecyclerView>(R.id.schedules_list)
    schedulesList.itemAnimator = FadeInAnimator()
    val adapter = SchedulesAdapter().also {
      it.setOnSwitchClickListener { schedule, active ->
        viewModel.toggleSchedule(schedule, active)
      }
    }
    schedulesList.adapter = adapter

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.schedulesFlow.collect {
          schedulesButton.text = if (it.isEmpty()) "Add schedules" else "Edit schedules"
          adapter.submitList(it)
        }
      }
    }
  }

  private fun openAccessibilityService() {
    if (!requireContext().isAccessibilitySettingsOn()) {
      val dialog = Builder(requireContext())
        .setTitle("Accessibility service")
        .setMessage("Please turn on accessibility service")
        .setPositiveButton("OK") { _, _ ->
          Toast.makeText(
            requireContext(),
            "Please turn on UsageManager service",
            Toast.LENGTH_SHORT
          ).show()
          val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
          intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
      dialog.show()
    }
  }

  companion object {
    const val NAME = "Focus mode"
    fun newInstance() = FocusModeFragment()
  }
}