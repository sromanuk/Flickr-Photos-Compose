package com.example.flickrimages.use_cases

import com.example.flickrimages.data.repositories.FlickrRepository
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(private val flickrRepository: FlickrRepository) {
    suspend operator fun invoke() = flickrRepository.getImages()
}