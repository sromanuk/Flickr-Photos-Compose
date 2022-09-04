package com.example.flickrimages.ui.screens.main

sealed class MainPhotosScreenAction {
    object Refresh: MainPhotosScreenAction()
    data class SearchTags(val tagsString: String): MainPhotosScreenAction()
    object ClearTags: MainPhotosScreenAction()
    data class ViewSelectionChanged(val selector: ViewSelection): MainPhotosScreenAction()
    data class GetBitmap(val photoID: Int, val url: String): MainPhotosScreenAction()
}

enum class ViewSelection {
    List,
    Grid
}