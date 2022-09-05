package com.example.flickrimages.ui.screens.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.flickrimages.ui.utils.BaseViewModel
import com.example.flickrimages.ui.utils.UserError
import com.example.flickrimages.ui.utils.toUserError
import com.example.flickrimages.use_cases.DataForDownload
import com.example.flickrimages.use_cases.GetImagesBitmapsUseCase
import com.example.flickrimages.use_cases.GetImagesUseCase
import com.example.flickrimages.use_cases.GetImagesWithTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MainPhotosScreenViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val getImagesWithTagsUseCase: GetImagesWithTagsUseCase,
    private val getImagesBitmapsUseCase: GetImagesBitmapsUseCase
): BaseViewModel<MainPhotosScreenState, MainPhotosScreenAction, UserError>(MainPhotosScreenState()) {

    private val searchFlow = MutableStateFlow(NO_TAGS_SELECTED)
    private var fetchingScope: CoroutineScope? = null
    private var imagesChannel: Channel<DataForDownload>? = null

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

        viewModelScope.launch {
            getImagesBitmapsUseCase.resultFlow.collectLatest {
                Log.d("MainPhotosScreenViewModel", ">>> recieved bitmap for ${it.imageIndex}")
                currentStateValue.bitmapsMap[it.imageIndex] = it.bitmap
                setState { copy(dummyUpdateCounter = dummyUpdateCounter + 1) }
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
        if (currentStateValue.bitmapsMap[id] == null) {
            fetchingScope?.launch {
                imagesChannel?.send(DataForDownload(id, url))
            }
        }

        // TODO: async way does not work because of Flickr's limitation on requests. Need to move to sync way
//        fetchingScope?.launch {
//            delay(Random.nextDouble().seconds)
//
//            var retryCounter = 0
//
//            do {
//                delay((retryCounter * 0.5).seconds)
//
//                val byteArray = getImageByteArrayUseCase(url)
//                val isSuccess = if (byteArray != null) {
//                    currentValue.bitmapsMap[id] = BitmapFactory.decodeStream(byteArray.inputStream())
//                    setState { copy(dummyUpdateCounter = dummyUpdateCounter + 1) }
//                    true
//                } else {
//                    false
//                }
//
//                retryCounter++
//            } while (retryCounter < MAX_RETRY && isSuccess.not())
//
//            if (currentValue.bitmapsMap[id] == null) {
//                emitEffect(UserError("Could not download image #$id"))
//            }
//        }
    }

    private fun restartImageDownloadsScope() {
        fetchingScope?.cancel()
        fetchingScope = CoroutineScope(Job() + Dispatchers.IO)

        imagesChannel?.cancel()
        imagesChannel = Channel()

        fetchingScope?.launch {
            imagesChannel?.let {
                getImagesBitmapsUseCase(it)
            }
        }

        setState { copy(photos = emptyList(), bitmapsMap = HashMap(), dummyUpdateCounter = 0) }
    }

    private companion object {
        const val NO_TAGS_SELECTED = ""
    }
}