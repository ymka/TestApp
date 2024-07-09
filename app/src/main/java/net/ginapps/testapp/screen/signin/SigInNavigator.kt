package net.ginapps.testapp.screen.signin

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.ginapps.testapp.core.BaseActivity
import net.ginapps.testapp.core.MainCoroutineContext
import net.ginapps.testapp.core.launch
import net.ginapps.testapp.screen.home.HomeActivity

@Composable
fun SignInNavHost(navController: NavHostController, navigator: NavHostSigInNavigator) {
    NavHost(
        navController = navController,
        startDestination = DestinationName.SIGN_IN,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(DestinationName.SIGN_IN) { SigInScreen() }
    }

    navigator.destinationListener = { destination ->
        if (destination == SignInDestination.Home) {
            (navController.context as? BaseActivity)?.let {
                it.startActivity(Intent(it.applicationContext, HomeActivity::class.java))
            }
        } else {
            navController.navigate(destination.name)
        }
    }

    navigator.goBackListener = { navController.popBackStack() }
}

sealed class SignInDestination(val name: String) {

    object SigIn : SignInDestination(DestinationName.SIGN_IN)
    object Home : SignInDestination(DestinationName.HOME)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignInDestination

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

private object DestinationName {
    const val HOME = "account"
    const val SIGN_IN = "sig_in"
}

interface SigInNavigator {
    fun navigateTo(destination: SignInDestination)
    fun goBack()
}

class NavHostSigInNavigator(private val mainContext: MainCoroutineContext) : SigInNavigator {

    var destinationListener: (SignInDestination) -> Unit = {}
    var goBackListener: () -> Unit = {}

    override fun navigateTo(destination: SignInDestination) {
        mainContext.launch { destinationListener(destination) }
    }

    override fun goBack() {
        mainContext.launch { goBackListener() }
    }
}
