package com.onishi.androidusbdebugmode

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class UsbDebugTileService : TileService() {
    private val controller: UsbDebugController by lazy {
        UsbDebugController(AndroidUsbDebugSettingsAccess(applicationContext))
    }

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()

        val status = controller.loadStatus()
        if (!status.hasWriteSecureSettingsPermission) {
            refreshTile()
            return
        }

        val nextEnabled = controller.nextEnabledState(status.adbEnabledReadback)
        controller.setUsbDebugging(nextEnabled)
        refreshTile()
    }

    private fun refreshTile() {
        val tile = qsTile ?: return
        val status = controller.loadStatus()

        tile.label = getString(R.string.qs_tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = when {
                !status.hasWriteSecureSettingsPermission -> getString(R.string.qs_tile_permission_missing)
                status.adbEnabledReadback == 1 -> getString(R.string.qs_tile_enabled)
                else -> getString(R.string.qs_tile_disabled)
            }
        }
        tile.state = when {
            !status.hasWriteSecureSettingsPermission -> Tile.STATE_UNAVAILABLE
            status.adbEnabledReadback == 1 -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}
