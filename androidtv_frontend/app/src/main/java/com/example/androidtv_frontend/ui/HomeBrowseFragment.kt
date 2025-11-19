package com.example.androidtv_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.example.androidtv_frontend.data.MockContentRepository
import com.example.androidtv_frontend.domain.MediaCategory
import com.example.androidtv_frontend.domain.MediaItem
import com.example.androidtv_frontend.presentation.CardPresenter

/**
 * Home screen using Leanback's BrowseSupportFragment with rows.
 * Displays Featured, Movies, and Live TV placeholders.
 */
class HomeBrowseFragment : BrowseSupportFragment() {

    private val repository = MockContentRepository()
    private val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUi()
        buildRows(repository.getHomeCategories())
        setupListeners()
    }

    private fun setupUi() {
        title = "Ocean TV"
        brandColor = 0xFF2563EB.toInt()
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
    }

    private fun buildRows(categories: List<MediaCategory>) {
        rowsAdapter.clear()
        val cardPresenter = CardPresenter()

        categories.forEach { cat ->
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            cat.items.forEach { item -> listRowAdapter.add(item) }
            val header = HeaderItem(cat.id.hashCode().toLong(), cat.title)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        adapter = rowsAdapter
    }

    private fun setupListeners() {
        setOnItemViewClickedListener { _, item, _, _ ->
            (item as? MediaItem)?.let { media ->
                val ctx = (activity as? FragmentActivity) ?: return@let
                val i = Intent(ctx, PlaybackActivity::class.java).apply {
                    putExtras(
                        bundleOf(
                            PlaybackActivity.EXTRA_MEDIA_ID to media.id,
                            PlaybackActivity.EXTRA_MEDIA_TITLE to media.title,
                            PlaybackActivity.EXTRA_MEDIA_URL to media.mediaUrl,
                            PlaybackActivity.EXTRA_MEDIA_IS_LIVE to media.isLive
                        )
                    )
                }
                ctx.startActivity(i)
            }
        }

        setOnItemViewSelectedListener { _, item, _, _ ->
            // Could update background with item.backgroundImageUrl
            if (item is MediaItem) {
                Log.d("HomeBrowseFragment", "Selected: ${item.title}")
            }
        }
    }
}
