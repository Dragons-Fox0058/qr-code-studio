package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ID = "qr_app_updates_channel"
    const val NOTIFICATION_ID = 1001
    const val GITHUB_REPO_URL = "https://github.com/Dragons-Fox0058/qr-code-studio"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Uygulama Güncellemeleri"
            val descriptionText = "Yeni sürüm ve özellik bildirimleri"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendUpdateNotification(
        context: Context,
        title: String = "QR Code Studio Güncellemesi",
        message: String = "v1.1.0 sürümü başarıyla yüklendi! Yeni özellikleri keşfedin."
    ) {
        createNotificationChannel(context)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPO_URL)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            // Permission might be denied on Android 13+
        }
    }
}
