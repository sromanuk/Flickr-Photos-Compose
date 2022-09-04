package com.example.flickrimages.ui.screens.details

import com.example.flickrimages.ui.utils.BaseViewModel
import com.example.flickrimages.ui.utils.UserError
import com.example.flickrimages.use_cases.GetImageByteArrayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsImageScreenViewModel @Inject constructor(
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase
): BaseViewModel<DetailsImageScreenState, Unit, UserError>(DetailsImageScreenState()) {
}