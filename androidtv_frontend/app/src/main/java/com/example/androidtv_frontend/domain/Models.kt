package com.example.androidtv_frontend.domain

/**
 * Domain layer models for TV media catalogs.
 * Keep models small and immutable; suitable for repository and UI mapping.
 */

// PUBLIC_INTERFACE
/**
 * MediaType enumerates types of playable media.
 */
enum class MediaType {
    MOVIE, SERIES, LIVE
}

// PUBLIC_INTERFACE
/**
 * MediaItem represents a content entity rendered in Leanback rows.
 * @property id stable unique id
 * @property title human-readable title
 * @property description optional description/synopsis
 * @property cardImageUrl poster/card image URL (can be https or app resource)
 * @property backgroundImageUrl background hero image URL
 * @property mediaUrl playable URL (HLS/DASH/MP4 etc.)
 * @property mimeType optional mime type hint
 * @property isLive whether this item is a live stream
 * @property type media classification
 */
data class MediaItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val cardImageUrl: String? = null,
    val backgroundImageUrl: String? = null,
    val mediaUrl: String? = null,
    val mimeType: String? = null,
    val isLive: Boolean = false,
    val type: MediaType = MediaType.MOVIE
)

// PUBLIC_INTERFACE
/**
 * MediaCategory represents a row on Browse screen.
 * @property id stable unique id
 * @property title header title
 * @property items ordered list of MediaItem in the row
 */
data class MediaCategory(
    val id: String,
    val title: String,
    val items: List<MediaItem>
)
