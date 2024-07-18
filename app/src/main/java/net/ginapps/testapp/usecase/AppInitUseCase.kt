package net.ginapps.testapp.usecase

import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.presets.Web3ModalChainsPresets
import net.ginapps.testapp.repository.UserRepository

interface AppInitUseCase {
    suspend fun run()
}

class DefaultAppInitUseCase(
    private val delegate: Web3Modal.ModalDelegate,
    private val userRepository: UserRepository,
) : AppInitUseCase {
    override suspend fun run() {
        Web3ModalChainsPresets.ethChains["1"]?.let { chain ->
            val requiredMethods = chain.requiredMethods.toMutableList()
            requiredMethods.add("eth_sign")
            Web3Modal.setChains(listOf(chain.copy(requiredMethods = requiredMethods)))
        }

        Web3Modal.setDelegate(delegate)
        userRepository.fetchData()
    }
}
