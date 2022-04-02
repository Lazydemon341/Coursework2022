package com.example.coursework2022.usage_stats

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R
import com.example.coursework2022.usage_stats.UsageStatsAdapter.ViewHolder
import com.example.coursework2022.utils.formatTime
import com.example.coursework2022.utils.getQuantityString

class UsageStatsAdapter : ListAdapter<AppUsageInfo, ViewHolder>(DiffCallback) {

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app_usage, viewGroup, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemId(position: Int): Long {
    return getItem(position).packageName.hashCode().toLong()
  }

  inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
    private val appName: TextView = v.findViewById(R.id.app_name)
    private val usage: TextView = v.findViewById(R.id.app_usage)
    private val launchCount: TextView = v.findViewById(R.id.launch_count)
    private val appIcon: ImageView = v.findViewById(R.id.app_image)

    @SuppressLint("SetTextI18n")
    fun bind(data: AppUsageInfo) {
      appName.text = data.appLabel
      usage.text = formatTime(data.usageTimeSeconds)
      launchCount.text = v.context.getQuantityString(R.plurals.times_launched, data.launchesCount)
      appIcon.setImageDrawable(data.appIcon)
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<AppUsageInfo>() {
    override fun areItemsTheSame(oldItem: AppUsageInfo, newItem: AppUsageInfo): Boolean =
      oldItem.packageName == newItem.packageName

    override fun areContentsTheSame(oldItem: AppUsageInfo, newItem: AppUsageInfo): Boolean =
      oldItem == newItem
  }
}