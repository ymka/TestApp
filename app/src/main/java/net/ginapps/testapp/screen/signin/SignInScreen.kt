package net.ginapps.testapp.screen.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.walletconnect.web3.modal.ui.components.internal.Web3ModalComponent
import kotlinx.coroutines.launch
import net.ginapps.testapp.R
import net.ginapps.testapp.core.view.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
) {
    BaseScreen(
        viewModel = viewModel,
        errorState = viewModel.error
    ) { innerPadding ->
        var openBottomSheet by rememberSaveable { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        if (viewModel.sessionRejected.collectAsState().value) {
            openBottomSheet = false
            AlertDialog(
                onDismissRequest = {
                    viewModel.confirmRejection()
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.confirmRejection()
                        }
                    ) {
                        Text(stringResource(id = R.string.ok))
                    }
                },
                title = {
                    Text(stringResource(id = R.string.session_rejected))
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier.padding(),
                onClick = {
                    viewModel.signIn()
                },
                enabled = !viewModel.signInLoading.collectAsState().value
            ) {
                Text(text = stringResource(id = R.string.sig_in_screen_cta))
            }

            if (viewModel.signInLoading.collectAsState().value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.waiting_siwe_approve))
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (viewModel.showBottomSheet.collectAsState().value) {
            // TODO: update sheet height when content changed
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        viewModel.closeBottomSheet()
                    }
                },
                content = {
                    Web3ModalComponent(
                        shouldOpenChooseNetwork = false,
                        closeModal = {
                            viewModel.closeBottomSheet()
                        }
                    )
                }
            )
        }
    }
}
