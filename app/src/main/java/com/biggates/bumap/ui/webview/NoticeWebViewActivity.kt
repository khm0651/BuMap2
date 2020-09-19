package com.biggates.bumap.ui.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.activity_notice_web_view.*

class NoticeWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_web_view)

        var href = intent.getStringExtra("href")!!

        var webView = notice_web_view
        webView.webViewClient = WebViewClient()

        var webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.loadUrl(href)

    }
}
