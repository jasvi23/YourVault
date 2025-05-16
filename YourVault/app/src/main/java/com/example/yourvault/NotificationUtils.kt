package com.example.yourvault

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationUtils {
    const val CHANNEL_ID = "vault_channel"
    const val CHANNEL_NAME = "Vault Notifications"

    fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            ctx.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(chan)
        }
    }

    fun shouldNotify(ctx: Context): Boolean =
        ctx.getSharedPreferences("vault_prefs", 0)
            .getBoolean("notifications_enabled", true)
}
