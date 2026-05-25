// service/BootReceiver.kt
package com.autocall.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.autocall.app.data.Prefs

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && Prefs.isConfigured(context)) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, CallListenerService::class.java)
            )
        }
    }
}
