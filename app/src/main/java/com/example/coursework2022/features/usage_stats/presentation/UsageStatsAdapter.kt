package com.example.coursework2022.features.usage_stats.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coursework2022.R
import com.example.coursework2022.features.usage_stats.UsageStatsModel
import com.example.coursework2022.features.usage_stats.presentation.UsageStatsAdapter.ViewHolder
import com.example.coursework2022.utils.formatTime
import com.example.coursework2022.utils.getQuantityString

class UsageStatsAdapter : ListAdapter<UsageStatsModel, ViewHolder>(DiffCallback) {

  init {
    setHasStableIds(true)
  }

  private var onAppClickListener: ((UsageStatsModel) -> Unit)? = null

  fun setOnAppClickListener(listener: (UsageStatsModel) -> Unit) {
    onAppClickListener = listener
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app_usage, viewGroup, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.unbind()
  }

  override fun getItemId(position: Int): Long {
    return getItem(position).packageName.hashCode().toLong()
  }

  inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val appName: TextView = view.findViewById(R.id.app_name)
    private val usage: TextView = view.findViewById(R.id.app_usage)
    private val launchCount: TextView = view.findViewById(R.id.launch_count)
    private val appIcon: ImageView = view.findViewById(R.id.app_image)

    @SuppressLint("SetTextI18n")
    fun bind(data: UsageStatsModel) {
      appName.text = data.appLabel
      usage.text = formatTime(data.usageTimeSeconds)
      launchCount.text = view.context.getQuantityString(R.plurals.times_launched, data.launchesCount)
      Glide.with(view)
        .load(data.appIcon)
        .into(appIcon)
      view.setOnClickListener {
        onAppClickListener?.invoke(data)
      }
    }

    fun unbind() {
      view.setOnClickListener(null)
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<UsageStatsModel>() {
    override fun areItemsTheSame(oldItem: UsageStatsModel, newItem: UsageStatsModel): Boolean =
      oldItem.packageName == newItem.packageName

    override fun areContentsTheSame(oldItem: UsageStatsModel, newItem: UsageStatsModel): Boolean =
      oldItem == newItem
  }
}