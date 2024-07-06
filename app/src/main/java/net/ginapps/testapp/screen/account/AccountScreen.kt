package net.ginapps.testapp.screen.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = stringResource(
                    id = R.string.wallet_address,
                    viewModel.address.collectAsState().value
                )
            )
            Button(onClick = { viewModel.logOut() }) {
                Text(text = stringResource(id = R.string.logout))
            }
        }
    }
}
