package com.example.coursework2022

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.coursework2022.usage_stats.UsageStatsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val PAGES_COUNT = 2

class ViewPagerFragment : Fragment(R.layout.fragment_pager) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val viewPager = view.findViewById<ViewPager2>(R.id.pager)
    viewPager.adapter = PagerAdapter(this)
    val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
      tab.text = when (position) {
        0 -> UsageStatsFragment.NAME
        else -> FocusModeFragment.NAME
      }
    }.attach()
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
  }
}