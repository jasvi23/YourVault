package com.example.yourvault

import android.app.Application

class VaultApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // inicializa canal de notificaciones, tambien cambia el manifest
        NotificationUtils.createChannel(this)
    }
}
