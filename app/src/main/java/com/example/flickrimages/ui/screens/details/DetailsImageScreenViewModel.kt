package com.example.flickrimages.ui.screens.details

import androidx.lifecycle.viewModelScope
import com.example.flickrimages.ui.utils.BaseViewModel
import com.example.flickrimages.ui.utils.UserError
import com.example.flickrimages.use_cases.GetImageByIDUseCase
import com.example.flickrimages.use_cases.GetMaximumSizeImageBitmapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsImageScreenViewModel @Inject constructor(
    private val getImageByIDUseCase: GetImageByIDUseCase,
    private val getMaximumSizeImageBitmapUSeCase: GetMaximumSizeImageBitmapUseCase
): BaseViewModel<DetailsImageScreenState, Unit, UserError>(DetailsImageScreenState()) {

    private var imageID: Int? = null

    fun setImageID(id: Int) {
        if (imageID != null) return

        imageID = id

        val flickrPhoto = getImageByIDUseCase(id)

        setState { copy(photoObject = flickrPhoto) }

        flickrPhoto?.imageID?.let { imageID ->
            viewModelScope.launch {
                val bitmap = getMaximumSizeImageBitmapUSeCase(imageID)
                setState { copy(bitmap =  bitmap) }
            }
        }
    }
}