package com.example.flickrimages.ui.screens.main

import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.example.flickrimages.ui.utils.BaseViewModel
import com.example.flickrimages.ui.utils.UserError
import com.example.flickrimages.ui.utils.toUserError
import com.example.flickrimages.use_cases.GetImageByteArrayUseCase
import com.example.flickrimages.use_cases.GetImagesUseCase
import com.example.flickrimages.use_cases.GetImagesWithTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@HiltViewModel
class MainPhotosScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val getImagesWithTagsUseCase: GetImagesWithTagsUseCase,
    private val getImageByteArrayUseCase: GetImageByteArrayUseCase
): BaseViewModel<MainPhotosScreenState, MainPhotosScreenAction, UserError>(MainPhotosScreenState()) {

    private val searchFlow = MutableStateFlow(NO_TAGS_SELECTED)
    private var fetchingScope: CoroutineScope? = null

    init {
        restartImageDownloadsScope()

        getInitialImages()

        viewModelScope.launch {
            searchFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect {
                    if (it.isBlank()) {
                        getInitialImages()
                    } else {
                        searchWithTags(it)
                    }
                }
        }
    }

    override suspend fun onAction(action: MainPhotosScreenAction) {
        when (action) {
            MainPhotosScreenAction.Refresh -> {
                restartImageDownloadsScope()
                refreshImages()
            }
            is MainPhotosScreenAction.ViewSelectionChanged -> {
                setState { copy(viewSelector = action.selector) }
            }
            is MainPhotosScreenAction.SearchTags -> {
                restartImageDownloadsScope()
                setState {
                    copy(tagsSearched = action.tagsString)
                }
                searchFlow.emit(action.tagsString)
            }
            MainPhotosScreenAction.ClearTags -> {
                restartImageDownloadsScope()
                setState { copy(tagsSearched = NO_TAGS_SELECTED) }
                searchFlow.emit(NO_TAGS_SELECTED)
            }
            is MainPhotosScreenAction.GetBitmap -> {
                getImageBitmap(action.photoID, action.url)
            }
        }
    }

    private fun getInitialImages() {
        fetchingScope?.launch {
            try {
                val images = performLoadingOperation {
                    getImagesUseCase()
                }

                setState { copy(photos = images) }
            } catch (e: Exception) {
                emitEffect(e.toUserError())
            }
        }
    }

    private fun searchWithTags(tags: String) {
        fetchingScope?.launch {
            val listOfTags = tags.replace("[^A-Za-z\\d ]".toRegex(), "").split(" ")
            try {
                val images = performRefreshOperation {
                    getImagesWithTagsUseCase(listOfTags)
                }

                setState { copy(photos = images) }
            } catch (e: Exception) {
                emitEffect(e.toUserError())
            }
        }
    }

    private fun refreshImages() {
        searchFlow.value.takeIf { it.isNotBlank() }?.also { searchWithTags(it) }
            ?: getInitialImages()
    }

    private fun getImageBitmap(id: Int, url: String) {
        fetchingScope?.launch {
            delay(Random.nextDouble().seconds)

            var retryCounter = 0

            do {
                delay((retryCounter * 0.5).seconds)

                val byteArray = getImageByteArrayUseCase(url)
                val isSuccess = if (byteArray != null) {
                    currentValue.bitmapsMap[id] = BitmapFactory.decodeStream(byteArray.inputStream())
                    setState { copy(dummyUpdateCounter = dummyUpdateCounter + 1) }
                    true
                } else {
                    false
                }

                retryCounter++
            } while (retryCounter < MAX_RETRY && isSuccess.not())
        }
    }

    private fun restartImageDownloadsScope() {
        fetchingScope?.cancel()
        fetchingScope = CoroutineScope(Job() + Dispatchers.IO)

        setState { copy(photos = emptyList(), bitmapsMap = HashMap(), dummyUpdateCounter = 0) }
    }

    private companion object {
        const val NO_TAGS_SELECTED = ""
        const val MAX_RETRY = 8
    }
}