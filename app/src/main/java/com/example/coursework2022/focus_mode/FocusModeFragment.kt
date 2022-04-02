package com.example.coursework2022.focus_mode

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R
import com.example.coursework2022.R.layout
import com.example.coursework2022.utils.isAccessibilitySettingsOn
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FocusModeFragment : Fragment(layout.focus_mode_fragment) {

  private val viewModel: FocusModeViewModel by viewModels()

  private lateinit var focusModeButton: Button
  private lateinit var blackListApps: RecyclerView
  private lateinit var allowedApps: RecyclerView


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupFocusButton(view)
    setupBlacklistApps(view)
    setupAllowedApps(view)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupFocusButton(view: View) {
    focusModeButton = view.findViewById(R.id.focus_mode_button)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.focusModeStatusFlow.collect {
          focusModeButton.text =
            if (it && requireContext().isAccessibilitySettingsOn())
              "Stop FocusMode"
            else
              "Start FocusMode"
        }
      }
    }
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
  }

  private fun setupBlacklistApps(view: View) {
    blackListApps = view.findViewById(R.id.blacklist_apps)
    val adapter = FocusModeAppsAdapter()
    adapter.setOnAppClickListener(viewModel::removeFromBlackList)
    blackListApps.adapter = adapter
    blackListApps.itemAnimator = SlideInLeftAnimator()

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.blackListAppsFlow.collect {
          adapter.submitList(it)
          focusModeButton.isEnabled = it.isNotEmpty()
        }
      }
    }
  }

  private fun setupAllowedApps(view: View) {
    allowedApps = view.findViewById(R.id.allowed_apps)
    val adapter = FocusModeAppsAdapter()
    adapter.setOnAppClickListener(viewModel::addToBlackList)
    allowedApps.adapter = adapter
    allowedApps.itemAnimator = SlideInLeftAnimator()

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.allowedAppsFlow.collect {
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
            "Please choose service/Coursework2022",
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