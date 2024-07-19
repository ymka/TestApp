package net.ginapps.testapp.repository

import com.walletconnect.web3.modal.client.Web3Modal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.execute
import net.ginapps.testapp.data.UserAccount

interface UserRepository {
    val account: StateFlow<UserAccount>
    suspend fun fetchData()
}

class Web3UserRepository(
    private val settingsRepository: SettingsRepository,
    private val io: IoCoroutineContext
) : UserRepository {

    private val _account = MutableStateFlow<UserAccount>(UserAccount.None)
    override val account: StateFlow<UserAccount> = _account

    override suspend fun fetchData() {
        io.execute {
            val web3Account = Web3Modal.getAccount()
            when {
                settingsRepository.signInCompleted && web3Account != null -> {
                    _account.value = UserAccount.Authorized(web3Account.address)
                }

                web3Account != null -> {
                    _account.value = UserAccount.SIWEWaiting
                }
                else -> {
                    _account.value = UserAccount.None
                }
            }
        }
    }
}
