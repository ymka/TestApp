package net.ginapps.testapp.repository

sealed class SignInState {
    data object Init : SignInState()
    data object SessionApproved : SignInState()
    class SessionRejected(val reason: String) : SignInState()
    data class Error(val e: Throwable) : SignInState()
    data class SessionRequestResponse(val result: RequestResponseResult) : SignInState()
}

sealed class RequestResponseResult(val requestId: Long) {
    class SignMsgApproved(val signature: String, requestId: Long) : RequestResponseResult(requestId)
    class SignMsgRejected(requestId: Long) : RequestResponseResult(requestId)
}
