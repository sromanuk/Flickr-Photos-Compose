package com.example.flickrimages.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flickrimages.R
import com.example.flickrimages.ui.screens.destinations.DetailsImageScreenDestination
import com.example.flickrimages.ui.screens.main.MainPhotosScreenAction
import com.example.flickrimages.ui.screens.main.MainPhotosScreenState
import com.example.flickrimages.ui.screens.main.MainPhotosScreenViewModel
import com.example.flickrimages.ui.screens.main.ViewSelection
import com.example.flickrimages.ui.screens.widgets.PhotoCard
import com.example.flickrimages.ui.screens.widgets.SearchBar
import com.example.flickrimages.ui.theme.spacing
import com.example.flickrimages.ui.utils.DefaultScreenCanvas
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainPhotosScreenContent(state = MainPhotosScreenState(), actioner = {}, navigatorAction = {})
}

@RootNavGraph(start = true)
@Destination
@Composable
fun MainPhotosScreen(
    viewModel: MainPhotosScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.currentState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            Toast.makeText(
                context,
                effect.errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val navigateToDetailsWithID: (Int) -> Unit = {
        navigator.navigate(DetailsImageScreenDestination(it))
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
                when {
                    state.photos.isEmpty() -> {
                        if (state.isRefreshing.not() && state.isLoading.not()) {
                            Text(stringResource(id = R.string.no_photos))
                        }
                    }

                    state.viewSelector == ViewSelection.List -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.photos) { photo ->
                                photo.mediumSizePhotoURL?.let {
                                    actioner(
                                        MainPhotosScreenAction.GetBitmap(
                                            photoID = photo.imageID,
                                            url = it
                                        )
                                    )
                                }

                                PhotoCard(photo = photo, state = state, navigatorAction = navigatorAction)
                            }
                        }
                    }

                    state.viewSelector == ViewSelection.Grid -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = 2),
                        ) {
                            itemsIndexed(state.photos) { index, photo ->
                                photo.mediumSizePhotoURL?.let {
                                    actioner(
                                        MainPhotosScreenAction.GetBitmap(
                                            photoID = photo.imageID,
                                            url = it
                                        )
                                    )
                                }
                                val modifier = if (index % 2 == 0) {
                                    Modifier.padding(end = spacing.tiny)
                                } else {
                                    Modifier.padding(start = spacing.tiny)
                                }
                                Box(modifier = modifier) {
                                    PhotoCard(photo = photo, state = state, navigatorAction = navigatorAction)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
