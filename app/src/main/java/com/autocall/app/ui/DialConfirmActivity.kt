// ui/DialConfirmActivity.kt
package com.autocall.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.autocall.app.databinding.ActivityDialConfirmBinding

class DialConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialConfirmBinding
    private var number: String = ""
    private var countdown: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )

        binding = ActivityDialConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        number = intent.getStringExtra("number") ?: ""
        binding.tvNumber.text = number
        binding.tvFrom.text = "Call requested from Salesforce"

        // Auto-dismiss after 30 seconds if no action
        startCountdown()

        binding.btnCall.setOnClickListener {
            countdown?.cancel()
            dialNumber()
        }

        binding.btnCancel.setOnClickListener {
            countdown?.cancel()
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        countdown?.cancel()
        number = intent?.getStringExtra("number") ?: number
        binding.tvNumber.text = number
        startCountdown()
    }

    private fun startCountdown() {
        countdown = object : CountDownTimer(30_000, 1000) {
            override fun onTick(ms: Long) {
                binding.btnCancel.text = "Dismiss (${ms / 1000}s)"
            }
            override fun onFinish() { finish() }
        }.start()
    }

    private fun dialNumber() {
        if (number.isEmpty()) { finish(); return }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            // ACTION_CALL — opens dialer pre-filled, user taps green button
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
        } else {
            // Request permission then retry
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
        finish()
    }

    override fun onRequestPermissionsResult(reqCode: Int, perms: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(reqCode, perms, results)
        if (reqCode == 101 && results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED) {
            dialNumber()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdown?.cancel()
    }
}
