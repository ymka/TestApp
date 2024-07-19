package net.ginapps.testapp.usecase

import com.walletconnect.util.bytesToHex
import com.walletconnect.util.randomBytes
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.client.models.request.Request
import com.walletconnect.web3.modal.client.models.request.SentRequestResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.ginapps.testapp.AppError
import net.ginapps.testapp.BuildConfig
import net.ginapps.testapp.core.Result
import net.ginapps.testapp.data.Method
import net.ginapps.testapp.data.SIWEData
import kotlin.coroutines.resume

interface SIWERequestUseCase {
    suspend fun run(): Result<SIWEData>
}

class Web3ModalSIWERequestUseCase : SIWERequestUseCase {
    override suspend fun run(): Result<SIWEData> {
        try {
            val result = withTimeout(5000L) {
                suspendCancellableCoroutine { continuation ->
                    val account = Web3Modal.getAccount()
                    if (account == null) {
                        continuation.resume(Result.Error(AppError.Unauthorized(IllegalStateException())))
                        return@suspendCancellableCoroutine
                    }

                    val message = EthSignTypedData(
                        domain = BuildConfig.domain,
                        address = account.address,
                        uri = "https://my.awesome.app.test",
                        version = 1,
                        chainId = account.chain.id,
                        nonce = randomBytes(12).bytesToHex(),
                        issuedAt = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    ).toMessage()


                    val request = Request(
                        method = Method.PersonalSign.name,
                        params = "[\"${account.address}\",\"$message\"]"
                    )

                    Web3Modal.request(
                        request,
                        onSuccess = { response ->
                            if (continuation.isActive) {
                                val requestId = (response as? SentRequestResult.WalletConnect)?.let {
                                    it.requestId
                                } ?: -1
                                continuation.resume(Result.Success(SIWEData(message, requestId)))
                            }
                        },
                        onError = {
                            if (continuation.isActive) {
                                continuation.resume(Result.Error(AppError.Unauthorized(it)))
                            }
                        })
                }
            }

            return result
        } catch (e: TimeoutCancellationException) {
            return Result.Error(AppError.Unexpected(e))
        }
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

    fun toMessage(): String = "$domain wants you to sign in with your Ethereum account: $address " +
            "URI: $uri Version: $version Chain ID: $chainId Nonce: $nonce Issued At: $issuedAt"
}
