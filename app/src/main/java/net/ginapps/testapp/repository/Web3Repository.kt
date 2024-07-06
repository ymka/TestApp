package net.ginapps.testapp.repository

import com.walletconnect.android.internal.common.exception.NoInternetConnectionException
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.launch
import timber.log.Timber

interface Web3Repository {
    val state: SharedFlow<Web3State>
}

interface ConnectionRepository {
    val connectionState: SharedFlow<AppError.NoInternet?>
}

class DefaultWeb3Repository(
    private val ioCoroutineContext: IoCoroutineContext
) : Web3Repository, ConnectionRepository, Web3Modal.ModalDelegate {
    private val _state = MutableSharedFlow<Web3State>()
    override val state: SharedFlow<Web3State> = _state

    private val _connectionState = MutableSharedFlow<AppError.NoInternet?>()
    override val connectionState: SharedFlow<AppError.NoInternet?> = _connectionState

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
        ioCoroutineContext.launch {
            _state.emit(Web3State.SessionRejected(rejectedSession.reason))
        }
    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        Timber.i("onSessionRequestResponse: $response")
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        Timber.i("onSessionUpdate: $updatedSession")
    }
}
