package net.ginapps.testapp.core

import kotlinx.coroutines.flow.StateFlow

abstract class StateViewModel<T> : BaseViewModel() {

    abstract val state: StateFlow<ViewState<T>>

    fun onSuccessState(call: (T) -> Unit) {
        val value = state.value
        if (value is ViewState.Success<T>) {
            call(value.data)
        }
    }
}
