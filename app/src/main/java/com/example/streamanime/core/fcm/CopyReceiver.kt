package com.example.streamanime.core.fcm

import android.app.NotificationManager
import android.content.*
import android.widget.Toast

class CopyReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val textToCopy = intent.getStringExtra(FcmMessagingEvent.TEXT_TO_COPY)

        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("freemogems", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(ctx, "Codes successfully copied to clipboard", Toast.LENGTH_SHORT).show()

        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}