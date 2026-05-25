// service/CallListenerService.kt
package com.autocall.app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autocall.app.R
import com.autocall.app.data.Prefs
import com.autocall.app.data.SupabaseManager
import com.autocall.app.ui.DialConfirmActivity
import kotlinx.coroutines.*

class CallListenerService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val TAG = "AutoCall"
    private val CHANNEL_ID = "autocall_channel"
    private val NOTIF_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification("Listening for calls…"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Prefs.isConfigured(this)) {
            updateNotification("Not configured — open AutoCall app")
            return START_STICKY
        }

        val token = Prefs.getToken(this)
        val url = Prefs.getSupabaseUrl(this)
        val anon = Prefs.getSupabaseAnon(this)

        if (!SupabaseManager.isInitialized()) {
            SupabaseManager.init(url, anon)
        }

        scope.launch {
            try {
                updateNotification("Connected — waiting for calls…")
                val flow = SupabaseManager.listenForCalls(token)
                flow.collect { number ->
                    if (number.isNotEmpty() && Prefs.isEnabled(this@CallListenerService)) {
                        Log.d(TAG, "Incoming call trigger: $number")
                        showDialConfirm(number)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Listener error: ${e.message}")
                updateNotification("Connection error — retrying…")
                delay(5000)
                startSelf()
            }
        }

        return START_STICKY
    }

    private fun showDialConfirm(number: String) {
        val intent = Intent(this, DialConfirmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("number", number)
        }
        startActivity(intent)
    }

    private fun startSelf() {
        startService(Intent(this, CallListenerService::class.java))
    }

    private fun buildNotification(text: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AutoCall")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_phone)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "AutoCall Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Keeps AutoCall running in background" }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
