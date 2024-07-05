package net.ginapps.testapp.core

import kotlinx.coroutines.flow.StateFlow
import net.ginapps.testapp.repository.ConnectionRepository

abstract class StateViewModel<T>(
    connectionRepository: ConnectionRepository,
    ioCoroutineContext: IoCoroutineContext
) : BaseViewModel(connectionRepository, ioCoroutineContext) {

    abstract val state: StateFlow<ViewState<T>>

    fun onSuccessState(call: (T) -> Unit) {
        val value = state.value
        if (value is ViewState.Success<T>) {
            call(value.data)
        }
    }
}
