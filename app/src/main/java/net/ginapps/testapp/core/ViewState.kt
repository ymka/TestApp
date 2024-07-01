package net.ginapps.testapp.core

sealed class ViewState<out T> {
    data object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(val exception: Throwable) : ViewState<Nothing>()
}
