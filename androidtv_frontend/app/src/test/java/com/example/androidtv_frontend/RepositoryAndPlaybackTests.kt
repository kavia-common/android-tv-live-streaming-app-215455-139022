package com.example.androidtv_frontend

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.androidtv_frontend.data.MockContentRepository
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Minimal unit tests validating repository provides rows and that playback intent builds.
 */
class RepositoryAndPlaybackTests {

    @Test
    fun repository_provides_categories_with_items() {
        val repo = MockContentRepository()
        val cats = repo.getHomeCategories()
        assertTrue(cats.isNotEmpty())
        assertTrue(cats.any { it.items.isNotEmpty() })
    }

    @Test
    fun playback_intent_builds() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val intent = com.example.androidtv_frontend.ui.PlaybackActivity.createIntent(
            ctx, "id", "Title", "https://example.com/stream.m3u8", true
        )
        assertTrue(intent.hasExtra("extra_media_url"))
    }
}
