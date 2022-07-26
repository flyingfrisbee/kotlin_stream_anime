package com.example.streamanime.core.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class ExactAlarm(
    private val context: Context
) {
    private lateinit var alarmIntent: PendingIntent
    private var alarmMgr: AlarmManager? = null

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

        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getTimeInMillisForAlarm(),
            alarmIntent
        )
    }

    private fun getTimeInMillisForAlarm(): Long {
        val currentTime = System.currentTimeMillis()
        val appointedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
        }.timeInMillis

        if (currentTime < appointedTime) {
            return appointedTime
        }

        val newAppointedTime = GregorianCalendar().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
        }.timeInMillis

        return newAppointedTime
    }
}