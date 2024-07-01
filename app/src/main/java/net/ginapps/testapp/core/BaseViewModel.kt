package net.ginapps.testapp.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Error happened in view model")
        handleExceptionAndStopCtaLoading(exception)
    }
    private var _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error
    private val _ctaLoading = MutableStateFlow(false)
    val ctaLoading: StateFlow<Boolean> = _ctaLoading

    // Launch coroutine functions on view model scope and catch all exceptions
    fun launchOnMain(
        exceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job =
        viewModelScope.launch(exceptionHandler ?: this.exceptionHandler) { block() }

    fun launchOnMainWithLoading(
        exceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job =
        viewModelScope.launch(exceptionHandler ?: this.exceptionHandler) {
            _ctaLoading.value = true
            block()
            _ctaLoading.value = false
        }

    fun showError(t: Throwable) {
        _error.value = t
    }

    fun hideError() {
        _error.value = null
    }

    private fun handleExceptionAndStopCtaLoading(t: Throwable) {
        _ctaLoading.value = false
        showError(t)
        handleException(t)
    }

    // Override to handle exception happened in coroutine launched on view model scope
    protected open fun handleException(e: Throwable) {}
}
