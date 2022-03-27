package com.example.coursework2022.usage_stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R
import com.example.coursework2022.usage_stats.AppUsageStatsAdapter.ViewHolder
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class AppUsageStatsAdapter : RecyclerView.Adapter<ViewHolder>() {
  private var items: List<AppUsageStatsModel> = emptyList()

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app_usage, viewGroup, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun updateItems(newItems: List<AppUsageStatsModel>) {
    val diffResult = DiffUtil.calculateDiff(DiffCallback(items, newItems))
    items = newItems
    diffResult.dispatchUpdatesTo(this)
  }

  inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val appName: TextView = v.findViewById(R.id.app_name)
    private val usage: TextView = v.findViewById(R.id.app_usage)
    private val lastTimeUsed: TextView = v.findViewById(R.id.app_time)
    private val appIcon: ImageView = v.findViewById(R.id.app_image)

    fun bind(data: AppUsageStatsModel) {
      appName.text = data.appLabel
      usage.text = getUsageText(data.usageStats.totalTimeInForeground)
      lastTimeUsed.text = DateFormat.getDateTimeInstance().format((Date(data.usageStats.lastTimeUsed)))
      appIcon.setImageDrawable(data.appIcon)
    }

    private fun getUsageText(usageTimeMillis: Long): String {
      val seconds = TimeUnit.MILLISECONDS.toSeconds(usageTimeMillis)
      val minutes = seconds / 60

      return when {
        minutes > 0L -> {
          "$minutes min"
        }
        minutes == 0L && seconds == 0L -> {
          "0 min"
        }
        else -> {
          "less than 1 min"
        }
      }
    }
  }

  class DiffCallback(
    private val old: List<AppUsageStatsModel>,
    private val new: List<AppUsageStatsModel>
  ) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
      old[oldItemPosition].usageStats.packageName == new[newItemPosition].usageStats.packageName

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
      old[oldItemPosition] == new[newItemPosition]
  }
}