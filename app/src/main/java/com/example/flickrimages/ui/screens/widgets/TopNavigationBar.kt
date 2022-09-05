package com.example.flickrimages.ui.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flickrimages.R

@Composable
fun TopNavigationBar(
    onBack: (() -> Unit)? = null,
    title: String,
    isSubScreen: Boolean,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .height(75.dp)
            .background(color = MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // left button/back
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isSubScreen) {
                BackButton {
                    onBack?.invoke()
                }
            }
        }

        // Title
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).width(250.dp),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                // shift text baseline down a bit to center with icon
                // by default it's offset a bit up
                baselineShift = BaselineShift(-0.1f)
            ),
            maxLines = 1
        )

        // right button/action
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            trailingContent?.invoke()
        }
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(
        onClick = onBack
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back_button),
            contentDescription = "Back button",
        )
    }
}