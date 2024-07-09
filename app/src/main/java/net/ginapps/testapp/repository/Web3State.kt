package net.ginapps.testapp.repository

sealed class Web3State {
    data object SessionApproved : Web3State()
    class SessionRejected(val reason: String) : Web3State()
}
