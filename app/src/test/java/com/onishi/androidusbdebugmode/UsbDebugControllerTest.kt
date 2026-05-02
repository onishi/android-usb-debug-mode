package com.onishi.androidusbdebugmode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UsbDebugControllerTest {
    @Test
    fun `adbEnabledValue returns 1 when enabled is true`() {
        val controller = UsbDebugController(FakeSettingsAccess(permissionGranted = true))

        assertEquals(1, controller.adbEnabledValue(enabled = true))
    }

    @Test
    fun `adbEnabledValue returns 0 when enabled is false`() {
        val controller = UsbDebugController(FakeSettingsAccess(permissionGranted = true))

        assertEquals(0, controller.adbEnabledValue(enabled = false))
    }

    @Test
    fun `setUsbDebugging fails when permission is missing`() {
        val controller = UsbDebugController(
            settingsAccess = FakeSettingsAccess(permissionGranted = false),
        )

        val result = controller.setUsbDebugging(enabled = true)

        assertTrue(result is ToggleResult.Failure)
        assertEquals(
            "WRITE_SECURE_SETTINGS がありません。USB 接続中に adb で権限付与してください。",
            (result as ToggleResult.Failure).message,
        )
    }

    @Test
    fun `setUsbDebugging writes 1 when enabling with permission`() {
        val access = FakeSettingsAccess(permissionGranted = true)
        val controller = UsbDebugController(
            settingsAccess = access,
        )

        val result = controller.setUsbDebugging(enabled = true)

        assertTrue(result is ToggleResult.Success)
        assertEquals(1, access.lastWrittenValue)
    }
}

private class FakeSettingsAccess(
    private val permissionGranted: Boolean,
) : UsbDebugSettingsAccess {
    var lastWrittenValue: Int? = null

    override fun hasWriteSecureSettingsPermission(): Boolean = permissionGranted

    override fun readAdbEnabledOrNull(): Int? = lastWrittenValue

    override fun writeAdbEnabled(value: Int): Boolean {
        lastWrittenValue = value
        return true
    }
}
