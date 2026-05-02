package com.onishi.androidusbdebugmode

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {
    private lateinit var controller: UsbDebugController

    private lateinit var overallStatusValueView: TextView
    private lateinit var permissionValueView: TextView
    private lateinit var readbackValueView: TextView
    private lateinit var resultValueView: TextView
    private lateinit var debugToggle: MaterialSwitch
    private lateinit var resultCardView: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = UsbDebugController(AndroidUsbDebugSettingsAccess(applicationContext))

        overallStatusValueView = findViewById(R.id.overallStatusValue)
        permissionValueView = findViewById(R.id.permissionValue)
        readbackValueView = findViewById(R.id.readbackValue)
        resultValueView = findViewById(R.id.resultValue)
        resultCardView = findViewById(R.id.resultCard)
        debugToggle = findViewById(R.id.debugToggle)

        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            renderStatus()
        }
        debugToggle.setOnCheckedChangeListener { _, isChecked ->
            if (!debugToggle.isPressed) {
                return@setOnCheckedChangeListener
            }
            applyToggle(enabled = isChecked)
        }
        val openDeveloperOptions = {
            runCatching {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            }.onFailure {
                updateResultCard(
                    message = "開発者向けオプションを開けませんでした。",
                    isSuccess = false,
                )
            }
        }
        findViewById<Button>(R.id.openDeveloperOptionsButton).setOnClickListener { openDeveloperOptions() }
        findViewById<Button>(R.id.openDeveloperOptionsButtonSecondary).setOnClickListener { openDeveloperOptions() }

        renderStatus()
    }

    override fun onResume() {
        super.onResume()
        renderStatus()
    }

    private fun renderStatus() {
        val status = controller.loadStatus()
        val hasPermission = status.hasWriteSecureSettingsPermission

        permissionValueView.text = if (hasPermission) {
            getString(R.string.permission_granted)
        } else {
            getString(R.string.permission_missing)
        }
        readbackValueView.text = status.adbEnabledReadback?.toString() ?: getString(R.string.readback_unknown)
        debugToggle.isEnabled = hasPermission
        debugToggle.isChecked = status.adbEnabledReadback == 1

        if (hasPermission) {
            overallStatusValueView.text = getString(R.string.status_ready)
            overallStatusValueView.setBackgroundResource(R.drawable.pill_status_neutral)
            overallStatusValueView.setTextColor(ContextCompat.getColor(this, R.color.ink_900))
        } else {
            overallStatusValueView.text = getString(R.string.status_permission_missing)
            overallStatusValueView.setBackgroundResource(R.drawable.pill_status_neutral)
            overallStatusValueView.setTextColor(ContextCompat.getColor(this, R.color.ink_900))
        }
    }

    private fun applyToggle(enabled: Boolean) {
        val result = controller.setUsbDebugging(enabled)
        when (result) {
            is ToggleResult.Success -> updateResultCard(result.message, isSuccess = true)
            is ToggleResult.Failure -> {
                debugToggle.isChecked = !enabled
                updateResultCard(result.message, isSuccess = false)
            }
        }
        renderStatus()
    }

    private fun updateResultCard(message: String, isSuccess: Boolean) {
        resultValueView.text = message
        resultCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.result_neutral))
        resultValueView.setTextColor(ContextCompat.getColor(this, R.color.ink_900))
    }
}
