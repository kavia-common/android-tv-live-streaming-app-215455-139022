package com.example.androidtv_frontend

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Main Activity for Android TV
 * Extends FragmentActivity for Leanback compatibility
 *
 * Provides a simple entry to navigate to the Content Info screen rendered via WebView.
 */
class MainActivity : FragmentActivity() {

    private lateinit var titleText: TextView
    private lateinit var openContentInfoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        titleText = findViewById(R.id.title_text)
        titleText.text = "androidtv_frontend"

        openContentInfoBtn = findViewById(R.id.btn_open_content_info)

        // Make sure the button gets initial focus for TV usage.
        openContentInfoBtn.isFocusable = true
        openContentInfoBtn.isFocusableInTouchMode = true
        openContentInfoBtn.requestFocus()

        openContentInfoBtn.setOnClickListener {
            // Launch the WebContentActivity to render the Content Info screen from assets/
            val i = WebContentActivity.createIntent(
                this,
                "content-info-1-539.html"
            )
            startActivity(i)
        }

        // Also handle DPAD_CENTER/ENTER when button is focused
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
