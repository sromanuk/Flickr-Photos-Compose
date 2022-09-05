package com.example.flickrimages.data.clients

import android.util.Log
import com.example.flickrimages.data.clients.utils.RequestRetrier
import com.example.flickrimages.model.FlickrPhoto
import com.example.flickrimages.model.FlickrResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class FlickrClient @Inject constructor(
    private val requestRetrier: RequestRetrier
) {

    suspend fun getImages(tags: List<String>): List<FlickrPhoto> {
        return requestRetrier.performRequest { ktorClient ->
                ktorClient.get(FLICKR_API_URL) {
                    parameter(PARAMETER_FORMAT_KEY, PARAMETER_FORMAT_VALUE)
                    parameter(PARAMETER_NO_JS_CALLBACK_KEY, PARAMETER_NO_JS_CALLBACK_VALUE)
                    if (tags.isNotEmpty()) {
                        parameter(PARAMETER_TAGS_KEY, tags.joinToString(separator = TAGS_URL_SEPARATOR))
                    }
                }
            }?.body<FlickrResponse>()?.items ?: emptyList()
    }

    suspend fun getImageDataFromURL(url: String): ByteArray? {
        val body =  requestRetrier.performRequest(initialDelay = Random.nextDouble().seconds) { ktorClient ->
            ktorClient.get(url)
        }?.body<ByteArray>()
        Log.d("FlickrClient", ">>> body received, ${body?.size}")
        return body
    }

    private companion object {
        const val FLICKR_API_URL = "https://api.flickr.com/services/feeds/photos_public.gne"

        const val PARAMETER_FORMAT_KEY = "format"
        const val PARAMETER_FORMAT_VALUE = "json"

        const val PARAMETER_NO_JS_CALLBACK_KEY = "nojsoncallback"
        const val PARAMETER_NO_JS_CALLBACK_VALUE = 1

        const val PARAMETER_TAGS_KEY = "tags"
        const val TAGS_URL_SEPARATOR = ","
    }
}