package net.ginapps.testapp

import android.app.Application
import com.walletconnect.android.BuildConfig
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.presets.Web3ModalChainsPresets
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: use different tress for debug and release
        Timber.plant(Timber.DebugTree())
        initWeb3()
    }

    private fun initWeb3() {
        val projectId = BuildConfig.PROJECT_ID
        val relayUrl = "relay.walletconnect.com"
        val serverUrl = "wss://$relayUrl?projectId=$projectId"
        val connectionType = ConnectionType.AUTOMATIC
        val appMetaData = Core.Model.AppMetaData(
            name = "My Test Wallet",
            description = "Some wallet description",
            url = "kotlin.wallet.walletconnect.com",
            icons = listOf("https://raw.githubusercontent.com/WalletConnect/walletconnect-assets/master/Icon/Gradient/Icon.png"),
            redirect = "kotlin-web3modal://request"
        )

        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            metaData = appMetaData
        ) {
            Timber.e(it.throwable)
        }

        Web3Modal.initialize(Modal.Params.Init(CoreClient)) { error ->
            Timber.e(error.throwable)
        }
        Web3Modal.setChains(Web3ModalChainsPresets.ethChains.values.toList())
    }
}
