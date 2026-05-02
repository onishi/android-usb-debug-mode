package com.onishi.androidusbdebugmode

data class UsbDebugStatus(
    val hasWriteSecureSettingsPermission: Boolean,
    val adbEnabledReadback: Int?,
    val note: String,
)

sealed interface ToggleResult {
    data class Success(val message: String) : ToggleResult

    data class Failure(val message: String, val cause: Throwable? = null) : ToggleResult
}
