package com.example.coursework2022

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import bot.box.appusage.utils.UsageUtils
import com.example.coursework2022.R.mipmap


fun Context.drawable(@DrawableRes res: Int): Drawable? {
  return AppCompatResources.getDrawable(this, res)
}

fun Fragment.drawable(@DrawableRes res: Int): Drawable? {
  return AppCompatResources.getDrawable(requireContext(), res)
}

fun Context.appLabel(packageName: String): String {
  try {
    val appInfo = packageManager.getApplicationInfo(packageName, 0)
    return packageManager.getApplicationLabel(appInfo).toString()
  } catch (e: Throwable) {
  }
  return packageName
}

fun Fragment.appLabel(packageName: String): String =
  requireContext().appLabel(packageName)

fun appLabel(packageName: String): Drawable? =
  UsageUtils.parsePackageIcon(packageName, mipmap.ic_launcher)

fun Context.getAppsList(): List<ResolveInfo> {
  val main = Intent(Intent.ACTION_MAIN, null)
  main.addCategory(Intent.CATEGORY_LAUNCHER);
  return packageManager.queryIntentActivities(main, 0)
    .filter {
      it.activityInfo.packageName != this.packageName
    }
    .distinctBy {
      it.activityInfo.packageName
    }
}