package com.example.coursework2022.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.provider.Settings.Secure
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bot.box.appusage.utils.UsageUtils
import com.example.coursework2022.R

fun Context.drawable(@DrawableRes res: Int): Drawable? {
  return AppCompatResources.getDrawable(this, res)
}

fun Fragment.drawable(@DrawableRes res: Int): Drawable? =
  requireContext().drawable(res)

fun appLabel(context: Context, packageName: String): String =
  UsageUtils.parsePackageName(context.packageManager, packageName)

fun appIcon(packageName: String): Drawable? =
  UsageUtils.parsePackageIcon(packageName, R.drawable.ic_launcher)

fun Context.getAppsList(): List<ResolveInfo> {
  val main = Intent(Intent.ACTION_MAIN, null)
  main.addCategory(Intent.CATEGORY_LAUNCHER);
  return packageManager.queryIntentActivities(main, 0)
    .distinctBy {
      it.activityInfo.packageName
    }
}

fun Context.isPermissionGranted(permission: String): Boolean =
  ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED

fun Context.isAccessibilitySettingsOn(): Boolean {
  try {
    val accessibilityEnabled = Secure.getInt(
      contentResolver,
      Secure.ACCESSIBILITY_ENABLED
    )
    if (accessibilityEnabled == 1) {
      val services = Secure.getString(
        contentResolver,
        Secure.ENABLED_ACCESSIBILITY_SERVICES
      )
      if (services != null) {
        return services.contains(packageName, ignoreCase = true)
      }
    }
  } catch (e: Throwable) {
  }
  return false
}

fun Context.getQuantityString(@PluralsRes resId: Int, value: Int): String {
  return String.format(resources.getQuantityText(resId, value).toString(), value)
}