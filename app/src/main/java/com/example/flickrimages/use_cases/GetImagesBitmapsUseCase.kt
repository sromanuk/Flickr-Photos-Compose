package com.example.flickrimages.use_cases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class GetImagesBitmapsUseCase @Inject constructor(
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase
) {
    private val downloadedImagesList = Vector<Int>(30)
    private val _resultFlow = MutableSharedFlow<DownloadResult>()
    val resultFlow = _resultFlow as Flow<DownloadResult>

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(imagesChannel: ReceiveChannel<DataForDownload>) {
        downloadedImagesList.clear()

        while (!imagesChannel.isClosedForReceive) {
            if (imagesChannel.isEmpty) {
                delay(1.seconds)
            } else {
                imagesChannel.tryReceive().getOrNull()?.let { dataForDownload ->
                    if (downloadedImagesList.contains(dataForDownload.imageID).not()) {
                        downloadedImagesList.add(dataForDownload.imageID)

                        delay(1.seconds)

                        val byteArray = getImageByteArrayUseCase(dataForDownload.imageURL)
                        byteArray?.inputStream()?.let {
                            val bitmap = BitmapFactory.decodeStream(byteArray.inputStream())
                            _resultFlow.emit(DownloadResult(dataForDownload.imageID, bitmap))
                        }
                    }
                }
            }
        }
    }
}

data class DataForDownload(
    val imageID: Int,
    val imageURL: String
)

data class DownloadResult(
    val imageIndex: Int,
    val bitmap: Bitmap?
)
