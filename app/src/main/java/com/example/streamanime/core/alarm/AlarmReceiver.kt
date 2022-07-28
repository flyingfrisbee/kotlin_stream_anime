package com.example.streamanime.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.streamanime.core.service.PingServerService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, PingServerService::class.java))
    }
}