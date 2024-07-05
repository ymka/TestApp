package net.ginapps.testapp.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import net.ginapps.testapp.AppError
import net.ginapps.testapp.repository.ConnectionRepository
import timber.log.Timber

abstract class BaseViewModel(
    private val connectionRepository: ConnectionRepository,
    private val ioCoroutineContext: IoCoroutineContext,
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "Error happened in view model")
        handleExceptionAndStopCtaLoading(AppError.Unexpected(exception))
    }
    private var _error = MutableSharedFlow<AppError?>()
    val error: SharedFlow<AppError?> =
        _error.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    private val _ctaLoading = MutableStateFlow(false)
    val ctaLoading: StateFlow<Boolean> = _ctaLoading

    open fun launch() {
        runOnlyOnce {
            launchOnMain {
                connectionRepository.connectionState.collect {
                    it?.let { showError(it) }
                }
            }
        }
    }

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

    fun launchOnIo(
        exceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job = ioCoroutineContext.launch(exceptionHandler ?: this.exceptionHandler) { block() }

    suspend fun <T> executeOnIo(
        exceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> T,
    ) = ioCoroutineContext.execute(exceptionHandler ?: this.exceptionHandler) { block() }

    fun showError(e: AppError) {
        launchOnMain { _error.emit(e) }
    }

    fun hideError() {
        launchOnMain { _error.emit(null) }
    }

    private fun handleExceptionAndStopCtaLoading(e: AppError) {
        _ctaLoading.value = false
        showError(e)
        handleException(e)
    }

    // Override to handle exception happened in coroutine launched on view model scope
    protected open fun handleException(e: AppError) {}
}
