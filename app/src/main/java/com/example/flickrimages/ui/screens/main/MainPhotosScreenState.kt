package com.example.flickrimages.ui.screens.main

import android.graphics.Bitmap
import com.example.flickrimages.model.FlickrPhoto
import com.example.flickrimages.ui.utils.State

data class MainPhotosScreenState(
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    val tagsSearched: String = "",
    val photos: List<FlickrPhoto> = emptyList(),
    val viewSelector: ViewSelection = ViewSelection.List,
    val bitmapsMap: HashMap<Int, Bitmap?> = HashMap(),
    val dummyUpdateCounter: Int = 0
): State() {
    override fun updateCommonState(loading: Boolean, refreshing: Boolean) =
        copy(isLoading = loading, isRefreshing = refreshing)
}