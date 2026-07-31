package com.dima.drunvpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.io.IOException

class MyVpnService : VpnService() {

    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopVpn()
            return START_NOT_STICKY
        }
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        try {
            parcelFileDescriptor = Builder()
                .setSession("DRUN VPN")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1")
                .establish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopVpn() {
        try {
            parcelFileDescriptor?.close()
            parcelFileDescriptor = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
    }
}
