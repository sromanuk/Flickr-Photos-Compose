package com.example.flickrimages.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration

open class BaseViewModel<STATE : State, ACTION, EFFECT>(initialState: STATE) : ViewModel() {

    private val innerState = MutableStateFlow(initialState)
    val currentState = innerState as StateFlow<STATE>

    protected val currentStateValue
        get() = innerState.value

    private val actionSink = MutableSharedFlow<ACTION>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val effectSink = MutableSharedFlow<EFFECT>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Strategy ensures that any values emitted before the initial subscribe are replayed, but
    // none are replayed after that. This means that the View being recreated and re-subscribing
    // wont cause Actions or Effects to repeat.

    private val actions = actionSink.shareIn(viewModelScope, SharingStarted.Lazily)

    val effects = effectSink.shareIn(viewModelScope, SharingStarted.Lazily)

    init {
        viewModelScope.launch {
            actions.collect {
                onAction(it)
            }
        }
    }

    protected open suspend fun onAction(action: ACTION) {
    }

    fun submitAction(action: ACTION) {
        actionSink.tryEmit(action)
    }

    protected fun emitEffect(effect: EFFECT) {
        effectSink.tryEmit(effect)
    }

    fun setState(reducer: STATE.() -> STATE) {
        innerState.update(reducer)
    }

    protected suspend fun <T> performLoadingOperation(operation: suspend () -> T): T {
        setState { updateCommonState(loading = true) as STATE }
        val operationResult = operation()
        setState { updateCommonState(loading = false) as STATE }

        return operationResult
    }

    protected suspend fun <T> performRefreshOperation(
        dismissRefreshStateTimeout: Duration? = null,
        operation: suspend () -> T
    ): T {
        setState { updateCommonState(refreshing = true) as STATE }
        val operationResult = operation()

        if (dismissRefreshStateTimeout != null) {
            delay(dismissRefreshStateTimeout)
        }

        setState { updateCommonState(refreshing = false) as STATE }

        return operationResult
    }
}

abstract class State(
    open val isLoading: Boolean = false,
    open val isRefreshing: Boolean = false
) {
    abstract fun updateCommonState(loading: Boolean = isLoading, refreshing: Boolean = isRefreshing): State
}

data class UserError(val errorMessage: String)
