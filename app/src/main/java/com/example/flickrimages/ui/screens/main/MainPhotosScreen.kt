package com.example.flickrimages.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flickrimages.R
import com.example.flickrimages.model.FlickrPhoto
import com.example.flickrimages.ui.screens.main.MainPhotosScreenAction
import com.example.flickrimages.ui.screens.main.MainPhotosScreenState
import com.example.flickrimages.ui.screens.main.MainPhotosScreenViewModel
import com.example.flickrimages.ui.screens.main.ViewSelection
import com.example.flickrimages.ui.theme.spacing
import com.example.flickrimages.ui.utils.DefaultScreenCanvas
import com.example.flickrimages.ui.widgets.SearchBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun MainPhotosScreen(
    viewModel: MainPhotosScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.currentState.collectAsState()

    val navigateToDetailsWithID: (Int) -> Unit = {
//        navigator.navigate()
    }

    MainPhotosScreenContent(state = state, viewModel::submitAction, navigateToDetailsWithID)
}

@Composable
fun MainPhotosScreenContent(
    state: MainPhotosScreenState,
    actioner: (MainPhotosScreenAction) -> Unit,
    navigatorAction: (Int) -> Unit
) {
    DefaultScreenCanvas {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = spacing.small, horizontal = spacing.mediumSmall)
        ) {
            SearchBar(
                modifier = Modifier.padding(bottom = spacing.medium),
                input = state.tagsSearched,
                output = { actioner(MainPhotosScreenAction.SearchTags(it)) },
                onClear = { actioner(MainPhotosScreenAction.ClearTags) },
                showSelector = true,
                selectorState = state.viewSelector == ViewSelection.Grid,
                selectorChangeAction = {
                    actioner(
                        MainPhotosScreenAction.ViewSelectionChanged(
                            if (it) ViewSelection.Grid else ViewSelection.List
                        )
                    )
                }
            )

            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                onRefresh = { actioner(MainPhotosScreenAction.Refresh) }
            ) {
                Log.d("MainPhotosScreen", ">>> image counter is ${state.photos.count()}")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    state.photos.forEach { photo ->
                        photo.mediumSizePhotoURL?.let {
                            actioner(
                                MainPhotosScreenAction.GetBitmap(
                                    photoID = photo.imageID,
                                    url = it
                                )
                            )
                        }
                        PhotoCard(photo = photo, state = state)
                    }
                }

//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(state.photos) { photo ->
//                        photo.mediumSizePhotoURL?.let {
//                            actioner(
//                                MainPhotosScreenAction.GetBitmap(
//                                    photoID = photo.imageID,
//                                    url = it
//                                )
//                            )
//                        }
//                        PhotoCard(photo = photo, state = state)
//                    }
//                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainPhotosScreenContent(state = MainPhotosScreenState(), actioner = {}, navigatorAction = {})
}

@Composable
private fun PhotoCard(photo: FlickrPhoto, state: MainPhotosScreenState) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(vertical = spacing.small)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(spacing.medium).fillMaxWidth()
        ) {

            val bitmap = state.bitmapsMap[photo.imageID]
            if (bitmap == null) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = spacing.tall))
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = spacing.tiny),
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = photo.title,
                    contentScale = ContentScale.FillWidth,
                )
            }
            
            Text(
                text = photo.title.takeIf { it.isNotBlank() } ?: stringResource(id = R.string.no_title)
            )
        }
    }
}
