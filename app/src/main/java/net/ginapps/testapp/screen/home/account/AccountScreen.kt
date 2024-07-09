package net.ginapps.testapp.screen.home.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.ginapps.testapp.R
import net.ginapps.testapp.core.view.BaseScreen

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    BaseScreen(
        viewModel = viewModel,
        errorState = viewModel.error
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.wallet_address,
                        viewModel.user.collectAsState().value.address
                    )
                )
                Button(
                    onClick = { viewModel.logOut() },
                    enabled = !viewModel.ctaLoading.collectAsState().value
                ) {
                    Text(text = stringResource(id = R.string.logout))
                }
            }
        }

        if (viewModel.ctaLoading.collectAsState().value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
