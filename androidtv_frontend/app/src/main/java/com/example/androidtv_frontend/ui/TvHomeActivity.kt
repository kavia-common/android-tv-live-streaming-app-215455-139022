package com.example.androidtv_frontend.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * TV-first Home activity that hosts HomeBrowseFragment.
 * Keeps existing MainActivity for WebView demo intact; launcher will remain pointing to MainActivity per manifest,
 * but TvHomeActivity can be used as the TV entry if desired.
 */
class TvHomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, HomeBrowseFragment())
                .commitNow()
        }
    }
}
