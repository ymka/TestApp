package net.ginapps.testapp.screen.signin

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.BaseViewModel
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.Result
import net.ginapps.testapp.core.onError
import net.ginapps.testapp.core.onSuccess
import net.ginapps.testapp.data.UserAccount
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.RequestResponseResult
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.repository.SignInState
import net.ginapps.testapp.repository.SignInRepository
import net.ginapps.testapp.usecase.CompleteSignInUseCase
import net.ginapps.testapp.usecase.LogOutUseCase
import net.ginapps.testapp.usecase.MsgSignatureVerifyUseCase
import net.ginapps.testapp.usecase.SIWERequestUseCase
import timber.log.Timber

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val userRepository: UserRepository,
    private val logOutUseCase: LogOutUseCase,
    private val sIWERequestUseCase: SIWERequestUseCase,
    private val msgSignatureVerifyUseCase: MsgSignatureVerifyUseCase,
    private val completeSignInUseCase: CompleteSignInUseCase,
    private val navigator: SigInNavigator,
    connectionRepository: ConnectionRepository,
    ioContext: IoCoroutineContext,
) : BaseViewModel(connectionRepository, ioContext) {

    private val _sessionRejected = MutableStateFlow(false)
    val sessionRejected = _sessionRejected.asStateFlow()
    private val _signInLoading = MutableStateFlow(false)
    val signInLoading = _signInLoading.asStateFlow()
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    override fun launch() {
        super.launch()
        launchOnMain {
            signInRepository.sigInState.collectLatest {
                when (it) {
                    is SignInState.SessionApproved -> {
                        sendSIWERequest()
                    }

                    is SignInState.SessionRequestResponse -> {
                        handleSessionRequestResponse(it.result)
                    }

                    is SignInState.SessionRejected -> {
                        _signInLoading.value = false
                        _sessionRejected.value = true
                    }

                    is SignInState.Error -> {
                        forceLogout()
                        _sessionRejected.value = true
                        _signInLoading.value = false
                    }

                    is SignInState.Init -> {
                        // do nothing
                    }
                }
            }
        }
    }

    fun signIn() {
        if (userRepository.account.value is UserAccount.None) {
            _showBottomSheet.value = true
        } else {
            launchOnMain {
                forceLogout()
                _showBottomSheet.value = true
            }
        }
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    private suspend fun sendSIWERequest() {
        _signInLoading.value = true
        executeOnIo {
            sIWERequestUseCase.run()
                .onSuccess { msg ->
                    launchOnMain {
                        signInRepository.lastSentMessage = msg
                    }
                }
                .onError { _ ->
                    _sessionRejected.value = true
                }
        }
    }

    private fun handleSessionRequestResponse(result: RequestResponseResult) {
        val lastMsg = signInRepository.lastSentMessage
        if (result.requestId == lastMsg?.requestId) {
            when (result) {
                is RequestResponseResult.SignMsgApproved -> {
                    launchOnMain {
                        val verified = executeOnIo {
                            msgSignatureVerifyUseCase.run(
                                result.signature,
                                lastMsg.value,
                            )
                        }

                        if (verified) {
                            executeOnIo { completeSignInUseCase.run() }
                            navigator.navigateTo(SignInDestination.Home)
                        } else {
                            forceLogout()
                            _signInLoading.value = false
                            showError(AppError.Unauthorized(IllegalStateException("Signature verification failed")))
                        }
                    }
                }

                is RequestResponseResult.SignMsgRejected -> {
                    _signInLoading.value = false
                    _sessionRejected.value = true
                }
            }
        }
    }

    private suspend fun forceLogout() {
        executeOnIo {
            var res = logOutUseCase.run()
            var counter = 5
            while (res is Result.Error && counter > 0) {
                counter--
                delay(500)
                res = logOutUseCase.run()
            }
        }
    }

    fun confirmRejection() {
        _sessionRejected.value = false
        _signInLoading.value = false
    }

}
