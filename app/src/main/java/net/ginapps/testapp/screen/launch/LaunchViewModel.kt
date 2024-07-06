package net.ginapps.testapp.screen.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.StateViewModel
import net.ginapps.testapp.core.ViewState
import net.ginapps.testapp.data.UserAccount
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.usecase.AppInitUseCase

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val initUseCase: AppInitUseCase,
    private val userRepository: UserRepository,
    connectionRepository: ConnectionRepository,
    ioCoroutineContext: IoCoroutineContext,
) : StateViewModel<Route>(connectionRepository, ioCoroutineContext) {

    private val _state = MutableStateFlow<ViewState<Route>>(ViewState.Loading)

    override val state: StateFlow<ViewState<Route>> = _state

    override fun launch() {
        super.launch()
        launchOnMain {
            executeOnIo { initUseCase.run() }
            userRepository.account.collect {
                val route = when (it) {
                    is UserAccount.Authorized -> Route.Home
                    is UserAccount.None -> Route.Sign
                }

                _state.value = ViewState.Success(route)
            }
        }
    }

}

sealed class Route {
    data object Sign : Route()
    data object Home : Route()
}
