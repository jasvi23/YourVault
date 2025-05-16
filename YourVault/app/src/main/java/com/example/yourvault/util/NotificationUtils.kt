package com.example.yourvault.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.yourvault.R

object NotificationUtils {
    const val CHANNEL_ID = "vault_channel"
    const val CHANNEL_NAME = "Vault Notifications"

    /** Crea el canal (llámalo una vez en Application.onCreate). */
    fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(chan)
        }
    }

    /** Comprueba el flag de SharedPreferences. */
    fun shouldNotify(ctx: Context): Boolean =
        ctx.getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)
            .getBoolean("notifications_enabled", true)

    /** Muestra la notificación con título y texto. */
    fun showNotification(ctx: Context, title: String, text: String) {
        val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)  // asegúrate de tener este drawable
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(System.currentTimeMillis().toInt(), notif)
    }
}
