package net.ginapps.testapp.screen.home.account

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.ginapps.testapp.core.BaseViewModel
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.onError
import net.ginapps.testapp.core.onSuccess
import net.ginapps.testapp.data.UserAccount
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.screen.home.HomeDestination
import net.ginapps.testapp.screen.home.HomeNavigator
import net.ginapps.testapp.usecase.LogOutUseCase

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val logOutUseCase: LogOutUseCase,
    private val navigator: HomeNavigator,
    connectionRepository: ConnectionRepository,
    ioCoroutineContext: IoCoroutineContext,
) : BaseViewModel(connectionRepository, ioCoroutineContext) {
    private val _user = MutableStateFlow(UserAccount.Authorized(""))
    val user = _user.asStateFlow()

    override fun launch() {
        super.launch()
        launchOnMain {
            userRepository.account.collect {
                when (it) {
                    is UserAccount.Authorized -> {
                        _user.value = it
                    }

                    is UserAccount.None, UserAccount.SIWEWaiting -> {
                        navigator.navigateTo(HomeDestination.SigIn)
                    }
                }
            }
        }
    }

    fun logOut() {
        launchOnMainWithLoading {
            executeOnIo { logOutUseCase.run() }
                .onSuccess {
                    navigator.navigateTo(HomeDestination.SigIn)
                }
                .onError {
                    showError(it)
                }
        }
    }
}
