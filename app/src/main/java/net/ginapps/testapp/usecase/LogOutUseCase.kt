package net.ginapps.testapp.usecase

import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.client.models.Session
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.Result
import net.ginapps.testapp.repository.SettingsRepository
import net.ginapps.testapp.repository.SignInRepository
import net.ginapps.testapp.repository.UserRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface LogOutUseCase {
    suspend fun run(): Result<Unit>
}

class DefaultLogOutUseCase(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val signInRepository: SignInRepository,
): LogOutUseCase {
    override suspend fun run(): Result<Unit> {
        return if (Web3Modal.getAccount() != null) {
            (Web3Modal.getSession() as? Session.WalletConnectSession)?.let {
                CoreClient.Pairing.disconnect(Core.Params.Disconnect(it.pairingTopic))
            }

            val result = suspendCoroutine {
                Web3Modal.disconnect(
                    onSuccess = {
                        it.resume(Result.Success(Unit))
                    },
                    onError = { e: Throwable ->
                        it.resume(Result.Error(AppError.LogOut(e)))
                    }
                )
            }

            if (result is Result.Success) {
                settingsRepository.signInCompleted = false
                updateUserAccount()
            }

            result
        } else {
            updateUserAccount()
            Result.Success(Unit)
        }
    }

    private suspend fun updateUserAccount() {
        signInRepository.resetState()
        userRepository.fetchData()
    }
}
