package net.ginapps.testapp.core.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import net.ginapps.testapp.AppError
import net.ginapps.testapp.R

@Composable
fun ErrorSnackbar(hostState: SnackbarHostState) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { snackbarData: SnackbarData ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Gray)
                ) {
                    Text(
                        text = snackbarData.visuals.message,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    )
}

@Composable
fun ErrorSnackbarManager(
    snackBarHostState: SnackbarHostState,
    exception: SharedFlow<AppError?>
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        exception.collect { error ->
            error?.let {
                val message = getErrorText(context, it)
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(message)
            } ?: run { snackBarHostState.currentSnackbarData?.dismiss() }
        }
    }
}

private fun getErrorText(context: Context, error: AppError): String {
    val stringId = when (error) {
        is AppError.NoInternet -> R.string.no_internet_error_message
        is AppError.Unexpected -> R.string.common_error_message
        is AppError.SignMessageFailed -> R.string.sign_msg_error_message
        is AppError.LogOut -> R.string.common_error_message
        is AppError.Unauthorized -> R.string.unauthorized_error_message
    }

    return context.getString(stringId)
}

