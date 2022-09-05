package com.example.flickrimages.ui.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flickrimages.R
import com.example.flickrimages.ui.screens.widgets.ScreenWithTopNavigation
import com.example.flickrimages.ui.theme.Gray
import com.example.flickrimages.ui.theme.spacing
import com.example.flickrimages.ui.utils.DefaultScreenCanvas
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.format.DateTimeFormatter
import java.util.*


@Preview(showBackground = true)
@Composable
fun DetailsImageScreenPreview() {
    DetailsImageScreenContent(DetailsImageScreenState())
}

@Destination
@Composable
fun DetailsImageScreen(
    imageID: Int,
    viewModel: DetailsImageScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.currentState.collectAsState()

    viewModel.setImageID(imageID)

    DetailsImageScreenContent(state, navigator::navigateUp)
}

@Composable
private fun DetailsImageScreenContent(state: DetailsImageScreenState, backAction: () -> Unit = {}) {
    DefaultScreenCanvas {
        ScreenWithTopNavigation(
            onBack = backAction,
            title = state.photoObject?.title.orEmpty(),
            isSubScreen = true,
            trailingContent = null
        ) {
            val mediumSpacing = spacing.medium
            val localUriHandler = LocalUriHandler.current

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium)
            ) {
                val (author, createdAt, publishedAt, description, image, tags, link) = createRefs()

                val dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale.US)

                Text(
                    modifier = Modifier
                        .padding(vertical = spacing.small)
                        .constrainAs(createdAt) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredWrapContent
                        },
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = stringResource(id = R.string.created_at),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
                            )
                        )
                        append(" ")
                        append(
                            AnnotatedString(
                                text = state.photoObject?.date_taken?.format(dateFormatter)
                                    .orEmpty(),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = Gray
                                )
                            )
                        )
                    }
                )

                Text(
                    modifier = Modifier
                        .padding(vertical = spacing.small)
                        .constrainAs(publishedAt) {
                            top.linkTo(createdAt.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredWrapContent
                        },
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = stringResource(id = R.string.published_at),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
                            )
                        )
                        append(" ")
                        append(
                            AnnotatedString(
                                text = state.photoObject?.published?.format(dateFormatter)
                                    .orEmpty(),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = Gray
                                )
                            )
                        )
                    }
                )

                Text(
                    modifier = Modifier
                        .padding(vertical = spacing.small)
                        .constrainAs(author) {
                            top.linkTo(publishedAt.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredWrapContent
                        },
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = stringResource(id = R.string.author_info),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
                            )
                        )
                        append(" ")
                        append(
                            AnnotatedString(
                                text = state.photoObject?.author?.format(dateFormatter).orEmpty(),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = Gray
                                )
                            )
                        )
                    }
                )

                val photoLink = state.photoObject?.link.orEmpty()
                Button(
                    modifier = Modifier
                        .padding(spacing.medium)
                        .constrainAs(link) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                    enabled = photoLink.isNotBlank(),
                    onClick = {
                        localUriHandler.openUri(photoLink)
                    }
                ) {
                    Text(text = stringResource(id = R.string.go_to_flickr))
                }

                Text(
                    modifier = Modifier
                        .padding(vertical = spacing.small)
                        .constrainAs(tags) {
                            bottom.linkTo(link.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredWrapContent
                        },
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = stringResource(id = R.string.tags_list),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
                            )
                        )
                        append(" ")
                        append(
                            AnnotatedString(
                                text = state.photoObject?.pictureTags?.joinToString(", ").orEmpty(),
                                spanStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = Gray
                                )
                            )
                        )
                    },
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier
                        .padding(vertical = spacing.small)
                        .constrainAs(description) {
                            bottom.linkTo(tags.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredWrapContent
                        },
                    text = state.photoObject?.description.orEmpty(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4
                )

                val imageModifier = Modifier.constrainAs(image) {
                    top.linkTo(author.bottom, margin = mediumSpacing)
                    bottom.linkTo(description.top, margin = mediumSpacing)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    if (state.bitmap != null) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                }

                if (state.bitmap != null) {
                    val scale = remember { mutableStateOf(1.0f) }

                    Image(
                        painter = BitmapPainter(state.bitmap.asImageBitmap()),
                        contentDescription = null,
                        contentScale = if (state.bitmap.height > state.bitmap.width) {
                            ContentScale.FillHeight
                        } else {
                            ContentScale.FillWidth
                        },
                        modifier = imageModifier
//                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale.value,
                                scaleY = scale.value,
                            )
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    scale.value = when {
                                        scale.value < 0.5f -> 0.5f
                                        scale.value > 3f -> 3f
                                        else -> scale.value * zoom
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                forEachGesture {
                                    awaitPointerEventScope {
                                        awaitFirstDown(requireUnconsumed = false)
                                        do {
                                            val event = awaitPointerEvent()
                                            val canceled = event.changes.any { it.isConsumed }
                                        } while (!canceled && event.changes.any { it.pressed })

                                        scale.value = 1.0f
                                    }
                                }
                            },
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = imageModifier
                            .padding(vertical = spacing.tall)
                            .height(75.dp)
                            .width(75.dp)
                    )
                }
            }
        }
    }
}

private const val DATE_FORMAT_PATTERN =  "dd/MM/yyyy HH:mm:ss"