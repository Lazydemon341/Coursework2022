package com.example.coursework2022.focus_mode

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework2022.R.id
import com.example.coursework2022.R.layout
import com.example.coursework2022.focus_mode.FocusModeAppsAdapter.ViewHolder

class FocusModeAppsAdapter : ListAdapter<FocusModeAppModel, ViewHolder>(DiffCallback) {

  init {
    setHasStableIds(true)
  }

  private var onAppClickListener: ((FocusModeAppModel) -> Unit)? = null

  fun setOnAppClickListener(listener: (FocusModeAppModel) -> Unit) {
    onAppClickListener = listener
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater.from(viewGroup.context)
        .inflate(layout.item_focus_mode_app, viewGroup, false)
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
    return getItem(position).packageName.hashCode().toLong()
  }

  inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val appName: TextView = view.findViewById(id.app_name)
    private val appIcon: ImageView = view.findViewById(id.app_image)
    private val card: CardView = view.findViewById(id.card)

    @SuppressLint("SetTextI18n")
    fun bind(data: FocusModeAppModel) {
      appName.text = data.appLabel
      appIcon.setImageDrawable(data.appIcon)
      card.setOnClickListener { onAppClickListener?.invoke(data) }
    }

    fun unbind() {
      card.setOnClickListener(null)
    }
  }

  private object DiffCallback : DiffUtil.ItemCallback<FocusModeAppModel>() {
    override fun areItemsTheSame(oldItem: FocusModeAppModel, newItem: FocusModeAppModel): Boolean =
      oldItem.packageName == newItem.packageName

    override fun areContentsTheSame(oldItem: FocusModeAppModel, newItem: FocusModeAppModel): Boolean =
      oldItem == newItem
  }
}