package com.example.flickrimages.use_cases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class GetMaximumSizeImageBitmapUseCase @Inject constructor(
    private val imageByIDUseCase: GetImageByIDUseCase,
    private val imageByteArrayUseCase: GetImageByteArrayUseCase
) {
    suspend operator fun invoke(imageID: Int): Bitmap? {
        val flickrPhoto = imageByIDUseCase(imageID)

        flickrPhoto?.fullSizePhotoURL?.let {
            val bitmap = retrieveBitmapWithURL(it)
            if (bitmap != null) {
                return bitmap
            }
        }

        flickrPhoto?.mediumSizePhotoURL?.let {
            val bitmap = retrieveBitmapWithURL(it)
            if (bitmap != null) {
                return bitmap
            }
        }

        return null
    }

    private suspend fun retrieveBitmapWithURL(imageURL: String): Bitmap? {
        delay(1.seconds)

        val byteArray = imageByteArrayUseCase(imageURL)
        return byteArray?.inputStream()?.let {
            BitmapFactory.decodeStream(byteArray.inputStream())
        }
    }
}