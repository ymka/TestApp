package net.ginapps.testapp.usecase

import com.walletconnect.util.bytesToHex
import com.walletconnect.util.randomBytes
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import net.ginapps.testapp.AppError
import net.ginapps.testapp.data.UserAccount
import net.ginapps.testapp.repository.UserRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import net.ginapps.testapp.core.Result

interface SIWEUseCase {
    suspend fun run(): Result<Unit>
}

class Web3ModalSIWEUseCase(private val userRepository: UserRepository) : SIWEUseCase {

    override suspend fun run(): Result<Unit> {
        val result = suspendCoroutine { continuation ->
            (userRepository.account.value as? UserAccount.Authorized)?.let { account ->
                val authParams = Modal.Params.Authenticate(
                    chains = account.chains,
                    domain = "sample.kotlin.dapp",
                    uri = "https://web3inbox.com/all-apps",
                    nonce = randomBytes(12).bytesToHex(),
                    statement = "Sign in with wallet of my awesome app.",
                    exp = null,
                    nbf = null,
                    methods = account.methods,
                    expiry = null
                )
                Web3Modal.authenticate(
                    authParams,
                    onSuccess = {
                        // TODO: store the string in local repository if needed
                        continuation.resume(Result.Success(Unit))
                    }, onError = {
                        continuation.resume(Result.Error(AppError.Unauthorized(it.throwable)))
                    })
            } ?: run {
                continuation.resume(Result.Error(AppError.Unauthorized(IllegalStateException("User is not authorized"))))
            }
        }

        if (result is Result.Error) {
            // TODO: Think about error case during log out
            userRepository.logOut()
        }

        return result
    }
}
