package com.example.androidtv_frontend.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.androidtv_frontend.R
import com.example.androidtv_frontend.domain.MediaItem

/**
 * Simple card presenter showing poster and title via contentDescription.
 * Colors follow Ocean Professional accents.
 */
class CardPresenter : Presenter() {

    private val defaultCardWidth = 300
    private val defaultCardHeight = 450
    private val focusScale = 1.05f

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val imageView = object : AppCompatImageView(parent.context) {
            override fun setSelected(selected: Boolean) {
                super.setSelected(selected)
                animate().scaleX(if (selected) focusScale else 1f)
                    .scaleY(if (selected) focusScale else 1f)
                    .setDuration(120)
                    .start()
                // Focus glow using color filter
                colorFilter = if (selected) {
                    android.graphics.PorterDuffColorFilter(
                        Color.parseColor("#2563EB"), android.graphics.PorterDuff.Mode.SRC_ATOP
                    )
                } else null
                background = if (selected) {
                    ColorDrawable(Color.parseColor("#40F59E0B")) // subtle amber overlay
                } else null
            }
        }.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = ViewGroup.LayoutParams(defaultCardWidth, defaultCardHeight)
            contentDescription = ""
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val media = item as MediaItem
        val view = viewHolder.view as AppCompatImageView
        view.contentDescription = media.title
        Glide.with(view.context)
            .load(media.cardImageUrl)
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher_fallback)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val view = viewHolder.view as AppCompatImageView
        Glide.with(view).clear(view)
        view.colorFilter = null
        view.background = null
    }
}
