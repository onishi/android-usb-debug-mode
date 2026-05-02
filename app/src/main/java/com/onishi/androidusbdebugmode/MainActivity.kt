package com.onishi.androidusbdebugmode

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var controller: UsbDebugController

    private lateinit var permissionValueView: TextView
    private lateinit var readbackValueView: TextView
    private lateinit var noteValueView: TextView
    private lateinit var resultValueView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = UsbDebugController(AndroidUsbDebugSettingsAccess(applicationContext))

        permissionValueView = findViewById(R.id.permissionValue)
        readbackValueView = findViewById(R.id.readbackValue)
        noteValueView = findViewById(R.id.noteValue)
        resultValueView = findViewById(R.id.resultValue)

        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            renderStatus()
        }
        findViewById<Button>(R.id.enableButton).setOnClickListener {
            applyToggle(enabled = true)
        }
        findViewById<Button>(R.id.disableButton).setOnClickListener {
            applyToggle(enabled = false)
        }
        findViewById<Button>(R.id.openDeveloperOptionsButton).setOnClickListener {
            runCatching {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            }.onFailure {
                resultValueView.text = "開発者向けオプションを開けませんでした。"
            }
        }

        renderStatus()
    }

    override fun onResume() {
        super.onResume()
        renderStatus()
    }

    private fun renderStatus() {
        val status = controller.loadStatus()
        permissionValueView.text = if (status.hasWriteSecureSettingsPermission) "あり" else "なし"
        readbackValueView.text = status.adbEnabledReadback?.toString() ?: "不明"
        noteValueView.text = status.note
    }

    private fun applyToggle(enabled: Boolean) {
        val result = controller.setUsbDebugging(enabled)
        resultValueView.text = when (result) {
            is ToggleResult.Success -> result.message
            is ToggleResult.Failure -> result.message
        }
        renderStatus()
    }
}
