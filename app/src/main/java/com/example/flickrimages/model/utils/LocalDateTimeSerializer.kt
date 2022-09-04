package com.example.flickrimages.model.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXXXX"

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale.US)
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale.US)
        encoder.encodeString(formatter.format(value))
    }
}