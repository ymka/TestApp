package net.ginapps.testapp.usecase

import com.walletconnect.util.bytesToHex
import com.walletconnect.util.randomBytes
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.client.models.request.Request
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.ginapps.testapp.AppError
import net.ginapps.testapp.core.Result
import net.ginapps.testapp.data.UserAccount
import net.ginapps.testapp.repository.UserRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
            }
        }

        if (result is Result.Error) {
            // TODO: Think about error case during log out
            userRepository.logOut()
        }

        return result
    }
}

class Web3ModalSIWERequestUseCase(
    private val userRepository: UserRepository,
) : SIWEUseCase {
    override suspend fun run(): Result<Unit> {
        val result = suspendCancellableCoroutine { continuation ->
            (userRepository.account.value as? UserAccount.Authorized)?.let { _ ->
                val account = Web3Modal.getAccount()
                if (account == null) {
                    continuation.resume(Result.Error(AppError.Unauthorized(IllegalStateException())))
                    return@suspendCancellableCoroutine
                }

                val param = EthSignTypedData(
                    domain = "my.awesome.app.test",
                    address = account.address,
                    uri = "https://my.awesome.app.test",
                    version = 1,
                    chainId = account.chain.id,
                    nonce = randomBytes(12).bytesToHex(),
                    issuedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                ).toParams()

                val request = Request(
                    method = "eth_sign",
                    params = param
                )

                Web3Modal.request(request, onSuccess = { _ ->
                    if (continuation.isActive) {
                        continuation.resume(Result.Success(Unit))
                    }
                },
                    onError = {
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(AppError.Unauthorized(it)))
                        }
                    })

            }
        }

        if (result is Result.Error) {
            // TODO: Think about error case during log out
            userRepository.logOut()
        }

        return Result.Success(Unit)
    }

}

private class EthSignTypedData(
    val domain: String,
    val address: String,
    val uri: String,
    val version: Int,
    val chainId: String,
    val nonce: String,
    val issuedAt: LocalDate,
) {

    fun toParams(): String {
        val msg = """
            $domain wants you to sign in with your Ethereum account:
            $address
            
            URI: $uri
            Version: $version
            Chain ID: $chainId
            Nonce: $nonce
            Issued At: $issuedAt
        """
        return "[\"$address\",\"$msg\"]"
    }
}
