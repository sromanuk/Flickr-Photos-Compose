package com.example.flickrimages.model

import com.example.flickrimages.model.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FlickrPhoto(
    val title: String,
    val description: String,
    val link: String,
    val media: Map<String, String>,
    @Serializable(LocalDateTimeSerializer::class)
    val date_taken: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class)
    val published: LocalDateTime,
    val author: String,
    val author_is: String,
    @SerialName("tags")
    val tagsString: String
) {
    @Transient
    var imageID: Int = 0

    @Transient
    val pictureTags = tagsString.split(TAGS_DELIMITER)

    @Transient
    val mediumSizePhotoURL = media[MEDIUM_FILE_SIZE]

    @Transient
    val fullSizePhotoURL = mediumSizePhotoURL?.replace(MEDIUM_FILE_SUFFIX, ".")

    private companion object {
        const val MEDIUM_FILE_SIZE = "m"
        const val MEDIUM_FILE_SUFFIX = "_m."
        const val TAGS_DELIMITER = " "
    }
}
