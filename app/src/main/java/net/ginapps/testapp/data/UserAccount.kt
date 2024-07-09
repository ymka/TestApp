package net.ginapps.testapp.data

sealed class UserAccount {
    data class Authorized(
        val address: String,
        val pairingTopic: String,
        val chains: List<String>,
        val methods: List<String>,
    ) : UserAccount()

    data object None : UserAccount()
}
