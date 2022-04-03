package com.example.coursework2022.features.schedules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R
import com.example.coursework2022.features.schedules.SchedulesAdapter.ViewHolder
import com.example.coursework2022.utils.getShortWeekdayName
import com.google.android.material.switchmaterial.SwitchMaterial

class SchedulesAdapter : ListAdapter<ScheduleModel, ViewHolder>(DiffCallback) {

  private var onSwitchClick: ((ScheduleModel, Boolean) -> Unit)? = null

  init {
    setHasStableIds(true)
  }

  fun setOnSwitchClickListener(listener: (ScheduleModel, Boolean) -> Unit) {
    onSwitchClick = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.unbind()
  }

  override fun getItemId(position: Int): Long {
    return getItem(position).uuid.hashCode().toLong()
  }

  inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val days: TextView = view.findViewById(R.id.schedule_days)
    private val time: TextView = view.findViewById(R.id.schedule_time)
    private val switch: SwitchMaterial = view.findViewById(R.id.schedule_switch)

    fun bind(data: ScheduleModel) {
      days.text = data.daysOfWeek.joinToString(separator = ",") { getShortWeekdayName(it.value) }
      switch.isChecked = data.active
      switch.setOnCheckedChangeListener { _, isChecked ->
        onSwitchClick?.invoke(data, isChecked)
      }
    }

    fun unbind() {
      switch.setOnClickListener(null)
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<ScheduleModel>() {
    override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
      oldItem.uuid == newItem.uuid

    override fun areContentsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean =
      oldItem == newItem
  }
}