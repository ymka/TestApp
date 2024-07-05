package net.ginapps.testapp.core.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.SharedFlow
import net.ginapps.testapp.AppError

@Composable
fun BaseScreen(
    errorState: SharedFlow<AppError?>,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    ErrorSnackbarManager(snackBarHostState, errorState)
    Scaffold(
        snackbarHost = { ErrorSnackbar(hostState = snackBarHostState) },
        topBar = topBar,
    ) {
        content(it)
    }
}
