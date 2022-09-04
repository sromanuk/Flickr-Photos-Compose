package com.example.flickrimages.ui.screens.details

import android.graphics.Bitmap
import com.example.flickrimages.ui.utils.State

data class DetailsImageScreenState(
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    val bitmaps: Bitmap? = null
): State() {
    override fun updateCommonState(loading: Boolean, refreshing: Boolean) =
        copy(isLoading = loading, isRefreshing = refreshing)
}