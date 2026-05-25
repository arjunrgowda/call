// ui/MainActivity.kt
package com.autocall.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.autocall.app.data.Prefs
import com.autocall.app.databinding.ActivityMainBinding
import com.autocall.app.service.CallListenerService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load saved settings
        binding.etToken.setText(Prefs.getToken(this))
        binding.etSupabaseUrl.setText(Prefs.getSupabaseUrl(this))
        binding.etSupabaseAnon.setText(Prefs.getSupabaseAnon(this))
        binding.switchEnabled.isChecked = Prefs.isEnabled(this)

        updateStatus()

        binding.switchEnabled.setOnCheckedChangeListener { _, checked ->
            Prefs.setEnabled(this, checked)
        }

        binding.btnSave.setOnClickListener {
            val token = binding.etToken.text.toString().trim()
            val url = binding.etSupabaseUrl.text.toString().trim()
            val anon = binding.etSupabaseAnon.text.toString().trim()

            if (token.isEmpty() || url.isEmpty() || anon.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Prefs.save(this, token, url, anon)
            restartService()
            updateStatus()
            Toast.makeText(this, "✅ Saved! Listener restarted.", Toast.LENGTH_SHORT).show()
        }

        // Start service if configured
        if (Prefs.isConfigured(this)) startService()
    }

    private fun startService() {
        ContextCompat.startForegroundService(
            this, Intent(this, CallListenerService::class.java)
        )
    }

    private fun restartService() {
        stopService(Intent(this, CallListenerService::class.java))
        startService()
    }

    private fun updateStatus() {
        if (Prefs.isConfigured(this)) {
            binding.tvStatus.text = "✅ Active — listening for calls"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvStatus.text = "⚠️ Not configured — fill in settings below"
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark))
        }
    }
}
