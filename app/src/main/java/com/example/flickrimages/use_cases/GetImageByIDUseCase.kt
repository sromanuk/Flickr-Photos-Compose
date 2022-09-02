package com.example.flickrimages.use_cases

import com.example.flickrimages.data.repositories.FlickrRepository
import javax.inject.Inject

class GetImageByIDUseCase @Inject constructor(private val flickrRepository: FlickrRepository) {
    operator fun invoke(imageID: Int) = flickrRepository.getImageByID(imageID)
}