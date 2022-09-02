package com.example.flickrimages.ui.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.flickrimages.ui.theme.FlickrImagesTheme

@Composable
fun DefaultScreenCanvas(content: @Composable () -> Unit) {
    FlickrImagesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
