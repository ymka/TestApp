package net.ginapps.testapp.screen.home

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
import net.ginapps.testapp.screen.home.account.AccountScreen
import net.ginapps.testapp.screen.signin.SignInActivity

@Composable
fun HomeNavHost(navController: NavHostController, navigator: NavHostHomeNavigator) {
    NavHost(
        navController = navController,
        startDestination = DestinationName.ACCOUNT,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(DestinationName.ACCOUNT) { AccountScreen() }
    }

    navigator.destinationListener = { destination ->
        if (destination == HomeDestination.SigIn) {
            (navController.context as? BaseActivity)?.let {
                it.startActivity(Intent(it.applicationContext, SignInActivity::class.java))
                it.finish()
            }
        } else {
            navController.navigate(destination.name)
        }
    }

    navigator.goBackListener = { navController.popBackStack() }
}

sealed class HomeDestination(val name: String) {

    object Account : HomeDestination(DestinationName.ACCOUNT)
    object SigIn : HomeDestination(DestinationName.SIGN_IN)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeDestination

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

object DestinationName {
    const val ACCOUNT = "account"
    const val SIGN_IN = "sig_in"
}

interface HomeNavigator {
    fun navigateTo(destination: HomeDestination)
    fun goBack()
}

class NavHostHomeNavigator(private val mainContext: MainCoroutineContext) : HomeNavigator {

    var destinationListener: (HomeDestination) -> Unit = {}
    var goBackListener: () -> Unit = {}

    override fun navigateTo(destination: HomeDestination) {
        mainContext.launch { destinationListener(destination) }
    }

    override fun goBack() {
        mainContext.launch { goBackListener() }
    }
}
