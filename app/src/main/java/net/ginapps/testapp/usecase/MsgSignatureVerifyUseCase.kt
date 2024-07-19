package net.ginapps.testapp.usecase

import com.walletconnect.android.cacao.signature.SignatureType
import com.walletconnect.android.internal.common.model.ProjectId
import com.walletconnect.android.internal.common.signing.message.MessageSignatureVerifier
import com.walletconnect.web3.modal.client.Web3Modal
import net.ginapps.testapp.BuildConfig

interface MsgSignatureVerifyUseCase {
    suspend fun run(signature: String, msg: String): Boolean
}

class WebMsgSignatureVerifyUseCase : MsgSignatureVerifyUseCase {
    override suspend fun run(signature: String, msg: String): Boolean =
        Web3Modal.getAccount()?.let {
            MessageSignatureVerifier(projectId = ProjectId(BuildConfig.project_id))
                .verify(signature, msg, it.address, SignatureType.EIP191)
        } ?: false
}
