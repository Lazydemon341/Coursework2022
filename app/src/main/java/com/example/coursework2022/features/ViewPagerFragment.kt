package com.example.coursework2022.features

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.coursework2022.R
import com.example.coursework2022.R.dimen
import com.example.coursework2022.R.layout
import com.example.coursework2022.features.focus_mode.FocusModeFragment
import com.example.coursework2022.features.usage_stats.presentation.UsageStatsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment(layout.fragment_pager) {

  private lateinit var tabLayout: TabLayout

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val viewPager = view.findViewById<ViewPager2>(R.id.pager)
    viewPager.adapter = PagerAdapter(this)
    viewPager.offscreenPageLimit = 1

    tabLayout = view.findViewById(R.id.tab_layout)
    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
      tab.text = when (position) {
        0 -> UsageStatsFragment.NAME
        else -> FocusModeFragment.NAME
      }
    }.attach()
  }

  fun updateTabLayoutShadow(show: Boolean) {
    tabLayout.elevation = if (show) {
      requireContext().resources.getDimension(dimen.margin_small)
    } else {
      0f
    }
  }

  private inner class PagerAdapter(fragment: ViewPagerFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = PAGES_COUNT

    override fun createFragment(position: Int): Fragment {
      return when (position) {
        0 -> UsageStatsFragment.newInstance()
        else -> FocusModeFragment.newInstance()
      }
    }
  }

  companion object {

    fun newInstance() = ViewPagerFragment()

    private const val PAGES_COUNT = 2
  }
}