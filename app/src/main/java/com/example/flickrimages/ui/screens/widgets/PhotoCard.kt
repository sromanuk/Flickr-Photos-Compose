package com.example.flickrimages.ui.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flickrimages.R
import com.example.flickrimages.model.FlickrPhoto
import com.example.flickrimages.ui.screens.main.MainPhotosScreenState
import com.example.flickrimages.ui.theme.spacing

@Composable
fun PhotoCard(
    photo: FlickrPhoto,
    state: MainPhotosScreenState,
    navigatorAction: (Int) -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(spacing.small)
        .clickable { navigatorAction(photo.imageID) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(spacing.medium)
                .fillMaxWidth()
        ) {
            val bitmap = state.bitmapsMap[photo.imageID]

            if (bitmap == null) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = spacing.tall))
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.tiny)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                )
            }

            Text(
                modifier = Modifier.defaultMinSize(minHeight = 20.dp),
                text = photo.title.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.no_title),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}