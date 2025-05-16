package com.example.yourvault

import android.app.Application
import com.example.yourvault.util.NotificationUtils

class VaultApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // inicializa canal de notificaciones, tambien cambia el manifest
        NotificationUtils.createChannel(this)

    }
}
