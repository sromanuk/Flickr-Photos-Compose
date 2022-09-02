package com.example.flickrimages.data.repositories

import com.example.flickrimages.data.clients.FlickrClient
import com.example.flickrimages.model.FlickrPhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(private val flickrClient: FlickrClient) {

    private var imageStorage: HashMap<Int, FlickrPhoto> = HashMap()

    suspend fun getImages() = getImagesWithTags()

    suspend fun getImagesWithTags(tags: List<String> = emptyList()) =
        flickrClient.getImages(tags).also {
            imageStorage.clear()

            for ((index, image) in it.withIndex()) {
                imageStorage[index] = image.apply { imageID = index }
            }
        }

    fun getImageByID(index: Int) = if (index in 0 until imageStorage.keys.size) {
        imageStorage[index]
    } else {
        null
    }
}