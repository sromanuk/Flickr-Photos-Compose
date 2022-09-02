package com.example.flickrimages.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.String.Companion

@Serializable
data class FlickrResponse(
    val items: List<FlickrPhoto>
)

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
    val pictureTags = tagsString.split(TAGS_DELIMITER)

    val mediumSizePhotoURL = media[MEDIUM_FILE_SIZE]

    val fullSizePhotoURL = mediumSizePhotoURL?.replace(MEDIUM_FILE_SUFFIX, ".")

    private companion object {
        const val MEDIUM_FILE_SIZE = "m"
        const val MEDIUM_FILE_SUFFIX = "_m."
        const val TAGS_DELIMITER = " "
    }
}

internal object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("LocalDateTimeType", Companion.serializer().descriptor)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return decoder.decodeString().toLocalDateTime()
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
}