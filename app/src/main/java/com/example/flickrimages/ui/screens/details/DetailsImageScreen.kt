package com.example.flickrimages.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun DetailsImageScreen(
    imageID: Int,
    viewModel: DetailsImageScreenViewModel = hiltViewModel()
) {

}