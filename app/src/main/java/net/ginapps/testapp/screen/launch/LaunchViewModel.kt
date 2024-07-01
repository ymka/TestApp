package net.ginapps.testapp.screen.launch

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.ginapps.testapp.core.StateViewModel
import net.ginapps.testapp.core.ViewState
import net.ginapps.testapp.core.runOnlyOnce

class LaunchViewModel : StateViewModel<Route>() {

    private val _state = MutableStateFlow<ViewState<Route>>(ViewState.Loading)

    override val state: StateFlow<ViewState<Route>> = _state

    val launch = runOnlyOnce {
        launchOnMain {
            delay(1000)
            _state.value = ViewState.Success(Route.Sign)
        }
    }

}

sealed class Route {
    data object Sign : Route()
    data object Home : Route()
}
