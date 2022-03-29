package com.example.coursework2022

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.button.MaterialButton

class FocusModeButton @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  defStyleAttr: Int = 0
) : MaterialButton(context, attributeSet, defStyleAttr) {

  private var onTouchDisabledListener: () -> Unit = {}

  fun setOnTouchDisabledListener(listener: () -> Unit) {
    onTouchDisabledListener = listener
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (!isEnabled && event?.action == MotionEvent.ACTION_DOWN) {
      onTouchDisabledListener.invoke()
    }
    return super.onTouchEvent(event)
  }
}