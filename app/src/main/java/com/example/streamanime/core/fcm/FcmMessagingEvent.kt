package com.example.streamanime.core.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.streamanime.R
import com.example.streamanime.core.utils.toTimestamp
import com.example.streamanime.data.local.BookmarkDao
import com.example.streamanime.presentation.main_activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class FcmMessagingEvent : FirebaseMessagingService() {

    @Inject
    lateinit var dao: BookmarkDao
    private val generalNotificationChannel = "CHANNEL_ID"
    private val codesNotificationChannel = "CODES_CHANNEL_ID"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val redemptionCodes = message.data["codes"]
        redemptionCodes?.let {
            createCodesNotification(it)
            return
        }

        message.data.apply {
            val animeID = this["id"]!!
            val title = this["title"]!!
            val body = this["body"]!!
            val latestEpisode = this["latest_episode"]!!
            val updatedAt = this["updated_at"]!!
            runBlocking {
                dao.syncAnimeData(animeID.toInt(), latestEpisode, updatedAt.toTimestamp())
            }
            createNotification(title, body)
        }
    }

    private fun createNotification(title: String, body: String) {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, generalNotificationChannel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "OP channel"
            val descriptionText = "Notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(generalNotificationChannel, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createCodesNotification(codes: String) {
        createCodesNotificationChannel()

        val copyIntent = Intent(this, CopyReceiver::class.java)
        copyIntent.putExtra(TEXT_TO_COPY, codes)
        val copyPendingIntent = PendingIntent.getBroadcast(this, 0, copyIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, codesNotificationChannel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("New code is available to redeem")
            .setContentText(codes)
            .addAction(R.drawable.ic_copy, "Copy", copyPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createCodesNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Redemption codes channel"
            val descriptionText = "Codes notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(codesNotificationChannel, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TEXT_TO_COPY = "TEXT_TO_COPY"
    }
}