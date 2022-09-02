package com.example.flickrimages.model

import kotlinx.serialization.Serializable

@Serializable
data class FlickrResponse(
    val items: List<FlickrPhoto>
)
