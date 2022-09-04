package com.example.flickrimages.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flickrimages.ui.screens.main.MainPhotosScreenViewModel
import com.example.flickrimages.ui.utils.DefaultScreenCanvas
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun MainPhotosScreen(viewModel: MainPhotosScreenViewModel = hiltViewModel()) {
    DefaultScreenCanvas {
        Text(text = "Hello!")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    FlickrImagesTheme {
//        Greeting()
//    }
}
