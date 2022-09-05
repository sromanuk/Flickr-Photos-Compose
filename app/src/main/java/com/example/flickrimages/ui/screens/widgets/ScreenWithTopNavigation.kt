package com.example.flickrimages.ui.screens.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenWithTopNavigation(
    onBack: (() -> Unit)? = null,
    title: String,
    isSubScreen: Boolean,
    trailingContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopNavigationBar(
            onBack = onBack,
            title = title,
            isSubScreen = isSubScreen,
            trailingContent = trailingContent
        )

        Box(modifier = Modifier.wrapContentHeight()) {
            content?.invoke()
        }
    }
}