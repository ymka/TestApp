package net.ginapps.testapp.repository

import android.content.SharedPreferences
import com.walletconnect.android.internal.common.exception.NoInternetConnectionException
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.launch
import net.ginapps.testapp.data.Method
import net.ginapps.testapp.data.SIWEData
import timber.log.Timber

interface SignInRepository {
    val sigInState: StateFlow<SignInState>
    var lastSentMessage: SIWEData?
    fun resetState()
}

interface ConnectionRepository {
    val connectionState: SharedFlow<AppError.NoInternet?>
}

class Web3InRepository(
    private val userRepository: UserRepository,
    private val ioCoroutineContext: IoCoroutineContext,
    private val sharedPreferences: SharedPreferences,
) : SignInRepository, ConnectionRepository, Web3Modal.ModalDelegate {
    private val _sigInState = MutableStateFlow<SignInState>(SignInState.Init)
    override val sigInState = _sigInState.asStateFlow()

    private val _connectionState = MutableSharedFlow<AppError.NoInternet?>()
    override val connectionState: SharedFlow<AppError.NoInternet?> = _connectionState

    override var lastSentMessage: SIWEData?
        get()  {

            return SIWEData(sharedPreferences.getString(LAST_SENT_MESSAGE, null) ?: "",
                sharedPreferences.getLong(LAST_SENT_REQUEST_ID, -1))
        }
        set(value) {
            sharedPreferences.edit().also {
                it.putString(LAST_SENT_MESSAGE, value?.value)
                it.putLong(LAST_SENT_REQUEST_ID, value?.requestId ?: -1)
            }.apply()
        }

    override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
        Timber.i("onConnectionStateChange: $state")
        val error = if (state.isAvailable) null else AppError.NoInternet
        ioCoroutineContext.launch {
            _connectionState.emit(error)
        }
    }

    override fun onError(error: Modal.Model.Error) {
        Timber.i("onError: $error")
        if (error.throwable is NoInternetConnectionException) {
            ioCoroutineContext.launch {
                _connectionState.emit(AppError.NoInternet)
            }
        } else {
            _sigInState.value = SignInState.Error(error.throwable)
        }
    }

    override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
        Timber.i("onProposalExpired: $proposal  ")
    }

    override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
        Timber.i("onRequestExpired: $request")
    }

    override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
        Timber.i("onSessionApproved: $approvedSession")
        ioCoroutineContext.launch {
            userRepository.fetchData()
            _sigInState.value = SignInState.SessionApproved
        }
    }

    override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
        Timber.i("onSessionDelete: $deletedSession")
    }

    @Deprecated(
        "Use onSessionEvent(Modal.Model.Event) instead. Using both will result in duplicate events.",
        replaceWith = ReplaceWith("onEvent(event)")
    )
    override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
        Timber.i("onSessionEvent: $sessionEvent")
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.Event) {
        Timber.i("onSessionEvent: $sessionEvent")
    }

    override fun onSessionExtend(session: Modal.Model.Session) {
        Timber.i("onSessionExtend: $session")
    }

    override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
        Timber.i("onSessionRejected: $rejectedSession")
        _sigInState.value = SignInState.SessionRejected(rejectedSession.reason)
    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        Timber.i("onSessionRequestResponse: $response")
        if (response.method == Method.PersonalSign.name) {
            val responseRes = when (val result = response.result) {
                is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                    RequestResponseResult.SignMsgApproved(
                        result.result, result.id
                    )
                }

                is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                    RequestResponseResult.SignMsgRejected(result.id)
                }
            }

            _sigInState.value = SignInState.SessionRequestResponse(responseRes)
        }
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        Timber.i("onSessionUpdate: $updatedSession")
    }

    override fun resetState() {
        _sigInState.value = SignInState.Init
    }

    private companion object {
        const val LAST_SENT_MESSAGE = "LAST_SENT_MESSAGE"
        const val LAST_SENT_REQUEST_ID = "LAST_SENT_REQUEST_ID"
    }
}
