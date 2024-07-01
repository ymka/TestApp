package net.ginapps.testapp.screen.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import net.ginapps.testapp.R
import net.ginapps.testapp.core.view.BaseScreen

@Composable
fun SigInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
) {
    BaseScreen(
        errorState = viewModel.error.collectAsState()
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier.padding(),
                onClick = {
                }) {
                Text(text = stringResource(id = R.string.sig_in_screen_cta))
            }
        }
    }
}
