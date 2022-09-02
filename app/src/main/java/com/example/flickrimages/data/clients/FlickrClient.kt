package com.example.flickrimages.data.clients

import com.example.flickrimages.data.clients.utils.RequestRetrier
import com.example.flickrimages.model.FlickrPhoto
import com.example.flickrimages.model.FlickrResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class FlickrClient @Inject constructor(
    private val ktorClient: HttpClient,
    private val requestRetrier: RequestRetrier
) {

    suspend fun getImages(tags: List<String>): List<FlickrPhoto> {
        return requestRetrier.performRequest {
                ktorClient.get(FLICKR_API_URL) {
                    parameter(PARAMETER_FORMAT_KEY, PARAMETER_FORMAT_VALUE)
                    parameter(PARAMETER_NO_JS_CALLBACK_KEY, PARAMETER_NO_JS_CALLBACK_VALUE)
                    if (tags.isNotEmpty()) {
                        parameter(PARAMETER_TAGS_KEY, tags.joinToString(separator = TAGS_URL_SEPARATOR))
                    }
                }
            }?.body<FlickrResponse>()?.items ?: emptyList()
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