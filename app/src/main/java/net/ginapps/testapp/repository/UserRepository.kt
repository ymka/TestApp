package net.ginapps.testapp.repository

import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.web3.modal.client.models.Session
import com.walletconnect.web3.modal.client.Web3Modal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.Result
import net.ginapps.testapp.core.execute
import net.ginapps.testapp.data.Chain
import net.ginapps.testapp.data.UserAccount
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserRepository {
    val account: StateFlow<UserAccount>
    suspend fun fetchData()
    suspend fun logOut(): Result<Unit>
}

class Web3UserRepository(
    private val io: IoCoroutineContext
) : UserRepository {

    private val _account = MutableStateFlow<UserAccount>(UserAccount.None)
    override val account: StateFlow<UserAccount> = _account

    override suspend fun fetchData() {
        io.execute {
            val web3Account = Web3Modal.getAccount()
            val session = Web3Modal.getSession() as? Session.WalletConnectSession
            if (web3Account != null && session != null) {
                val (chains, methods) = session.namespaces[Chain.Eth.name]?.let {
                    (it.chains ?: emptyList()) to it.methods
                } ?: (emptyList<String>() to emptyList())
                _account.value = UserAccount.Authorized(
                    web3Account.address,
                    session.pairingTopic,
                    chains,
                    methods
                )
            }
        }
    }

    override suspend fun logOut(): Result<Unit> = io.execute {
        if (Web3Modal.getSession() != null && Web3Modal.getAccount() != null) {
            (Web3Modal.getSession() as? Session.WalletConnectSession)?.let {
                CoreClient.Pairing.disconnect(Core.Params.Disconnect(it.pairingTopic))
            }

            suspendCoroutine<Result<Unit>> {
                Web3Modal.disconnect(
                    onSuccess = {
                        _account.value = UserAccount.None
                        it.resume(Result.Success(Unit))
                    },
                    onError = { e: Throwable ->
                        it.resume(Result.Error(AppError.LogOut(e)))
                    }
                )
            }
        } else {
            _account.value = UserAccount.None
            Result.Success(Unit)
        }
    }
}
