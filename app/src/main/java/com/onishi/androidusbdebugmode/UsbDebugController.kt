package com.onishi.androidusbdebugmode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

class UsbDebugController(
    private val settingsAccess: UsbDebugSettingsAccess,
) {
    fun loadStatus(): UsbDebugStatus {
        val hasPermission = settingsAccess.hasWriteSecureSettingsPermission()
        val adbReadback = if (hasPermission) settingsAccess.readAdbEnabledOrNull() else null

        return UsbDebugStatus(
            hasWriteSecureSettingsPermission = hasPermission,
            adbEnabledReadback = adbReadback,
            note = buildStatusNote(hasPermission, adbReadback),
        )
    }

    fun setUsbDebugging(enabled: Boolean): ToggleResult {
        if (!hasWriteSecureSettingsPermission()) {
            return ToggleResult.Failure(
                message = "WRITE_SECURE_SETTINGS がありません。USB 接続中に adb で権限付与してください。",
            )
        }

        return runCatching {
            val targetValue = adbEnabledValue(enabled)
            val success = settingsAccess.writeAdbEnabled(targetValue)

            if (success) {
                ToggleResult.Success(
                    message = if (enabled) {
                        "USB デバッグを ON に切り替えました。"
                    } else {
                        "USB デバッグを OFF に切り替えました。現在の adb 接続は切断される可能性があります。"
                    },
                )
            } else {
                ToggleResult.Failure(
                    message = "Settings.Global.ADB_ENABLED の更新に失敗しました。",
                )
            }
        }.getOrElse { error ->
            ToggleResult.Failure(
                message = "USB デバッグ切替に失敗しました: ${error.message ?: error::class.java.simpleName}",
                cause = error,
            )
        }
    }

    fun adbEnabledValue(enabled: Boolean): Int = if (enabled) 1 else 0

    fun hasWriteSecureSettingsPermission(): Boolean {
        return settingsAccess.hasWriteSecureSettingsPermission()
    }

    private fun buildStatusNote(hasPermission: Boolean, adbReadback: Int?): String {
        if (!hasPermission) {
            return "権限未付与です。adb で WRITE_SECURE_SETTINGS を付与してください。"
        }

        return when (adbReadback) {
            1 -> "ADB_ENABLED の読み取り値は 1 です。"
            0 -> "ADB_ENABLED の読み取り値は 0 です。端末によっては常に 0 を返します。"
            else -> "ADB_ENABLED の読み取りに失敗しました。端末実装により取得できない場合があります。"
        }
    }
}

interface UsbDebugSettingsAccess {
    fun hasWriteSecureSettingsPermission(): Boolean

    fun readAdbEnabledOrNull(): Int?

    fun writeAdbEnabled(value: Int): Boolean
}

class AndroidUsbDebugSettingsAccess(
    private val context: Context,
) : UsbDebugSettingsAccess {
    override fun hasWriteSecureSettingsPermission(): Boolean {
        return context.checkCallingOrSelfPermission(
            Manifest.permission.WRITE_SECURE_SETTINGS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun readAdbEnabledOrNull(): Int? {
        return runCatching {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
            )
        }.getOrNull()
    }

    override fun writeAdbEnabled(value: Int): Boolean {
        return Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED,
            value,
        )
    }
}
