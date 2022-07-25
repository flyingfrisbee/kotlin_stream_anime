package com.example.streamanime.core.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import java.util.*

class ExactAlarm(
    private val context: Context
) {
    private lateinit var alarmIntent: PendingIntent
    private var alarmMgr: AlarmManager? = null
    val calendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
//        set(Calendar.HOUR_OF_DAY, 23)
//        set(Calendar.MINUTE, 30)
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 44)
    }

    @SuppressLint("InlinedApi")
    operator fun invoke() {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
            Toast.makeText(context, "Success set alarm", Toast.LENGTH_SHORT).show()
        }
    }
}