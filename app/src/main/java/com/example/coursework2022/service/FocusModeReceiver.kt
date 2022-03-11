package com.example.coursework2022.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.coursework2022.App

class FocusModeReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (App.focusModeOn) {
      val i = Intent(context, FocusModeService::class.java)
      context.startService(i)
    }
  }
}