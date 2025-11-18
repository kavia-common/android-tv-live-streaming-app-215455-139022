package com.example.androidtv_frontend

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView

/**
 * Main Activity for Android TV
 * Extends FragmentActivity for Leanback compatibility
 *
 * Provides simple entries to navigate to HTML screens rendered via WebView.
 * - Content Info (assets/content-info-1-539.html)
 * - Home Page (assets/home-page-1-2.html)
 */
class MainActivity : FragmentActivity() {

    private lateinit var titleText: TextView
    private lateinit var openContentInfoBtn: Button
    private lateinit var openHomePageBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleText = findViewById(R.id.title_text)
        titleText.text = "androidtv_frontend"

        openContentInfoBtn = findViewById(R.id.btn_open_content_info)
        openHomePageBtn = findViewById(R.id.btn_open_home_page)

        // Ensure both buttons are focusable for TV usage.
        openContentInfoBtn.isFocusable = true
        openContentInfoBtn.isFocusableInTouchMode = true

        openHomePageBtn.isFocusable = true
        openHomePageBtn.isFocusableInTouchMode = true

        // Give initial focus to the first button to maintain previous behavior
        openContentInfoBtn.requestFocus()

        // Content Info navigation
        openContentInfoBtn.setOnClickListener {
            val i = WebContentActivity.createIntent(
                this,
                "content-info-1-539.html"
            )
            startActivity(i)
        }
        openContentInfoBtn.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                (v as? Button)?.performClick()
                true
            } else {
                false
            }
        }

        // PUBLIC_INTERFACE
        /**
         * Navigate to Home Page screen in assets.
         * Triggered by D-pad OK/Enter or click on "Open Home Page".
         */
        openHomePageBtn.setOnClickListener {
            val i = WebContentActivity.createIntent(
                this,
                "home-page-1-2.html"
            )
            startActivity(i)
        }
        openHomePageBtn.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                (v as? Button)?.performClick()
                true
            } else {
                false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Handle TV remote control inputs not consumed by focused views
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER -> {
                // Let focused view handle click; do not consume here.
                false
            }
            KeyEvent.KEYCODE_BACK -> {
                // Handle BACK button
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
