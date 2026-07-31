package com.dima.drunvpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidVPN")
            loadUrl("file:///android_asset/index.html")
        }

        setContentView(webView)
    }

    inner class WebAppInterface(private val activity: MainActivity) {
        @JavascriptInterface
        fun startVpn() {
            activity.runOnUiThread {
                val intent = VpnService.prepare(activity)
                if (intent != null) {
                    vpnPermissionLauncher.launch(intent)
                } else {
                    startVpnService()
                }
            }
        }

        @JavascriptInterface
        fun stopVpn() {
            activity.runOnUiThread {
                val intent = Intent(activity, MyVpnService::class.java).apply {
                    action = "STOP"
                }
                activity.startService(intent)
            }
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, MyVpnService::class.java)
        startService(intent)
    }
}
