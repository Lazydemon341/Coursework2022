package com.example.coursework2022

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import bot.box.appusage.model.AppData
import bot.box.appusage.utils.UsageUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.coursework2022.R.id
import com.example.coursework2022.R.layout
import com.example.coursework2022.R.mipmap
import com.example.coursework2022.R.plurals
import com.example.coursework2022.UsageStatsAdapter.ViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsageStatsAdapter() : Adapter<ViewHolder>() {
  private var dataList: List<AppData> = emptyList()

  fun updateData(data: List<AppData>) {
    dataList = data
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
    val itemView: View = LayoutInflater.from(viewGroup.context).inflate(layout.item_app_usage, viewGroup, false)
    return ViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(dataList[position])
  }

  override fun getItemCount(): Int {
    return dataList.size
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(id.app_name)
    private val usage: TextView = itemView.findViewById(id.app_usage)
    private val time: TextView = itemView.findViewById(id.app_time)
    private val icon: ImageView = itemView.findViewById(id.app_image)
    private val launchCount: TextView = itemView.findViewById(id.launch_count)
    private val dataUsed: TextView = itemView.findViewById(id.data_used)

    fun bind(data: AppData) {
      name.text = data.mName
      usage.text = UsageUtils.humanReadableMillis(data.mUsageTime)
      time.text = String.format(
        Locale.getDefault(),
        "%s", "Last Launch " +
            SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(Date(data.mEventTime))
      )

      val text = "${data.mCount} ${itemView.resources.getQuantityString(plurals.times_launched, data.mCount)}"
      launchCount.text = text

      dataUsed.text = UsageUtils.humanReadableByteCount(data.mWifi + data.mMobile)

      Glide.with(itemView.context)
        .load(UsageUtils.parsePackageIcon(data.mPackageName, mipmap.ic_launcher))
        .transition(DrawableTransitionOptions().crossFade())
        .into(icon)

      itemView.setOnClickListener { v: View ->
//        DetailActivity.start( TODO
//          v.context as Activity,
//          item.mPackageName
//        )
      }
    }

  }
}
