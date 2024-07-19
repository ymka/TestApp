package net.ginapps.testapp.data

sealed class UserAccount {
    data class Authorized(
        val address: String,
    ) : UserAccount()

    data object SIWEWaiting : UserAccount()

    data object None : UserAccount()
}
