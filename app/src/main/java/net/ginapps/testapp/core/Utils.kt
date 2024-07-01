package net.ginapps.testapp.core

import androidx.annotation.MainThread

@MainThread
fun runOnlyOnce(call: () -> Unit): () -> Unit {
    var executed = false
    return {
        if (!executed) {
            executed = true
            call()
        }
    }
}
