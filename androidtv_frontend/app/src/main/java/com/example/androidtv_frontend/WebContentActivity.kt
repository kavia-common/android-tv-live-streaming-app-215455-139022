package com.example.androidtv_frontend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity

/**
 * PUBLIC_INTERFACE
 * WebContentActivity is a simple screen that renders HTML content from the app's assets folder
 * inside a WebView. It is intended to host generated design screens (HTML/CSS/JS) such as
 * Content Info or Home Page.
 *
 * Parameters (via Intent extras):
 * - EXTRA_ASSET_PATH (String): Relative path under assets/ to the HTML file to load,
 *   e.g. "content-info-1-539.html".
 *
 * Behavior:
 * - Loads "file:///android_asset/<EXTRA_ASSET_PATH>" so that references like "./common.css"
 *   and "./content-info-1-539.js" resolve against assets/.
 * - Sets focus to the first focusable element within the HTML by injecting a small script.
 * - Supports Android TV D-pad navigation; BACK key closes the activity.
 */
class WebContentActivity : FragmentActivity() {

    companion object {
        private const val EXTRA_ASSET_PATH = "extra_asset_path"

        // PUBLIC_INTERFACE
        /**
         * Create an Intent to launch WebContentActivity for a given assets HTML file.
         * @param context Context used to create the Intent.
         * @param assetHtmlPath Relative path under assets/ to the HTML file (e.g., "content-info-1-539.html").
         */
        fun createIntent(context: Context, assetHtmlPath: String): Intent {
            val i = Intent(context, WebContentActivity::class.java)
            i.putExtra(EXTRA_ASSET_PATH, assetHtmlPath)
            return i
        }
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Build a simple layout programmatically to keep footprint minimal.
        webView = WebView(this).apply {
            id = View.generateViewId()
            isFocusable = true
            isFocusableInTouchMode = true

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.allowFileAccess = true
            settings.allowContentAccess = true

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // Attempt to move focus to the first actionable element in the document.
                    // This will help with TV D-pad navigation.
                    val focusScript = """
                        (function() {
                            try {
                                var candidates = document.querySelectorAll('button, [role="button"], a[href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
                                if (candidates && candidates.length > 0) {
                                    var first = candidates[0];
                                    if (first) { first.focus(); }
                                } else {
                                    // Fallback: focus body
                                    document.body && document.body.focus && document.body.focus();
                                }
                            } catch (e) {}
                        })();
                    """.trimIndent()
                    view?.evaluateJavascript(focusScript, null)
                }
            }
        }

        setContentView(webView)

        val relativeAssetPath = intent.getStringExtra(EXTRA_ASSET_PATH)
            ?: "content-info-1-539.html" // default to Content Info if not provided

        // Load from assets keeping paths relative to assets/
        val url = "file:///android_asset/$relativeAssetPath"
        webView.loadUrl(url)
        webView.requestFocus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                finish()
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                // Let the WebView/HTML handle click if a focused element is present.
                false
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
