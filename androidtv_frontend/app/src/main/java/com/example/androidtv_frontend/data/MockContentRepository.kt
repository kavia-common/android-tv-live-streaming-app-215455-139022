package com.example.androidtv_frontend.data

import com.example.androidtv_frontend.domain.MediaCategory
import com.example.androidtv_frontend.domain.MediaItem
import com.example.androidtv_frontend.domain.MediaType

/**
 * Simple in-memory repository providing mock content.
 * No secrets or network calls. Structure allows swapping with real data source later.
 */
class MockContentRepository(
    private val imageBaseUrl: String = "https://picsum.photos"
) {

    // PUBLIC_INTERFACE
    /**
     * Returns featured, movies, and live placeholders as categories (rows).
     */
    fun getHomeCategories(): List<MediaCategory> {
        val featured = MediaCategory(
            id = "featured",
            title = "Featured",
            items = listOf(
                sampleVod("1", "Big Buck Bunny", "https://storage.googleapis.com/shaka-demo-assets/bbb-dark-truths-hls/hls.m3u8"),
                sampleVod("2", "Tears of Steel", "https://storage.googleapis.com/shaka-demo-assets/tearsofsteel/tearsofsteel.mpd"),
                sampleVod("3", "Sintel", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4")
            )
        )

        val movies = MediaCategory(
            id = "movies",
            title = "Movies",
            items = (4..12).map { idx ->
                sampleVod(idx.toString(), "Sample Movie $idx", "https://storage.googleapis.com/shaka-demo-assets/angel-one-hls/hls.m3u8")
            }
        )

        val live = MediaCategory(
            id = "livetv",
            title = "Live TV",
            items = listOf(
                sampleLive("live-1", "Live News 24/7", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
                sampleLive("live-2", "Live Sports", "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
            )
        )

        return listOf(featured, movies, live)
    }

    private fun sampleVod(id: String, title: String, url: String): MediaItem {
        return MediaItem(
            id = id,
            title = title,
            description = "Sample VOD stream",
            cardImageUrl = "$imageBaseUrl/seed/$id/400/600",
            backgroundImageUrl = "$imageBaseUrl/seed/bg$id/1280/720",
            mediaUrl = url,
            mimeType = null,
            isLive = false,
            type = MediaType.MOVIE
        )
    }

    private fun sampleLive(id: String, title: String, url: String): MediaItem {
        return MediaItem(
            id = id,
            title = title,
            description = "Sample Live HLS stream",
            cardImageUrl = "$imageBaseUrl/seed/$id/640/360",
            backgroundImageUrl = "$imageBaseUrl/seed/bg$id/1280/720",
            mediaUrl = url,
            mimeType = "application/x-mpegURL",
            isLive = true,
            type = MediaType.LIVE
        )
    }
}
