package com.example.flickrimages.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickrimages.use_cases.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainPhotosScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase
): ViewModel() {
    init {
        viewModelScope.launch {
            getImagesUseCase()
        }
    }
}