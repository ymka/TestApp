package net.ginapps.testapp.data

sealed class Chain(val name: String) {
    data object Eth : Chain("eip155")
}
