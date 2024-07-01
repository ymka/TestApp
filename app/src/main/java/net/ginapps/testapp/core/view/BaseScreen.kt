package net.ginapps.testapp.core.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
fun BaseScreen(
    // TODO: handle states
    errorState: State<Throwable?>,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topBar,
    ) {
        content(it)
    }
}
