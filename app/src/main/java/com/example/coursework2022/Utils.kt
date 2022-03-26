package com.example.coursework2022

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment

fun Context.drawable(@DrawableRes res: Int): Drawable? {
  return AppCompatResources.getDrawable(this, res)
}

fun Fragment.drawable(@DrawableRes res: Int): Drawable? {
  return AppCompatResources.getDrawable(requireContext(), res)
}