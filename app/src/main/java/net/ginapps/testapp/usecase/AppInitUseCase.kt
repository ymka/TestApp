package net.ginapps.testapp.usecase

import com.walletconnect.web3.modal.client.Web3Modal

interface AppInitUseCase {
    suspend fun run()
}

class DefaultAppInitUseCase(
    private val delegate: Web3Modal.ModalDelegate
) : AppInitUseCase {
    override suspend fun run() {
        Web3Modal.setDelegate(delegate)
    }
}
