package net.ginapps.testapp.screen.signin

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import net.ginapps.testapp.core.BaseViewModel
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.onError
import net.ginapps.testapp.core.onSuccess
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.Web3Repository
import net.ginapps.testapp.repository.Web3State
import net.ginapps.testapp.usecase.SIWEUseCase

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val web3Repository: Web3Repository,
    private val sIWEUseCase: SIWEUseCase,
    private val navigator: SigInNavigator,
    connectionRepository: ConnectionRepository,
    ioContext: IoCoroutineContext,
) : BaseViewModel(connectionRepository, ioContext) {

    private val _sessionRejected = MutableStateFlow(false)
    val sessionRejected = _sessionRejected

    override fun launch() {
        super.launch()
        launchOnMain {
            web3Repository.state.collectLatest {
                when (it) {
                    is Web3State.SessionApproved -> {
                        launchOnMainWithLoading {
                            sIWEUseCase.run()
                                .onSuccess {
                                    navigator.navigateTo(SignInDestination.Home)
                                }
                                .onError { error ->
                                    showError(error)
                                }
                        }
                    }

                    is Web3State.SessionRejected -> {
                        _sessionRejected.value = true
                    }
                }
            }
        }
    }

    fun confirmRejection() {
        _sessionRejected.value = false
    }

}
